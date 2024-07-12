package co.jp.xeex.chat.domains.taskmngr.mapper;

import co.jp.xeex.chat.domains.taskmngr.dto.TaskDto;
import co.jp.xeex.chat.entity.Task;

/**
 * TaskMapper
 * 
 * @author q_thinh
 */
public interface TaskMapper {
    /**
     * Mapping properties Task to TaskDto
     * 
     * @param chatMessage
     * @return
     */
    public TaskDto taskToDto(Task task);
}
