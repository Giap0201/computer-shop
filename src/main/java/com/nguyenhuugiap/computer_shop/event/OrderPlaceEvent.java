package com.nguyenhuugiap.computer_shop.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderPlaceEvent extends ApplicationEvent {
    private final Long orderId;

    public OrderPlaceEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }
}
