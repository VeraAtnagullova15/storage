package ru.atnagullova.cloud_storage;


import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class StorageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    protected String bucket;

    @BeforeEach
    public void cleanBucket() throws Exception {

        Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .recursive(true)
                .build());;

        for (Result<Item> i : items) {
            minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(i.get().objectName())
                    .build());
        }
    }

    protected Cookie signUpAndSignIn(String username) throws Exception {

        String body = getContentBody(username, "password123");

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getCookie("SESSION");
    }

    private String getRootByUsername(String username) {

        Long id = userRepository.findByUsername(username).orElseThrow().getId();

        return "user-" + id + "-files/";
    }

    private List<String> getBucketObjects(String prefix) throws Exception {

        List<String> objects = new ArrayList<>();
        Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .recursive(true)
                .build());

        for (Result<Item> i : items) {
            objects.add(i.get().objectName());
        }

        return objects;
    }


    @Test
    public void upload_fileExistsOnBucket() throws Exception {

        Cookie alina = signUpAndSignIn("alina");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "test.txt",
                                "text/plain", "test".getBytes()))
                        .param("path", "")
                        .cookie(alina))
                .andExpect(status().isCreated());

        String root = getRootByUsername("alina");

        assertThat(getBucketObjects(root).contains(root + "text.txt"));

    }


    @Test
    public void delete_fileNotExistsOnBucket() throws Exception {

        Cookie alina = signUpAndSignIn("alina");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "test.txt",
                                "text/plain", "test".getBytes()))
                        .param("path", "")
                        .cookie(alina))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/resource")
                .param("path", "test.txt")
                        .cookie(alina))
                .andExpect(status().isNoContent());

        String root = getRootByUsername("alina");

        assertThat(getBucketObjects(root).isEmpty());
    }


    @Test
    public void renameOrMove_newFileExistsOnBucket() throws Exception {

        Cookie alina = signUpAndSignIn("alina");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "test.txt",
                                "text/plain", "test".getBytes()))
                        .param("path", "")
                        .cookie(alina))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/resource/move")
                        .param("from", "test.txt")
                .param("to", "test2.txt")
                .cookie(alina))
                .andExpect(status().isOk());

        String root = getRootByUsername("alina");

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "test2.txt"));
    }


    @Test
    public void renameOrMove_oldFileNotExistsOnBucket() throws Exception {

        Cookie tamara = signUpAndSignIn("tamara");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "test/test.txt",
                                "text/plain", "test".getBytes()))
                        .param("path", "")
                        .cookie(tamara))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/directory")
                .param("path", "docs/")
                .cookie(tamara))
                        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/resource/move")
                        .param("from", "test/test.txt")
                        .param("to", "docs/test.txt")
                        .cookie(tamara))
                .andExpect(status().isOk());

        String root = getRootByUsername("tamara");

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "docs/test.txt"));
        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "test/test.txt")).isFalse();
    }


    @Test
    public void search_userCannotDeleteFileOfAnotherUser() throws Exception {

        Cookie vera = signUpAndSignIn("vera");
        Cookie alina = signUpAndSignIn("alina");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "docs/test.txt",
                                "text/plain", "test".getBytes()))
                        .param("path", "")
                        .cookie(vera))
                .andExpect(status().isCreated());

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "test/string.txt",
                                "text/plain", "string".getBytes()))
                        .param("path", "")
                        .cookie(alina))
                .andExpect(status().isCreated());

        MvcResult result =  mockMvc.perform(get("/api/resource/search")
                .param("query", "string")
                .cookie(vera))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertThat(response.contains("string")).isFalse();
    }


    @Test
    public void getInfo_getInfoUnexistingResource() throws Exception {

        Cookie tamara = signUpAndSignIn("tamara");
        String root = getRootByUsername("tamara");

        mockMvc.perform(get("/api/resource")
                .param("path", "star.txt")
                .cookie(tamara))
                .andExpect(status().isNotFound());

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "star.txt")).isFalse();
    }


    @Test
    public void createEmptyDirectory_directoryExistsOnBucket() throws Exception {

        Cookie vera = signUpAndSignIn("vera");
        String root = getRootByUsername("vera");

        mockMvc.perform(post("/api/directory")
                .param("path", "Vera docs/")
                .cookie(vera))
                .andExpect(status().isCreated());

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "Vera docs/"));
    }


    @Test
    public void upload_uploadFileWithCyrillicName() throws Exception {

        Cookie petr = signUpAndSignIn("petr");
        String root = getRootByUsername("petr");

        mockMvc.perform(post("/api/directory")
                        .param("path", "список дел/")
                        .cookie(petr))
                .andExpect(status().isCreated());

        mockMvc.perform(multipart("/api/resource")
                .file(new MockMultipartFile("object", "расписание.txt",
                        "text/plain", "расписание занятий".getBytes()))
                        .param("path", "список дел/")
                .cookie(petr))
                .andExpect(status().isCreated());

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "список дел/расписание.txt"));

    }


    @Test
    public void upload_uploadAlreadyExistingFile() throws Exception {

        Cookie natali = signUpAndSignIn("Nataly");

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "nataly.txt",
                                "text/plain", "to-do list".getBytes()))
                        .param("path", "")
                        .cookie(natali))
                .andExpect(status().isCreated());

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "nataly.txt",
                                "text/plain", "расписание занятий".getBytes()))
                        .param("path", "")
                        .cookie(natali))
                .andExpect(status().isConflict());
    }



    @Test
    public void renameOrMove_moveDirectoryIntoItself_DirectoryAlreadyExists() throws Exception {

        Cookie pavel = signUpAndSignIn("pavel");
        String root = getRootByUsername("pavel");

        mockMvc.perform(post("/api/directory")
                        .param("path", "Pavel's schedule/")
                        .cookie(pavel))
                .andExpect(status().isCreated());

        assertThat(getBucketObjects(root).contains(getBucketObjects(root) + "Pavel's schedule/"));

        mockMvc.perform(post("/api/resource/move")
                        .param("from", "Pavel's schedule/")
                        .param("to", "Pavel's schedule/")
                        .cookie(pavel))
                .andExpect(status().isConflict());

    }


}
