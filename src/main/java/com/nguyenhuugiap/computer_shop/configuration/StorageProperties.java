package com.nguyenhuugiap.computer_shop.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("storage")
public class StorageProperties {
    private String location = "uploads";
}
