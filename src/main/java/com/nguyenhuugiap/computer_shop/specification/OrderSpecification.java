package com.nguyenhuugiap.computer_shop.specification;

import com.nguyenhuugiap.computer_shop.dto.order.AdminOrderSearchRequest;
import com.nguyenhuugiap.computer_shop.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> getSearchSpec(AdminOrderSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //Tim theo order code, tu dong format viet hoa tim theo like
            if (StringUtils.hasText(request.getOrderCode())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("orderCode")),
                        "%" + request.getOrderCode().toUpperCase() + "%"
                ));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (request.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getFromDate()));
            }

            if (request.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getToDate()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
