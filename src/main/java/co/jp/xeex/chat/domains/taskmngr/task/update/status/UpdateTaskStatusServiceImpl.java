package co.jp.xeex.chat.domains.taskmngr.task.update.status;

import org.springframework.stereotype.Service;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.taskmngr.enums.TaskStatus;
import co.jp.xeex.chat.entity.Task;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.TaskRepository;
import co.jp.xeex.chat.util.DateTimeUtil;

/**
 * UpdateTaskStatusServiceImpl
 * 
 * @author q_thinh
 */
@Service
public class UpdateTaskStatusServiceImpl extends ServiceBaseImpl<UpdateTaskStatusRequest, UpdateTaskStatusResponse>
        implements UpdateTaskStatusService {

    // Error keys
    private static final String UPDATE_TASK_STATUS_ERR_TASK_IS_NOT_EXISTED = "UPDATE_TASK_STATUS_ERR_TASK_IS_NOT_EXISTED";
    private static final String UPDATE_TASK_STATUS_ERR_PERMISSION_DENIED = "UPDATE_TASK_STATUS_ERR_PERMISSION_DENIED";

    // DI
    private final TaskRepository taskRepo;

    public UpdateTaskStatusServiceImpl(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    @Override
    public UpdateTaskStatusResponse processRequest(UpdateTaskStatusRequest in) throws BusinessException {
        // Get task
        Task task = taskRepo.findById(in.taskId).orElse(null);
        if (task == null) {
            throw new BusinessException(UPDATE_TASK_STATUS_ERR_TASK_IS_NOT_EXISTED, in.lang);
        }

        // Check permission user
        if (!in.requestBy.equals(task.getCreateBy()) && !in.requestBy.equals(task.getAssignee())) {
            throw new BusinessException(UPDATE_TASK_STATUS_ERR_PERMISSION_DENIED, in.lang);
        }

        //v_long: in case change status from unfinished -> completed
        //needs to update end date (according to current)
        if(in.taskStatus==TaskStatus.DONE){
            task.setDueDate(DateTimeUtil.getCurrentTimestamp());
        }
        //Update priority
        task.setTaskStatus(in.taskStatus);
        taskRepo.saveAndFlush(task);

        // Response
        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setResult(true);
        return response;
    }
}
