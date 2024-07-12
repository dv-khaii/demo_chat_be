package co.jp.xeex.chat.domains.file.upload.chat;

import org.springframework.web.multipart.MultipartFile;

import co.jp.xeex.chat.base.RequestBase;
import co.jp.xeex.chat.domains.file.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UploadChatFileRequest
 * 
 * @author q_thinh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadChatFileRequest extends RequestBase {
    private MultipartFile[] files;
    private FileType fileType;
}
