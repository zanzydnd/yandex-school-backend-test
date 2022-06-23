package kozlov.yandex.backend.school.yandexbackend.common;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;


public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:12.4")
            .withDatabaseName("backend")
            .withUsername("postgres")
            .withPassword("postgres")
            .withNetworkAliases("pgdb");

    static {
        dbContainer.start();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + dbContainer.getJdbcUrl(),
                "spring.datasource.username=" + dbContainer.getUsername(),
                "spring.datasource.password=" + dbContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
