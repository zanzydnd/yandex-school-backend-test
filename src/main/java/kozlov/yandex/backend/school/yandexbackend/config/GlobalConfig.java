package kozlov.yandex.backend.school.yandexbackend.config;

import kozlov.yandex.backend.school.yandexbackend.dto.ReturnItemDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ReturnSalesDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitHistoryModel;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "kozlov.yandex.backend.school.yandexbackend")
@EnableJpaRepositories(basePackages = "kozlov.yandex.backend.school.yandexbackend.jpa.repository")
@ComponentScan("kozlov.yandex.backend.school.yandexbackend.service")
public class GlobalConfig {
    @Bean
    public ModelMapper ModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(ShopUnitHistoryModel.class, ReturnItemDto.class)
                .addMapping(ShopUnitHistoryModel::getOriginId, ReturnItemDto::setId)
                .addMapping(ShopUnitHistoryModel::getParentId, ReturnItemDto::setParentId);
        return modelMapper;
    }
}
