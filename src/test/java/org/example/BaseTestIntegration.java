package org.example;

import com.zaxxer.hikari.HikariConfig;
import org.example.database.DataSource;
import org.example.database.DbUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class BaseTestIntegration {
    private static PostgreSQLContainer<?> container;

    @BeforeAll
    public static void setUp() {
        container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        container.start();

        String jdbcUrl = container.getJdbcUrl();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        DataSource.init(config);
        DbUtils.startTest();
    }


    @AfterAll
    public static void tearDown() {
        if (container != null) {
            container.stop();
        }
    }
}
