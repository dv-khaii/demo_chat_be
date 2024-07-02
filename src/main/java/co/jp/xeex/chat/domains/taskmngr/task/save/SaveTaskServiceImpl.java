package co.jp.xeex.chat.domains.taskmngr.task.save;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.chat.ChatAction;
import co.jp.xeex.chat.domains.chat.ChatMessageBroadcastService;
import co.jp.xeex.chat.domains.chat.ChatMessageDto;
import co.jp.xeex.chat.domains.file.dto.FileDto;
import co.jp.xeex.chat.domains.file.enums.FileClassify;
import co.jp.xeex.chat.domains.file.enums.FileType;
import co.jp.xeex.chat.domains.taskmngr.task.dto.TaskDto;
import co.jp.xeex.chat.domains.taskmngr.task.enums.TaskStatus;
import co.jp.xeex.chat.domains.taskmngr.task.mapper.TaskMapper;
import co.jp.xeex.chat.entity.ChatMessage;
import co.jp.xeex.chat.entity.File;
import co.jp.xeex.chat.entity.MessageTask;
import co.jp.xeex.chat.entity.Task;
import co.jp.xeex.chat.entity.TaskFile;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import co.jp.xeex.chat.repository.FileRepository;
import co.jp.xeex.chat.repository.MessageTaskRepository;
import co.jp.xeex.chat.repository.TaskFileRepository;
import co.jp.xeex.chat.repository.TaskRepository;
import co.jp.xeex.chat.repository.UserRepository;
import co.jp.xeex.chat.util.EnvironmentUtil;
import co.jp.xeex.chat.util.FileUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SaveTaskServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class SaveTaskServiceImpl extends ServiceBaseImpl<SaveTaskRequest, SaveTaskResponse>
        implements SaveTaskService {

    // Error keys
    private static final String SAVE_TASK_ERR_USER_IS_NOT_EXISTED = "SAVE_TASK_ERR_USER_IS_NOT_EXISTED";
    private static final String SAVE_TASK_ERR_MESSAGE_TASK_CREATED_FROM_THIS_MESSAGE = "SAVE_TASK_ERR_MESSAGE_TASK_CREATED_FROM_THIS_MESSAGE";
    private static final String SAVE_TASK_ERR_CAN_NOT_EDIT_DONE_TASK = "SAVE_TASK_ERR_CAN_NOT_EDIT_DONE_TASK";
    private static final String SAVE_TASK_ERR_START_DATE_GREATER_THAN_DUE_DATE = "SAVE_TASK_ERR_START_DATE_GREATER_THAN_DUE_DATE";
    private static final String SAVE_TASK_ERR_EXCEEDED_FILE_COUNT = "SAVE_TASK_ERR_EXCEEDED_FILE_COUNT";
    private static final String SAVE_TASK_ERR_PERMISSION_DENIED = "SAVE_TASK_ERR_PERMISSION_DENIED";

    // DI
    private TaskRepository taskRepo;
    private TaskFileRepository taskFileRepo;
    private FileRepository fileRepo;
    private MessageTaskRepository messageTaskRepo;
    private ChatMessageRepository chatMessageRepo;
    private UserRepository userRepo;
    private TaskMapper taskMapper;
    private ChatMessageBroadcastService chatMessageSendService;
    private EnvironmentUtil environmentUtil;

    @Override
    @Transactional
    public SaveTaskResponse processRequest(SaveTaskRequest in) throws BusinessException {
        // Find task and validation data
        Task task = taskRepo.findById(in.taskId).orElse(new Task());

        // Save task
        try {
            boolean isCreateMessageTask = validation(in, task);
            saveTask(in, task, isCreateMessageTask);
        } catch (ParseException e) {
            throw new BusinessException(e.getMessage(), in.lang);
        } catch (IOException ioe) {
            throw new BusinessException(ioe.getMessage(), in.lang);
        }

        // Response
        SaveTaskResponse response = new SaveTaskResponse();
        response.setTask(settingTaskDto(in, task));
        return response;
    }

    /**
     * validation
     * 
     * @param in
     * @param task
     * @return flag detect create message task
     * @throws IOException
     */
    private boolean validation(SaveTaskRequest in, Task task) throws IOException {
        // validation assignee
        if (!StringUtils.isEmpty(in.assignee) && userRepo.findByUserName(in.assignee) == null) {
            throw new BusinessException(SAVE_TASK_ERR_USER_IS_NOT_EXISTED, new String[] { in.assignee }, in.lang);
        }

        // Check date
        if (in.startDate != null && in.dueDate != null && in.startDate.compareTo(in.dueDate) > 0) {
            throw new BusinessException(SAVE_TASK_ERR_START_DATE_GREATER_THAN_DUE_DATE, in.lang);
        }

        // Check max count file save
        if (in.taskFiles.size() > environmentUtil.maxUploadFileCount) {
            // Delete temp files
            Path targetTempPath = Paths.get(environmentUtil.rootUploadPath, AppConstant.PATH_TEMP_PREFIX, in.requestBy);
            List<String> storeNames = in.taskFiles.stream().map(FileDto::getStoreName).collect(Collectors.toList());
            FileUtil.deleteListFile(targetTempPath, storeNames);

            throw new BusinessException(SAVE_TASK_ERR_EXCEEDED_FILE_COUNT,
                    new String[] { environmentUtil.maxUploadFileCount.toString() }, in.lang);
        }

        // Find task
        boolean isCreateMessageTask = false;
        if (StringUtils.isEmpty(task.getId())) {
            // Create task if not exist
            task.initDefault(in.requestBy);

            // Check is exist message task
            isCreateMessageTask = !StringUtils.isEmpty(in.messageId);
            if (isCreateMessageTask && messageTaskRepo.getTaskIdByMessageId(in.messageId) != null) {
                throw new BusinessException(SAVE_TASK_ERR_MESSAGE_TASK_CREATED_FROM_THIS_MESSAGE, in.lang);
            }
        } else {
            // Lock edit DONE status task
            if (TaskStatus.DONE.equals(task.getTaskStatus())) {
                throw new BusinessException(SAVE_TASK_ERR_CAN_NOT_EDIT_DONE_TASK, in.lang);
            }

            // Check permission user
            if (!in.requestBy.equals(task.getCreateBy()) && !in.requestBy.equals(task.getAssignee())) {
                throw new BusinessException(SAVE_TASK_ERR_PERMISSION_DENIED, in.lang);
            }
        }

        return isCreateMessageTask;
    }

    /**
     * saveTask
     * 
     * @param in
     * @param task
     * @param isCreateMessageTask
     * @throws ParseException
     */
    private void saveTask(SaveTaskRequest in, Task task, boolean isCreateMessageTask) throws ParseException {
        // Save startdate
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
        task.setStartDate(null);
        if (!StringUtils.isEmpty(in.startDate)) {
            // Convert date
            Timestamp startDate = new Timestamp(sdf.parse(in.startDate).getTime());
            task.setStartDate(startDate);
        }

        // Save duedate
        task.setDueDate(null);
        if (!StringUtils.isEmpty(in.dueDate)) {
            // Convert date
            Timestamp dueDate = new Timestamp(sdf.parse(in.dueDate).getTime());
            task.setDueDate(dueDate);
        }

        // Save task
        task.setGroupId(StringUtils.EMPTY.equals(in.groupId) ? null : in.groupId);
        task.setTaskName(in.taskName);
        task.setAssignee(in.assignee);
        task.setDescription(in.description);
        task.setTaskStatus(in.taskStatus);
        task.setTaskPriority(in.taskPriority);
        task.setUpdateBy(in.requestBy);
        taskRepo.saveAndFlush(task);

        // Save message task
        if (isCreateMessageTask) {
            MessageTask messageTask = new MessageTask();
            messageTask.initDefault(in.requestBy);
            messageTask.setTaskId(task.getId());
            messageTask.setMessageId(in.messageId);
            messageTaskRepo.saveAndFlush(messageTask);
        }

        // Save and delete task files
        saveDeleteTaskFiles(in, task);
    }

    /**
     * settingTaskDto
     * 
     * @param in
     * @param task
     * @return
     * @throws BusinessException
     */
    private TaskDto settingTaskDto(SaveTaskRequest in, Task task) throws BusinessException {
        // Setting taskDto
        TaskDto taskDto = taskMapper.taskToDto(task);
        taskDto.requestBy = in.requestBy;
        taskDto.setIsLockEdit(false);
        taskDto.setIsOwner(true);
        taskDto.setTaskFiles(in.taskFiles);

        // Setting message
        ChatMessage chatMessage = chatMessageRepo.findByTaskId(task.getId());
        if (chatMessage != null) {
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.messageId = chatMessage.getId();
            chatMessageDto.taskId = task.getId();
            chatMessageDto.groupId = chatMessage.getGroupId();
            chatMessageDto.chatContent = chatMessage.getChatContent();
            chatMessageDto.requestBy = in.requestBy;
            chatMessageDto.lang = in.lang;
            chatMessageDto.action = ChatAction.CREATE_TASK_MESSAGE;
            taskDto.setChatMessage(chatMessageDto);

            // Notify all other user in group
            chatMessageSendService.broadcastMessageToGroup(chatMessageDto);
        }

        return taskDto;
    }

    /**
     * Save and delete task file
     * 
     * @param in
     * @param task
     */
    private void saveDeleteTaskFiles(SaveTaskRequest in, Task task) {
        // Get task files DB
        List<TaskFile> oldTaskFiles = taskFileRepo.findAllByTaskId(task.getId());
        List<String> oldFileIds = oldTaskFiles.stream().map(TaskFile::getFileId).collect(Collectors.toList());

        List<File> oldFiles = fileRepo.findByIdList(oldFileIds);
        List<String> oldStoreNames = oldFiles.stream().map(File::getStoreName).collect(Collectors.toList());

        // Save new files DB
        String domain = environmentUtil.getDomain();
        String targetGroupPath = StringUtils.isEmpty(in.groupId) ? in.requestBy : in.groupId;
        for (FileDto requestFile : in.taskFiles) {
            if (!oldStoreNames.contains(requestFile.getStoreName())) {
                // Add file info
                File file = new File();
                file.initDefault(in.requestBy);
                file.setOriginName(requestFile.getOriginName());
                file.setStoreName(requestFile.getStoreName());
                file.setFileType(FileType.FILE);
                fileRepo.saveAndFlush(file);

                // Add task file info
                TaskFile taskFile = new TaskFile();
                taskFile.initDefault(in.requestBy);
                taskFile.setTaskId(task.getId());
                taskFile.setFileId(file.getId());
                taskFileRepo.saveAndFlush(taskFile);

                try {
                    // Save store file
                    FileUtil.saveTempFile(environmentUtil.rootUploadPath,
                            targetGroupPath,
                            requestFile.getStoreName(),
                            file.getCreateBy(),
                            false);
                } catch (IOException e) {
                    throw new BusinessException(e.getMessage(), in.lang);
                }

                // Set response file url
                String fileUrl = String.format(AppConstant.FILE_URL, domain, AppConstant.PATH_TASK_PREFIX,
                        file.getStoreName());
                requestFile.setDownloadUrl(fileUrl);
            }
        }

        // Delete old files
        try {
            deleteOldFile(in, oldTaskFiles, oldFiles);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage(), in.lang);
        }
    }

    /**
     * Delete old store file
     * 
     * @param in
     * @param oldTaskFiles
     * @param oldFiles
     * @throws IOException
     */
    private void deleteOldFile(SaveTaskRequest in, List<TaskFile> oldTaskFiles, List<File> oldFiles)
            throws IOException {

        // Delete files
        String targetGroupPath = StringUtils.isEmpty(in.groupId) ? in.requestBy : in.groupId;
        List<String> newStoreNames = in.taskFiles.stream().map(FileDto::getStoreName).collect(Collectors.toList());
        for (File oldFile : oldFiles) {
            if (!newStoreNames.contains(oldFile.getStoreName())) {
                // Delete task file
                oldTaskFiles.stream()
                        .filter(item -> item.getFileId().equals(oldFile.getId()))
                        .findFirst()
                        .ifPresent(taskFileRepo::delete);

                // Delete file
                fileRepo.delete(oldFile);

                // Delete store file
                Path targetPath = FileUtil.getTargetPath(environmentUtil.rootUploadPath, FileClassify.TASK.toString(),
                        targetGroupPath, oldFile.getCreateAt());
                targetPath = targetPath.resolve(Paths.get(oldFile.getStoreName()));
                if (Files.exists(targetPath)) {
                    Files.delete(targetPath);
                }
            }
        }
    }
}
