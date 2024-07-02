package co.jp.xeex.chat.domains.file.dto;

import lombok.Data;

/**
 * FileDto
 * 
 * @author q_thinh
 */
@Data
public class FileConfigDto {
    String maxUploadFileCount;
    String maxUploadFileSize;
}
