package kozlov.yandex.backend.school.yandexbackend.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiError {
    private HttpStatus status;
    private String message;
    private Integer code;

    public ApiError(HttpStatus status, String message, Integer code) {
        super();
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
