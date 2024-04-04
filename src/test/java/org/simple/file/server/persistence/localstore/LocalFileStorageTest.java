package org.simple.file.server.persistence.localstore;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.simple.file.server.persistence.StorageError;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalFileStorageTest {

    LocalFileStorage localFileStorage;

    @Mock
    private MultipartFile file;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        localFileStorage = new LocalFileStorage("build/uploads");
    }

    @Test
    public void saveFileOnLocalFolder()  {
        MockMultipartFile validFile = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());

         Either<StorageError, URI> result  = localFileStorage.saveFile(validFile);

        assertThat(result.get()).toString().contains("build/uploads/test.txt");
    }
}
