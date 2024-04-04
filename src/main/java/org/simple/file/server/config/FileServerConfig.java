package org.simple.file.server.config;

import org.simple.file.server.persistence.StorageSystem;
import org.simple.file.server.persistence.localstore.LocalFileStorage;
import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.service.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServerConfig {
    @Bean
    StorageSystem localStoreBean(@Value("${fileserver.uploadDir}") String uploadDir) {
        return new LocalFileStorage(uploadDir);
    }
    @Bean
    FileStorageService fileStorageServiceBean(StorageSystem storageSystem) {
        return new FileStorageServiceImpl(storageSystem);
    }

}
