package kozlov.yandex.backend.school.yandexbackend.utils;

import kozlov.yandex.backend.school.yandexbackend.exception.ApiError;
import kozlov.yandex.backend.school.yandexbackend.exception.BusinessLogicException;
import kozlov.yandex.backend.school.yandexbackend.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomRestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BodyError> handleException(NotFoundException e) {
        BodyError error = new BodyError("Item not found", 404);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BodyError> handleException(Exception e) {
        BodyError error = new BodyError("Validation Failed", 400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
