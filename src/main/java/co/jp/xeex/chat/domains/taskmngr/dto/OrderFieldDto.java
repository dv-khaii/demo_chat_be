package co.jp.xeex.chat.domains.taskmngr.dto;

import org.springframework.data.domain.Sort.Direction;

import io.micrometer.common.lang.Nullable;

/**
 * OrderDto
 * 
 * @author q_thinh
 */
public class OrderFieldDto {
    /**
     * The name of the field.
     */
    @Nullable
    public String fieldName;
    /**
     * The order type for sorting.
     * Default value is {@link Direction#ASC}.
     */
    @Nullable
    public Direction orderType = Direction.ASC;
}
