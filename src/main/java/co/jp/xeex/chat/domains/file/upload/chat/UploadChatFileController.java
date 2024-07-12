package co.jp.xeex.chat.domains.file.upload.chat;

import co.jp.xeex.chat.base.ControllerBase;
import co.jp.xeex.chat.common.RestApiEndPoints;
import co.jp.xeex.chat.domains.file.enums.FileType;
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
 * UploadChatFileController
 * 
 * @author q_thinh
 */
@RestController
public class UploadChatFileController extends ControllerBase<UploadChatFileRequest> {
    private final UploadChatFileService uploadChatFileService;

    public UploadChatFileController(@Autowired UploadChatFileService uploadChatFileService) {
        this.uploadChatFileService = uploadChatFileService;
    }

    @Override
    public final ResponseEntity<?> restApi(@RequestBody UploadChatFileRequest request) throws BusinessException {
        return null;
    }

    @SuppressWarnings({ "squid:S1452" })
    @PostMapping(path = RestApiEndPoints.UPLOAD_CHAT_FILE, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public final ResponseEntity<?> restFileApi(@RequestParam(value = "fileType", required = false) FileType fileType,
            @RequestParam("files") MultipartFile[] files) throws BusinessException {

        UploadChatFileRequest request = new UploadChatFileRequest();
        super.preProcessRequest(request);
        request.setFileType(fileType != null ? fileType : FileType.FILE);
        request.setFiles(files);

        UploadChatFileResponse out = uploadChatFileService.execute(request);
        return out != null ? createSuccessResponse(out) : createNullFailResponse();
    }
}
