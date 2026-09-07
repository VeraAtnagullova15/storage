package ru.atnagullova.cloud_storage.configuration.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioProperties {

    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;

}
