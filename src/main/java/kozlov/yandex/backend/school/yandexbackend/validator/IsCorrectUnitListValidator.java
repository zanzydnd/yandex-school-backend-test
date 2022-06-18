package kozlov.yandex.backend.school.yandexbackend.validator;

import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class IsCorrectUnitListValidator implements ConstraintValidator<IsCorrectUnitList, List<ShopUnitDto>> {
    @Override
    public boolean isValid(List<ShopUnitDto> shopUnitDtos, ConstraintValidatorContext constraintValidatorContext) {
        Map<UUID, ShopUnitDto> dtoMap = new HashMap<>();
        AtomicBoolean contains = new AtomicBoolean(true);
        shopUnitDtos.forEach(shopUnitDto -> {
            if (dtoMap.containsKey(shopUnitDto.getId())) {
                contains.set(false);
                return;
            }
            dtoMap.put(shopUnitDto.getId(), shopUnitDto);
        });
        return contains.get();
    }
}
