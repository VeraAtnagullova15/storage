package ru.atnagullova.cloud_storage;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
@OpenAPIDefinition(
        tags = {
                @Tag(name = "Authentication module", description = "Operations with authentication users"),
                @Tag(name = "Cloud storage module", description = "Operations with cloud storage")
        }
)
@SpringBootApplication
@ConfigurationPropertiesScan
public class StorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

}
