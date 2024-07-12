package co.jp.xeex.chat.domains.taskmngr.task.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.jp.xeex.chat.base.RequestBase;
import co.jp.xeex.chat.domains.taskmngr.dto.OrderFieldDto;
import co.jp.xeex.chat.domains.taskmngr.dto.SelectFilterDto;
import co.jp.xeex.chat.validation.DtoValidateConsts;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Pattern;

/**
 * GetTaskRequest
 * 
 * @author q_thinh
 */
public class GetTaskRequest extends RequestBase {
    /**
     * Group task id
     * Get task data by group id
     * Get all when empty or null
     */
    @Pattern(regexp = DtoValidateConsts.PATTERN_UUID_STRING, message = DtoValidateConsts.VALIDATE_ERR_ID_UUID_INVALID)
    public String groupId = StringUtils.EMPTY;

    /**
     * Search text value
     * Get all when empty or null
     */
    @Nullable
    public String searchText = StringUtils.EMPTY;

    /**
     * Total task per page
     */
    @Nullable
    public Integer perPage = 20;

    /**
     * Client show task data with page
     */
    @Nullable
    public Integer pageIdx = 1;

    /**
     * The filter used to select specific tasks.
     */
    @Nullable
    public SelectFilterDto filter = new SelectFilterDto();

    /**
     * Detail order by
     */
    @Nullable
    public List<OrderFieldDto> orderFieldList = new ArrayList<>();
}
