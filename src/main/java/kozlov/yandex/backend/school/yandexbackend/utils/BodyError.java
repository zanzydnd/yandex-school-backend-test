package kozlov.yandex.backend.school.yandexbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
//Тело response для ошибочных запросов
public class BodyError {
    private final String message;
    private final Integer code;
}
