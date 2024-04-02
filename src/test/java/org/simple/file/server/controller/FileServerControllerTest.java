package org.simple.file.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class FileServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadEndpoint_successful() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test data".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUploadEndpoint_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
