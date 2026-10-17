package ru.atnagullova.cloud_storage;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.atnagullova.cloud_storage.repository.UserRepository;

import java.nio.file.Path;
import java.time.Duration;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @ServiceConnection(name = "redis")
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    static final GenericContainer<?> minio = new GenericContainer<>(DockerImageName
            .parse("quay.io/minio/aistor/minio:RELEASE.2026-04-11T03-20-12Z"))
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin123")
            .withFileSystemBind(Path.of("minio.license").toAbsolutePath().toString(),
                    "/minio.license", BindMode.READ_ONLY)
            .withCommand("server", "/data", "--address", ":9010",
                    "--console-address", ":9011", "--license",  "/minio.license")
            .withExposedPorts(9010, 9011)
            .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9010))
            .withStartupTimeout(Duration.ofMinutes(1L));

    static {
        postgres.start();
        redis.start();
        minio.start();
    }

    @DynamicPropertySource
    static void configureMinio(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("minio.url",
                () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9010));
        dynamicPropertyRegistry.add("minio.access-key", () -> "minioadmin");
        dynamicPropertyRegistry.add("minio.secret-key", () -> "minioadmin123");
    }

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    protected String getContentBody(String username, String password) {

        return "{\"username\":\""
                + username
                + "\",\"password\":\""
        + password + "\"}";
    }
}
