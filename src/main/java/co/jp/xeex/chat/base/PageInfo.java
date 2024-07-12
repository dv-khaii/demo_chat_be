package co.jp.xeex.chat.base;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import co.jp.xeex.chat.domains.taskmngr.dto.OrderFieldDto;

/**
 * PageInfo represents the PageRequest
 * 
 * @author v_long
 */
@AllArgsConstructor
public class PageInfo {
    private Integer pageIdx;
    private Integer perPage = 20;
    private List<OrderFieldDto> orderFieldList;

    public PageRequest getPageRequest() {
        Sort sort = Sort.unsorted();
        if (this.orderFieldList != null && !this.orderFieldList.isEmpty()) {
            List<Order> orders = new ArrayList<>();
            for (OrderFieldDto orderFieldDto : this.orderFieldList) {
                Order order = new Order(orderFieldDto.orderType, orderFieldDto.fieldName);
                orders.add(order);
            }
            sort = Sort.by(orders);
        }
        Integer page = (this.pageIdx == null) ? 1 : this.pageIdx;
        return PageRequest.of(page - 1, perPage, sort);
    }
}
