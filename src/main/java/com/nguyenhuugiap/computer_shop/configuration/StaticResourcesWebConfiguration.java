package com.nguyenhuugiap.computer_shop.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration

public class StaticResourcesWebConfiguration implements WebMvcConfigurer {
    private final StorageProperties storageProperties;

    public StaticResourcesWebConfiguration(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        String rootLocation = storageProperties.getLocation();
        Path path = Paths.get(rootLocation);
        String absolutePath = path.toAbsolutePath().normalize().toUri().toString();
        System.out.println(absolutePath);
        registry.addResourceHandler("/" + rootLocation + "/**")
                .addResourceLocations(absolutePath);
    }
}
