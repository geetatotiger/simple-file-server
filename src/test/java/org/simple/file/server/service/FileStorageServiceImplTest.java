package org.simple.file.server.service;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.simple.file.server.controller.FileServerController;
import org.simple.file.server.exception.StorageFileUploadException;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.simple.file.server.persistence.localstore.LocalFileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class FileStorageServiceImplTest {

    FileStorageServiceImpl fileStorageService;
    @Mock
    StorageSystem storageSystem;

    @Mock
    MultipartFile file;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileStorageService = new FileStorageServiceImpl(storageSystem);
    }
    @Test
    public void testStoreFileOnServer() throws URISyntaxException {
        //Arrange
        URI expectedFilePath = new URI("path/to/file");
        when(storageSystem.saveFile(file)).thenReturn(Either.right(expectedFilePath));

        //Act
        URI actualFilePath = fileStorageService.storeFileOnServer(file);

        //assert
        assertThat(expectedFilePath).isEqualTo(actualFilePath);
    }

    @Test
    public void testStoreFileOnServerWithError() {
        // Arrange
        StorageError storageError = new StorageError(StorageErrorCode.FILE_SAVE_FAILED, "failed to save");
        when(storageSystem.saveFile(file)).thenReturn(Either.left(storageError));

        // Act and Assert

        assertThatThrownBy(() -> fileStorageService.storeFileOnServer(file))
                .isInstanceOf(StorageFileUploadException.class)
                .hasMessage("failed to save");
    }
}
