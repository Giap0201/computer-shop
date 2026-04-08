package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.cart.CartItemResponse;
import com.nguyenhuugiap.computer_shop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.product.name", target = "productName")
    @Mapping(source = "productVariant.imageUrl", target = "imageUrl")
    @Mapping(source = "productVariant.price", target = "unitPrice")
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
