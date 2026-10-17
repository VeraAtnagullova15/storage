package ru.atnagullova.cloud_storage;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StorageErrorsIntegrationTest extends StorageIntegrationTest {

    @MockitoBean
    private MinioClient minioClientMock;

    private final long maxFileSize = 10L;


    @Test
    public void upload_uploadTooBigFile() throws Exception {

        Cookie albina = signUpAndSignIn("albina");

        doThrow(new MaxUploadSizeExceededException(maxFileSize))
                .when(minioClientMock)
                .putObject(any(PutObjectArgs.class));

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "movie.mp4",
                                "video/mp4", new byte[1024 * 1024 * 11]))
                        .cookie(albina))
                .andExpect(status().is5xxServerError());
    }


    @Test
    public void upload_minioUnavailable() throws Exception {

        Cookie igor = signUpAndSignIn("igor");

        doThrow(new RuntimeException("Minio unavailable"))
                .when(minioClientMock)
                .putObject(any(PutObjectArgs.class));

        mockMvc.perform(multipart("/api/resource")
                        .file(new MockMultipartFile("object", "text.txt",
                                "text/plain", "bla-bla-bla".getBytes()))
                        .cookie(igor))
                .andExpect(status().is5xxServerError());
    }


    @Test
    public void download_minioUnavailable() throws Exception {

        Cookie vera = signUpAndSignIn("vera");

        doThrow(new RuntimeException("Minio unavailable"))
                .when(minioClientMock)
                .getObject(any(GetObjectArgs.class));

        mockMvc.perform(get("/api/resource/download")
                        .param("path", "test")
                        .cookie(vera))
                .andExpect(status().is5xxServerError());
    }


}

