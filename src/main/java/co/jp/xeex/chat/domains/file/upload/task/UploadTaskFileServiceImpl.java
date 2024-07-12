package co.jp.xeex.chat.domains.file.upload.task;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.file.dto.FileDto;
import co.jp.xeex.chat.domains.file.enums.FileType;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.util.EnvironmentUtil;
import co.jp.xeex.chat.util.FileUtil;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * UploadTaskFileServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class UploadTaskFileServiceImpl extends ServiceBaseImpl<UploadTaskFileRequest, UploadTaskFileResponse>
        implements UploadTaskFileService {

    // err message id
    private static final String UPLOAD_TASK_FILE_ERR_FILE_EMPTY = "UPLOAD_TASK_FILE_ERR_FILE_EMPTY";
    private static final String UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_SIZE = "UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_SIZE";
    private static final String UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_COUNT = "UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_COUNT";

    // DI
    private EnvironmentUtil environmentUtil;

    @Override
    @Transactional(rollbackFor = { Exception.class, BusinessException.class })
    public UploadTaskFileResponse processRequest(UploadTaskFileRequest in) throws BusinessException {
        // Validation files
        validation(in);

        try {
            // Setting upload path
            Path targetUploadPath = Paths.get(environmentUtil.rootUploadPath, AppConstant.PATH_TEMP_PREFIX,
                    in.requestBy);

            // Upload files
            List<FileDto> files = new ArrayList<>();
            for (MultipartFile multipartFile : in.getFiles()) {
                // Upload file
                String storeName = FileUtil.uploadFile(targetUploadPath, multipartFile);

                // File info
                FileDto fileDto = new FileDto();
                fileDto.setFileType(FileUtil.isImage(multipartFile) ? FileType.IMAGE : FileType.FILE);
                fileDto.setOriginName(multipartFile.getOriginalFilename());
                fileDto.setStoreName(storeName);
                fileDto.setEmpCd(in.requestBy);
                files.add(fileDto);
            }

            // Response
            UploadTaskFileResponse response = new UploadTaskFileResponse();
            response.setFiles(files);
            return response;
        } catch (IOException e) {
            throw new BusinessException(e.getMessage(), in.lang);
        }
    }

    /**
     * Validation files
     * 
     * @param in
     * @throws BusinessException
     */
    private void validation(UploadTaskFileRequest in) throws BusinessException {
        // Validation files
        if (in.getFiles()[0].isEmpty()) {
            throw new BusinessException(UPLOAD_TASK_FILE_ERR_FILE_EMPTY, in.lang);
        }

        // Check max files
        Integer maxCountUpload = environmentUtil.maxUploadFileCount;
        if (in.getFiles().length > maxCountUpload) {
            throw new BusinessException(UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_COUNT,
                    new String[] { maxCountUpload.toString() }, in.lang);
        }

        // Check max size
        for (MultipartFile file : in.getFiles()) {
            Float fileSize = file.getSize() / 1000000F;
            Float maxSizeAllow = (float) environmentUtil.maxUploadFileSize;
            if (fileSize > maxSizeAllow) {
                throw new BusinessException(UPLOAD_TASK_FILE_ERR_EXCEEDED_FILE_SIZE,
                        new String[] { maxSizeAllow.toString() }, in.lang);
            }
        }
    }
}
