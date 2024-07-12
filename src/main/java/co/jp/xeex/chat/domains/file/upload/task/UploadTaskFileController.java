package co.jp.xeex.chat.domains.file.upload.task;

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
 * UploadTaskFileController
 * 
 * @author q_thinh
 */
@RestController
public class UploadTaskFileController extends ControllerBase<UploadTaskFileRequest> {
    private final UploadTaskFileService uploadTaskFileService;

    public UploadTaskFileController(@Autowired UploadTaskFileService uploadTaskFileService) {
        this.uploadTaskFileService = uploadTaskFileService;
    }

    @Override
    public final ResponseEntity<?> restApi(@RequestBody UploadTaskFileRequest request) throws BusinessException {
        return null;
    }

    @SuppressWarnings({ "squid:S1452" })
    @PostMapping(path = RestApiEndPoints.UPLOAD_TASK_FILE, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public final ResponseEntity<?> restFileApi(@RequestParam("files") MultipartFile[] files) throws BusinessException {

        UploadTaskFileRequest request = new UploadTaskFileRequest();
        super.preProcessRequest(request);
        request.setFiles(files);

        UploadTaskFileResponse out = uploadTaskFileService.execute(request);
        return out != null ? createSuccessResponse(out) : createNullFailResponse();
    }
}
