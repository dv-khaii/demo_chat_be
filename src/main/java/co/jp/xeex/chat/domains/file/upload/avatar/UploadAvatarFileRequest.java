package co.jp.xeex.chat.domains.file.upload.avatar;

import org.springframework.web.multipart.MultipartFile;

import co.jp.xeex.chat.base.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UploadAvatarFileRequest
 * 
 * @author q_thinh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadAvatarFileRequest extends RequestBase {
    private MultipartFile file;
}
