package co.jp.xeex.chat.domains.file.upload.task;

import org.springframework.web.multipart.MultipartFile;

import co.jp.xeex.chat.base.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UploadTaskFileRequest
 * 
 * @author q_thinh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadTaskFileRequest extends RequestBase {
    private MultipartFile[] files;
}
