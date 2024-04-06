package org.simple.file.server.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@WebMvcTest
@Import({RequestValidator.class})
public class FileServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    RequestValidator requestValidator;
    @MockBean
    FileStorageService fileStorageService;

    @Test
    public void uploadEndpoint() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test data".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void uploadEndpoint_emptyFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void downloadEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/file/{filename}", "test.txt"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void listFilesEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/files"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/file/{filename}", "test.txt"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
