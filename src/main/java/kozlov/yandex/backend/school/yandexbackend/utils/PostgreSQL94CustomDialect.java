package kozlov.yandex.backend.school.yandexbackend.utils;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.array.UUIDArrayType;
import org.hibernate.dialect.PostgreSQL94Dialect;

// Поддержка uuid-array для hibernate
public class PostgreSQL94CustomDialect extends PostgreSQL94Dialect {

    public PostgreSQL94CustomDialect() {
        super();
        this.registerHibernateType(2003, UUIDArrayType.class.getName());
    }

}