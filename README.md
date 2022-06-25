# Запуск приложения
- Заполняем application.properties
- ./mvnw clean package -DskipTests
- docker-compose up --build -d

Будет доступен по 80 порту
# Описание

- Repositories:
    - Я использовал spring-data jpa, но в большинстве методов вы увидите испольнение сырых sql. 
        1. Hibernate не поддерживает recursive - запросы
        2. Вложенные запросы
    
    - ShopUnitRepositoryInterface - содержит методы для работы с моделью ShopUnit
    - ShopUnitHistoryRepositoryInterface - содержит методы для работы с моделью ShopUnitHistory

- Для валидации входящих запросов я использовал BeanValidators и HibernateListeners

- Бизнес логика приложения находится в service

- Остальное представлено ввиде комментариев в коде
