package kozlov.yandex.backend.school.yandexbackend.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = IsCorrectUnitListValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsCorrectUnitList {
    String message() default "List Can't Contain 2 similar Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
