package org.simple.file.server.service;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.simple.file.server.exception.FileDoNotPresetException;
import org.simple.file.server.exception.FileUploadException;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class FileStorageServiceImplTest {

    FileStorageServiceImpl fileStorageService;
    StorageSystem storageSystem =Mockito.mock(StorageSystem.class);
    MultipartFile file = Mockito.mock(MultipartFile.class);

    @BeforeEach
    public void setUp() {
        fileStorageService = new FileStorageServiceImpl(storageSystem);
    }
    @Test
    public void storeFileOnServer() throws URISyntaxException {
        //Arrange
        URI expectedFilePath = new URI("path/to/file");
        when(storageSystem.saveFile(file)).thenReturn(Either.right(expectedFilePath));

        //Act
        URI actualFilePath = fileStorageService.storeFileOnServer(file);

        //assert
        assertThat(expectedFilePath).isEqualTo(actualFilePath);
    }

    @Test
    public void storeFileOnServer_fileSaveError() {
        // Arrange
        StorageError storageError = new StorageError(StorageErrorCode.FILE_SAVE_FAILED, "failed to save");
        when(storageSystem.saveFile(file)).thenReturn(Either.left(storageError));

        // Act and Assert

        assertThatThrownBy(() -> fileStorageService.storeFileOnServer(file))
                .isInstanceOf(FileUploadException.class)
                .hasMessage("failed to save");
    }

    @Test
    public  void getFileFromServer() throws MalformedURLException {
        //Arragne
        Path path = Paths.get("build/uploads/test.txt");
        Resource expectedResource =new UrlResource(path.toUri());
        when(storageSystem.fetchFile("test.txt")).thenReturn( Either.right(expectedResource));

        //Act
         Resource actualResource = fileStorageService.fetchFile("test.txt");
        //Assert
        assertThat(actualResource).isEqualTo(expectedResource);
    }

    @Test
    public  void getFileFromServer_fileDoNotExist()  {
        //Arragne
        StorageError storageError = new StorageError(StorageErrorCode.FILE_DO_NOT_EXIST, "File do not present test.txt");
        when(storageSystem.fetchFile("test.txt")).thenReturn( Either.left(storageError));

        //Act and Assert
        assertThatThrownBy(() -> fileStorageService.fetchFile("test.txt"))
                .isInstanceOf(FileDoNotPresetException.class)
                .hasMessage("File do not present test.txt");
    }
}
