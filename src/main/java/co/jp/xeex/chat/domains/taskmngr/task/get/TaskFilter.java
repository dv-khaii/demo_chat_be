package co.jp.xeex.chat.domains.taskmngr.task.get;

import java.util.List;

import lombok.Data;

/**
 * Represents a data transfer object for filtering tasks.
 */
@Data
public class TaskFilter {
    private String status;
    private String assigner;
    private String assignee;
    private List<String> priorityList;
    private String dueDate;
    private String overDueDate;
}
