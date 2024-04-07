package org.simple.file.server.persistence.localstore;

import io.vavr.control.Either;
import org.junit.jupiter.api.*;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalFileStorageTest {

    static LocalFileStorage localFileStorage;
    String testFileName = "test.txt";
    @BeforeAll
    public static void setUpAll() {
        localFileStorage = new LocalFileStorage("build/uploads");
    }

   @BeforeEach
    public void setUp() {
        //test file which is uploaded for all test
        MockMultipartFile validFile = new MockMultipartFile("file", testFileName, "text/plain", "test data".getBytes());
        localFileStorage.saveFile(validFile);
    }

    @AfterEach
    public void tearDown() {
        localFileStorage.deleteFile(testFileName);
    }

    @Test
    public void saveFileOnLocalFolder()  {
        MockMultipartFile validFile = new MockMultipartFile("file", "testsave.txt", "text/plain", "test data".getBytes());

        Either<StorageError, URI> result  = localFileStorage.saveFile(validFile);

        assertThat(result.get()).asString().contains("build/uploads/testsave.txt");

        localFileStorage.deleteFile("testsave.txt");
    }

    @Test
    void fetchFileFromLocalStorage() {
        Either<StorageError, Resource> result = localFileStorage.fetchFile(testFileName);
        Resource resource = result.get();
        assertThat(resource).asString().contains("/build/uploads/" + testFileName);

    }

    @Test
    void listFilesFromLocalStorage(){
        Either<StorageError, List<String>> result = localFileStorage.listFiles();
        assertThat(result.get()).contains(testFileName);
    }

    @Test
    void fectFile_exception() {
        Either<StorageError, Resource> result = localFileStorage.fetchFile("notexistingfile");
        StorageError storageError = result.getLeft();
        assertThat(storageError.code()).isEqualByComparingTo(StorageErrorCode.FILE_DO_NOT_EXIST);
        assertThat(storageError.message()).isEqualTo("File do not present notexistingfile");
    }

}
