package com.nguyenhuugiap.computer_shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {
    // Lần này chúng ta gọi cái RedisTemplate chuẩn JSON vừa tạo ở file Config
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set-object")
    public String setObject() {
        // Giả lập một Object Sản phẩm (Product) dùng Map cho nhanh
        Map<String, Object> product = new HashMap<>();
        product.put("id", 101);
        product.put("name", "Laptop Dell XPS");
        product.put("price", 25000000);

        // Lưu toàn bộ Object vào Redis với chìa khóa "product:101", sống trong 5 phút
        redisTemplate.opsForValue().set("product:101", product, 5, TimeUnit.MINUTES);
        return "Đã lưu Object Sản phẩm vào Redis dạng JSON!";
    }

    @GetMapping("/get-object")
    public Object getObject() {
        // Lấy Object ra, Spring tự động dịch ngược từ JSON về Java
        return redisTemplate.opsForValue().get("product:101");
    }
}
