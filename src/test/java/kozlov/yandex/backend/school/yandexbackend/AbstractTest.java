package kozlov.yandex.backend.school.yandexbackend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import kozlov.yandex.backend.school.yandexbackend.dto.ResponseNodeShopUnitDto;
import org.checkerframework.checker.units.qual.A;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


//@WebAppConfiguration
public abstract class AbstractTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, clazz);
    }

    static class ResponseNodeShopUnitDtoComparator implements Comparator<ResponseNodeShopUnitDto> {
        @Override
        public int compare(ResponseNodeShopUnitDto o1, ResponseNodeShopUnitDto o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

    protected void sort(ResponseNodeShopUnitDto shopUnitDto) {
        if (shopUnitDto.getChildren() != null && shopUnitDto.getChildren().size() != 0) {
            shopUnitDto.getChildren().forEach(
                    this::sort
            );
            var list = new ArrayList<>(shopUnitDto.getChildren());
            Collections.sort(list,new ResponseNodeShopUnitDtoComparator());
            shopUnitDto.setChildren(list);
        }
    }
}