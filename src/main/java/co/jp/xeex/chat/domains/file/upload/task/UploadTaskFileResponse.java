package co.jp.xeex.chat.domains.file.upload.task;

import java.util.List;

import co.jp.xeex.chat.domains.file.dto.FileDto;
import lombok.Data;

/**
 * UploadTaskFileResponse
 * 
 * @author q_thinh
 */
@Data
public class UploadTaskFileResponse {
    List<FileDto> files;
}
