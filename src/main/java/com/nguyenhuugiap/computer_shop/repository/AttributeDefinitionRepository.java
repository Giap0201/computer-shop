package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, Long> {

    boolean existsByName(String name);

    Optional<AttributeDefinition> findByName(String name);

}
