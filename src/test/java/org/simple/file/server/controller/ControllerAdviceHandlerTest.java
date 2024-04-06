package org.simple.file.server.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.simple.file.server.exception.FileDoNotPresetException;
import org.simple.file.server.exception.FileUploadException;
import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
@Import({RequestValidator.class})
public class ControllerAdviceHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    RequestValidator requestValidator;
    @MockBean
    FileStorageService fileStorageService;

    @Test
    public void downloadEndpoint_badFileName() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/file/{filename}", "test?.txt"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void downloadFile_notExistingFile() throws Exception {
        when(fileStorageService.fetchFile("test.txt")).thenThrow(new FileDoNotPresetException("File do not present test.txt"));

        mockMvc.perform(MockMvcRequestBuilders.get("/file/{filename}", "test.txt"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string("File do not present test.txt"));
    }

    @Test
    void uploadFile_ioException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test data".getBytes()
        );
        when(fileStorageService.storeFileOnServer(file)).thenThrow(new FileUploadException("File upload failed !! "));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(content().string("File upload failed !! "));
    }

    @Test
    public void uncaught_exception() throws Exception {
        when(fileStorageService.fetchFile("test.txt")).thenThrow(new RuntimeException("Unknown Error !! "));

        mockMvc.perform(MockMvcRequestBuilders.get("/file/{filename}", "test.txt"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(content().string("File server could not complete the request"));
    }
}
