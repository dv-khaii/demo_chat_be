package co.jp.xeex.chat.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import co.jp.xeex.chat.domains.auth.dto.AppInfoDto;

@Component
@PropertySource("classpath:application.properties")
public class EnvironmentUtil {

    // constant
    private static final String HTTP = "http";

    // Application properties value
    @Value("${server.address}")
    public String serverAddress;
    @Value("${server.port}")
    public String serverPort;
    @Value("${file.path-upload}")
    public String rootUploadPath;

    // Limit file upload
    @Value("${file.limit-file-count-upload}")
    public Integer maxUploadFileCount;
    @Value("${file.limit-file-size-upload}")
    public Integer maxUploadFileSize;
    @Value("${file.limit-avatar-size-upload}")
    public Integer maxUploadFileAvatarSize;

    // DI
    private Environment env;

    public EnvironmentUtil(Environment env) {
        this.env = env;
    }

    /**
     * Get value properties with key
     * 
     * @param configKey
     * @return
     */
    public String getConfigValue(String configKey) {
        return env.getProperty(configKey);
    }

    /**
     * Get domain host
     * 
     * @return
     */
    public String getDomain() {
        return String.format("%s://%s:%s", HTTP, serverAddress, serverPort);
    }

    /**
     * Get app info
     * 
     * @return
     */
    public AppInfoDto getAppInfo() {
        AppInfoDto appInfoDto = new AppInfoDto();
        appInfoDto.setMaxUploadFileCount(maxUploadFileCount);
        appInfoDto.setMaxUploadFileSize(maxUploadFileSize);
        return appInfoDto;
    }
}
