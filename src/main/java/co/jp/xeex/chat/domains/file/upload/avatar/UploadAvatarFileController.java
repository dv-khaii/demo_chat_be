package co.jp.xeex.chat.domains.file.upload.avatar;

import co.jp.xeex.chat.base.ControllerBase;
import co.jp.xeex.chat.common.RestApiEndPoints;
import co.jp.xeex.chat.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * UploadAvatarFileController
 * 
 * @author q_thinh
 */
@RestController
public class UploadAvatarFileController extends ControllerBase<UploadAvatarFileRequest> {
    private final UploadAvatarFileService uploadAvatarFileService;

    public UploadAvatarFileController(@Autowired UploadAvatarFileService uploadAvatarFileService) {
        this.uploadAvatarFileService = uploadAvatarFileService;
    }

    @Override
    public final ResponseEntity<?> restApi(@RequestBody UploadAvatarFileRequest request) throws BusinessException {
        return null;
    }

    @SuppressWarnings({ "squid:S1452" })
    @PostMapping(path = RestApiEndPoints.UPLOAD_AVATAR_FILE, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public final ResponseEntity<?> restFileApi(@RequestParam("file") MultipartFile file) throws BusinessException {

        UploadAvatarFileRequest request = new UploadAvatarFileRequest();
        super.preProcessRequest(request);
        request.setFile(file);

        UploadAvatarFileResponse out = uploadAvatarFileService.execute(request);
        return out != null ? createSuccessResponse(out) : createNullFailResponse();
    }
}
