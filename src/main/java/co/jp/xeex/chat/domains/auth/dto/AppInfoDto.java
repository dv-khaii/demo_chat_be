package co.jp.xeex.chat.domains.auth.dto;

import lombok.Data;

/**
 * Represents the data transfer object for application information.
 * 
 * @author q_thinh
 */
@Data
public class AppInfoDto {
    /**
     * The maximum number of files that can be uploaded.
     */
    private Integer maxUploadFileCount;
    /**
     * The maximum upload file size allowed.
     */
    private Integer maxUploadFileSize;
}
