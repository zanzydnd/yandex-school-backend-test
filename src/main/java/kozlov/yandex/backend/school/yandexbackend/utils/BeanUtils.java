package kozlov.yandex.backend.school.yandexbackend.utils;

import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Служит для маппинга метода ShopUnitRepositoryInterface.getAllWithChildrenAndAveragePrice в dto

public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * List <Map <String, Object >> преобразуется в List <T>
     *
     * @param mapList
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> List<T> toList(List<Map<String, Object>> mapList, Class<T> clazz) throws IllegalAccessException, InstantiationException {

        if (mapList == null || clazz == null) {
            return null;
        }
        List<T> list = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            T t = clazz.newInstance();
            copyProperties(map, t);
            list.add(t);
        }
        return list;
    }

    /**
     * Копировать атрибуты к объектам с карты
     *
     * @param map
     * @param target
     */
    public static void copyProperties(Map<String, Object> map, Object target) {
        if (map == null || target == null || map.isEmpty()) {
            return;
        }
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() == null) {
                continue;
            }
            try {
                String key = targetPd.getName();
                Object value = map.get(key);

                setValue(target, targetPd, value);
            } catch (Exception ex) {
                throw new FatalBeanException("Could not copy properties from source to target", ex);
            }
        }
    }

    /**
     * Установите значение для целевого компонента
     *
     * @param target
     * @param targetPd
     * @param value
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void setValue(Object target, PropertyDescriptor targetPd, Object value) throws IllegalAccessException, InvocationTargetException, InvocationTargetException {
        // Здесь, чтобы определить, пусто ли следующее значение
        if (value != null && target!=null)  {
            Method writeMethod = targetPd.getWriteMethod();
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }
            writeMethod.invoke(target, value);
        }
    }
}
