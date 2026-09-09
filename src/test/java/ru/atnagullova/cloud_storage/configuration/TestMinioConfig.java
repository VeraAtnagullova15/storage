package ru.atnagullova.cloud_storage.configuration;

import io.minio.MinioClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMinioConfig {

    @Bean
    public MinioClient minioClient() {

        return Mockito.mock(MinioClient.class);
    }
}
