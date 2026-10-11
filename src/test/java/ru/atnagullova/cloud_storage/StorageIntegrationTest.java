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
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StorageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

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

    private Cookie signUpAndSignIn(String username) throws Exception {

        String body = """
               {"username:"
               """ + username + """
                ","password:"password123"}
                """;

        mockMvc.perform(post("/api/auth/sign-in")
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


}
