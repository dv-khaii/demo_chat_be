package co.jp.xeex.chat.domains.file.upload.avatar;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.file.dto.FileDto;
import co.jp.xeex.chat.domains.file.enums.FileType;
import co.jp.xeex.chat.entity.File;
import co.jp.xeex.chat.entity.User;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.FileRepository;
import co.jp.xeex.chat.repository.UserRepository;
import co.jp.xeex.chat.util.EnvironmentUtil;
import co.jp.xeex.chat.util.FileUtil;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UploadAvatarFileServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class UploadAvatarFileServiceImpl extends ServiceBaseImpl<UploadAvatarFileRequest, UploadAvatarFileResponse>
        implements UploadAvatarFileService {

    // err message id
    private static final String UPLOAD_AVATAR_FILE_ERR_FILE_EMPTY = "UPLOAD_AVATAR_FILE_ERR_FILE_EMPTY";
    private static final String UPLOAD_AVATAR_FILE_ERR_EXCEEDED_FILE_SIZE = "UPLOAD_AVATAR_FILE_ERR_EXCEEDED_FILE_SIZE";
    private static final String UPLOAD_AVATAR_FILE_ERR_FILE_TYPE_IMAGE_INCORRECT = "UPLOAD_AVATAR_FILE_ERR_FILE_TYPE_IMAGE_INCORRECT";

    // constants
    private static final String[] IMAGE_TYPES = { "png", "jpg", "jpeg", "svg" };

    // DI
    private UserRepository userRepo;
    private FileRepository fileRepo;
    private EnvironmentUtil environmentUtil;

    @Override
    @Transactional(rollbackFor = { Exception.class, BusinessException.class })
    public UploadAvatarFileResponse processRequest(UploadAvatarFileRequest in) throws BusinessException {
        // Validation files
        validation(in);

        try {
            // Setting upload path
            Path targetUploadPath = Paths.get(environmentUtil.rootUploadPath, AppConstant.PATH_AVATAR_PREFIX,
                    in.requestBy);

            // Upload file
            String newStoreName = FileUtil.uploadFile(targetUploadPath, in.getFile());

            // Save avatar
            User user = userRepo.findByUserName(in.requestBy);
            String oldStoreName = user.getAvatar();
            user.setAvatar(newStoreName);
            userRepo.saveAndFlush(user);

            // Delete old avatar
            deleteOldAvatar(oldStoreName);

            // Save new avatar file
            File file = new File();
            file.initDefault(in.requestBy);
            file.setStoreName(newStoreName);
            file.setOriginName(in.getFile().getOriginalFilename());
            file.setFileType(FileType.IMAGE);
            fileRepo.saveAndFlush(file);

            // Link avatar
            String fileUrl = String.format(AppConstant.FILE_URL, environmentUtil.fileHost,
                    AppConstant.PATH_AVATAR_PREFIX, newStoreName);
            FileDto fileDto = new FileDto();
            fileDto.setImageUrl(fileUrl);
            fileDto.setFileType(FileType.IMAGE);
            fileDto.setOriginName(in.getFile().getOriginalFilename());
            fileDto.setStoreName(newStoreName);
            fileDto.setEmpCd(in.requestBy);

            // Response
            UploadAvatarFileResponse response = new UploadAvatarFileResponse();
            response.setFile(fileDto);
            return response;
        } catch (IOException e) {
            throw new BusinessException(e.getMessage(), in.lang);
        }
    }

    /**
     * Validation avatar file
     * 
     * @param in
     * @throws BusinessException
     */
    private void validation(UploadAvatarFileRequest in) throws BusinessException {
        // Validation files
        if (in.getFile().isEmpty()) {
            throw new BusinessException(UPLOAD_AVATAR_FILE_ERR_FILE_EMPTY, in.lang);
        }

        // Check image type
        if (!FileUtil.isImage(in.getFile())) {
            throw new BusinessException(UPLOAD_AVATAR_FILE_ERR_FILE_TYPE_IMAGE_INCORRECT,
                    new String[] { String.join(",", IMAGE_TYPES) }, in.lang);
        }

        // Check max size
        Float fileSize = in.getFile().getSize() / 1000000F;
        Float maxSizeAllow = (float) environmentUtil.maxUploadFileAvatarSize;
        if (fileSize > maxSizeAllow) {
            throw new BusinessException(UPLOAD_AVATAR_FILE_ERR_EXCEEDED_FILE_SIZE,
                    new String[] { maxSizeAllow.toString() }, in.lang);
        }
    }

    /**
     * delete old avatar
     * 
     * @param oldStoreName old avatar store file
     * @throws IOException
     */
    private void deleteOldAvatar(String oldStoreName) throws IOException {
        // Delete old avatar
        File oldAvatarFile = fileRepo.findByStoreName(oldStoreName);
        if (oldAvatarFile != null) {
            fileRepo.delete(oldAvatarFile);

            // Delete store file
            Path targetPath = FileUtil.getTargetPath(environmentUtil.rootUploadPath, AppConstant.PATH_AVATAR_PREFIX,
                    oldAvatarFile.getCreateBy(), null);
            targetPath = targetPath.resolve(Paths.get(oldStoreName));
            Files.deleteIfExists(targetPath);
        }
    }
}
