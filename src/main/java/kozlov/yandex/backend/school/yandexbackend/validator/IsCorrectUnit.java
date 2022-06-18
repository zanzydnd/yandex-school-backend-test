package kozlov.yandex.backend.school.yandexbackend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { IsCorrectUnitValidator.class })
public @interface IsCorrectUnit {
    String message() default "Value is incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
