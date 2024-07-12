package co.jp.xeex.chat.domains.chatmngr.msg.unread;

import org.hibernate.validator.constraints.Length;

import co.jp.xeex.chat.base.RequestBase;
import co.jp.xeex.chat.validation.DtoValidateConsts;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Dto used to provide read status of chat messages<br>
 * (both request and response)
 * 
 * @author v_long
 */

public class GetChatUnreadRequest extends RequestBase {
    /**
     * Current username/empCd
     */
    @NotNull(message = DtoValidateConsts.VALIDATE_ERR_NOT_NULL)
    @NotBlank(message = DtoValidateConsts.VALIDATE_ERR_EMPTY)
    @Length(min = 1, max = 50, message = DtoValidateConsts.VALIDATE_ERR_LEN_MIN_MAX)
    public String userId;
    /**
     * the chat group id associated with the chat messages
     */
    @NotNull(message = DtoValidateConsts.VALIDATE_ERR_NOT_NULL)
    @NotBlank(message = DtoValidateConsts.VALIDATE_ERR_EMPTY)
    @Pattern(regexp = DtoValidateConsts.PATTERN_UUID_STRING, message = DtoValidateConsts.VALIDATE_ERR_ID_UUID_INVALID)
    public String chatGroupId;
    /**
     * the message id that the user has read
     */
    @Pattern(regexp = DtoValidateConsts.PATTERN_UUID_STRING, message = DtoValidateConsts.VALIDATE_ERR_ID_UUID_INVALID)
    public String repplyMessageId;
}
