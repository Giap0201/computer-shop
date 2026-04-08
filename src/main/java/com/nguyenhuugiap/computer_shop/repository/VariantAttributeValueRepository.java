package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, Long> {

    boolean existsByAttributeDefinitionId(Long id);
}
