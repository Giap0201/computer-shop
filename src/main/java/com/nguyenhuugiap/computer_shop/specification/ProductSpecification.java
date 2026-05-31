package com.nguyenhuugiap.computer_shop.specification;

import com.nguyenhuugiap.computer_shop.dto.product.ProductSearchRequest;
import com.nguyenhuugiap.computer_shop.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> getSearchSpec(ProductSearchRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), " % " + request.getKeyword() + " % "
                ));
            }

            if (request.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), request.getBrandId()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (request.getFromPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("minPrice"), request.getFromPrice()));
            }
            if (request.getToPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("minPrice"), request.getToPrice()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
