package kozlov.yandex.backend.school.yandexbackend;

import kozlov.yandex.backend.school.yandexbackend.common.PostgresInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Roman Leontev
 * 12:00 19.01.2022
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-containers-flyway")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class AbstractTestIT {
}
