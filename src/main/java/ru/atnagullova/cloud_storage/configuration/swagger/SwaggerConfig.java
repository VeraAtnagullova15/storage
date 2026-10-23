package ru.atnagullova.cloud_storage.configuration.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    public OpenAPI openAPI() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Vera Atnagullova");
        myContact.setEmail("verakrasnova56@gmail.com");

        Info info = new Info();
        info.setTitle("Cloud storage API");
        info.setVersion("1.0");
        info.setContact(myContact);
        info.setDescription("This API expose endpoints to manage cloud storage");

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
