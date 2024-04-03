package org.simple.file.server.config;

import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.service.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServerConfig {
    @Bean
    FileStorageService fileStorageServiceBean() {
        return new FileStorageServiceImpl();
    }
}
