package kozlov.yandex.backend.school.yandexbackend.validator;

import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsCorrectUnitValidator implements ConstraintValidator<IsCorrectUnit, ShopUnitDto> {

    @Override
    public boolean isValid(ShopUnitDto shopUnitDto, ConstraintValidatorContext constraintValidatorContext) {
        if (shopUnitDto.getPrice() == null && shopUnitDto.getType() == ShopUnitType.OFFER) return false;
        if (shopUnitDto.getPrice() != null && shopUnitDto.getType() == ShopUnitType.CATEGORY) return false;

        return true;
    }
}
