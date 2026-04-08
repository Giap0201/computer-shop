package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.order.OrderItemResponse;
import com.nguyenhuugiap.computer_shop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "subTotal", expression = "java(calculateSubTotal(item))")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "productVariant.id", target = "variantId")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    default BigDecimal calculateSubTotal(OrderItem item){
        if(item.getPriceAtPurchase() == null || item.getQuantity() == null)
            return BigDecimal.ZERO;
        return item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
