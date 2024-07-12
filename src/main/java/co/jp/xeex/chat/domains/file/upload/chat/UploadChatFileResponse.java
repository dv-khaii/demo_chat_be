package co.jp.xeex.chat.domains.file.upload.chat;

import java.util.List;

import co.jp.xeex.chat.domains.file.dto.FileDto;
import lombok.Data;

/**
 * UploadChatFileResponse
 * 
 * @author q_thinh
 */
@Data
public class UploadChatFileResponse {
    List<FileDto> files;
}
