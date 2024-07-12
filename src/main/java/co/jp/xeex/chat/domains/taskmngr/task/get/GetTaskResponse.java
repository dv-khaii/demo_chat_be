package co.jp.xeex.chat.domains.taskmngr.task.get;

import java.util.List;

import co.jp.xeex.chat.domains.taskmngr.dto.TaskDto;
import lombok.Data;

/**
 * GetTaskResponse
 * 
 * @author q_thinh
 */
@Data
public class GetTaskResponse {
    /**
     * Represents the total number of tasks.
     */
    private Long taskCount;
    /**
     * Represents a list of tasks.
     */
    private List<TaskDto> taskList;
}
