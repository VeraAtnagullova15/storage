package ru.atnagullova.cloud_storage.configuration.minio;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.atnagullova.cloud_storage.exception.InitializeBucketException;

@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Autowired
    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }


    @Bean
    public MinioClient minioClient() {

        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        
        initBucket(client);
        
        return client;      
    }

    private void initBucket(MinioClient client) {

        try {
            boolean found = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());

            if (!found) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build());
            }
        } catch (Exception e) {

            throw new InitializeBucketException("Bucket " + minioProperties.getBucket() + "wasn't initialize");
        }
    }


}
