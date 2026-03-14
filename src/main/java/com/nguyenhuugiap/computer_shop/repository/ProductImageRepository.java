package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProduct_Id(Long productId);
    //Lấy ảnh của Product này, sắp xếp giảm dần theo displayOrder, và chỉ lấy thằng đầu tiên (Top 1)
    Optional<ProductImage> findTopByProduct_IdOrderByDisplayOrderDesc(Long productId);
    Optional<ProductImage> findByIdAndProduct_Id(Long id, Long productId);
}
