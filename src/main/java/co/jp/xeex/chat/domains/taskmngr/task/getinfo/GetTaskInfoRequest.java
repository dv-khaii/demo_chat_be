package co.jp.xeex.chat.domains.taskmngr.task.getinfo;

import co.jp.xeex.chat.base.RequestBase;
import co.jp.xeex.chat.validation.DtoValidateConsts;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * GetTaskInfoRequest
 * 
 * @author q_thinh
 */
public class GetTaskInfoRequest extends RequestBase {
    @NotNull(message = DtoValidateConsts.VALIDATE_ERR_NOT_NULL)
    @NotEmpty(message = DtoValidateConsts.VALIDATE_ERR_EMPTY)
    @Pattern(regexp = DtoValidateConsts.PATTERN_UUID_STRING, message = DtoValidateConsts.VALIDATE_ERR_ID_UUID_INVALID)
    public String taskId;
}
