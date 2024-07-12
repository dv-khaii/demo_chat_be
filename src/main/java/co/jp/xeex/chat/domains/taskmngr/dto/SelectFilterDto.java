package co.jp.xeex.chat.domains.taskmngr.dto;

import io.micrometer.common.lang.Nullable;

/**
 * Represents a data transfer object for selecting filters in a task manager.
 */
public class SelectFilterDto {
    @Nullable
    public Boolean isDone = false;
    @Nullable
    public Boolean isNotDone = false;
    @Nullable
    public Boolean isAssigner = false;
    @Nullable
    public Boolean isAssignee = false;
    @Nullable
    public Boolean isHightPriority = false;
    @Nullable
    public Boolean isNormalPriority = false;
    @Nullable
    public Boolean isLowPriority = false;
    @Nullable
    public Boolean isDueDate = false;
    @Nullable
    public Boolean isOverDueDate = false;
}
