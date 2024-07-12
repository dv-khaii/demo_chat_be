package co.jp.xeex.chat.domains.taskmngr.task.get;

import co.jp.xeex.chat.base.PageInfo;
import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.taskmngr.dto.SelectFilterDto;
import co.jp.xeex.chat.domains.taskmngr.dto.TaskDto;
import co.jp.xeex.chat.domains.taskmngr.enums.TaskPriority;
import co.jp.xeex.chat.domains.taskmngr.enums.TaskStatus;
import co.jp.xeex.chat.domains.taskmngr.service.TaskService;
import co.jp.xeex.chat.entity.Task;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatGroupRepository;
import co.jp.xeex.chat.repository.TaskRepository;
import lombok.AllArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * GetTaskServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetTaskServiceImpl extends ServiceBaseImpl<GetTaskRequest, GetTaskResponse>
        implements GetTaskService {

    // Error keys
    private static final String GET_TASK_ERR_PERMISSION_DENIED = "GET_TASK_ERR_PERMISSION_DENIED";

    // DI
    private TaskRepository taskRepo;
    private ChatGroupRepository chatGroupRepo;
    private TaskService taskService;

    @Override
    public GetTaskResponse processRequest(GetTaskRequest in) throws BusinessException {
        // Setting task filter columns
        TaskFilter taskFilter = setTaskFilter(in.filter, in.requestBy);

        // v_long: refactoring create page
        PageInfo page = new PageInfo(in.pageIdx, in.perPage, in.orderFieldList);

        // Get all group id of request user
        List<String> groupIds = chatGroupRepo.getUserGroupIdList(in.requestBy);

        // Check permission user
        if (!StringUtils.isEmpty(in.groupId) && !groupIds.contains(in.groupId)) {
            throw new BusinessException(GET_TASK_ERR_PERMISSION_DENIED, in.lang);
        }

        // Get task data
        Page<Task> pageTasks = taskRepo.getTasksByValue(groupIds,
                in.groupId,
                in.requestBy,
                in.searchText,
                taskFilter,
                page.getPageRequest());
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : pageTasks) {
            TaskDto taskDto = taskService.getTaskDtoByTask(task, in.requestBy, false);
            taskDtos.add(taskDto);
        }

        // Response
        GetTaskResponse response = new GetTaskResponse();
        response.setTaskCount(pageTasks.getTotalElements());
        response.setTaskList(taskDtos);
        return response;
    }

    /**
     * Setting task filter from SelectFilterDto
     * 
     * @param filter    select filter dto by client
     * @param requestBy request by user
     * @return task filter
     */
    private TaskFilter setTaskFilter(SelectFilterDto selectFilterDto, String requestBy) {
        TaskFilter result = new TaskFilter();
        // Setting Status column value filter
        result.setStatus(null);
        if (!selectFilterDto.isDone.equals(selectFilterDto.isNotDone)) {
            TaskStatus taskStatus = Boolean.TRUE.equals(selectFilterDto.isDone) ? TaskStatus.DONE
                    : TaskStatus.IN_PROGRESS;
            result.setStatus(taskStatus.toString());
        }

        // Setting Assigner column value filter
        result.setAssigner(Boolean.TRUE.equals(selectFilterDto.isAssigner) ? requestBy : null);

        // Setting Assignee column value filter
        result.setAssignee(Boolean.TRUE.equals(selectFilterDto.isAssignee) ? requestBy : null);

        // Setting Priority column value filter
        List<String> priorityList = new ArrayList<>();
        String priorityValue;
        priorityValue = Boolean.TRUE.equals(selectFilterDto.isHightPriority) ? TaskPriority.HIGHT.toString() : "";
        priorityList.add(priorityValue);
        priorityValue = Boolean.TRUE.equals(selectFilterDto.isNormalPriority) ? TaskPriority.NORMAL.toString() : "";
        priorityList.add(priorityValue);
        priorityValue = Boolean.TRUE.equals(selectFilterDto.isLowPriority) ? TaskPriority.LOW.toString() : "";
        priorityList.add(priorityValue);

        Integer priorityListSize = priorityList.stream().filter(StringUtils::isNotBlank).toArray().length;
        priorityList = (priorityListSize == 0 || priorityListSize == 3) ? new ArrayList<>(Arrays.asList((String) null))
                : priorityList;
        result.setPriorityList(priorityList);

        // Setting Due date value filter
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
        String now = sdf.format(System.currentTimeMillis());
        result.setDueDate(Boolean.TRUE.equals(selectFilterDto.isDueDate) ? now : null);

        // Setting Over due date value filter
        result.setOverDueDate(Boolean.TRUE.equals(selectFilterDto.isOverDueDate) ? now : null);

        return result;
    }
}
