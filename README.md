# CRM System - Система управления продавцами и транзакциями

## Оглавление
- [Описание проекта](#описание-проекта)
- [Функциональность](#функциональность)
- [Технологии](#технологии)
- [Требования](#требования)
- [Установка и запуск](#установка-и-запуск)
- [Качество кода](#качество-кода)
- [API Документация](#api-документация)
- [Примеры использования](#примеры-использования)
- [Структура проекта](#структура-проекта)

## Описание проекта
CRM System - это RESTful API для управления продавцами и их транзакциями. Система предоставляет возможности для аналитики продаж, определения продуктивных периодов и мониторинга эффективности продавцов.

## Функциональность

### Управление продавцами
| Функция | Описание |
|---------|----------|
| CRUD | Создание, чтение, обновление, удаление продавцов |
| Валидация | Проверка корректности данных и уникальности записей |
| Дата регистрации | Автоматическая установка даты регистрации продавца |

### Управление транзакциями
| Функция | Описание                                          |
|---------|---------------------------------------------------|
| CRUD | Создание и просмотр транзакций                    |
| Типы оплаты | Поддержка CASH, CARD, TRANSFER                    |
| Привязка к продавцу | Автоматическая привязка транзакции к продавцу     |
| Валидация | Проверка корректности суммы и типа оплаты         |
| Дата создания | Автоматическая установка даты создания транзакции |

### Аналитика
| Функция | Описание |
|---------|----------|
| Топ-продавец | Определение наиболее продуктивного продавца за период (DAY, MONTH, QUARTER, YEAR) |
| Низкопроизводительные продавцы | Поиск продавцов с суммой транзакций ниже заданного порога |
| Лучший период | Определение наиболее продуктивного временного интервала для конкретного продавца |

## Технологии
| Категория | Технология |
|-----------|------------|
| Язык | Java 8 |
| Фреймворк | Spring Boot 2.5.14 |
| База данных | PostgreSQL, H2 (для тестирования) |
| Сборка | Gradle |
| Контейнеризация | Docker, Docker Compose |
| Работа с БД | Spring Data JPA |
| Документация API | OpenAPI 3 |
| Тестирование | JUnit 5, Mockito |

## Требования

### Для разработки
- Java 8 или выше
- Gradle 7+
- Docker и Docker Compose

### Для production
- Docker и Docker Compose
- 2GB свободной памяти
- Порты: 8080, 5432, 5050

## Установка и запуск

### Запуск с Docker
1. Клонируйте репозиторий:
    ```bash
    git clone https://github.com/Sweche21/CrmSystem.git
    cd crm-system
    ```


2. После клонирования рекомендуется инициализировать Gradle Wrapper:

    ```bash
    ./gradlew wrapper
    ```

Это создаст необходимые скрипты и файлы для запуска проекта через Gradle на вашей машине.

3. Запустите приложение:
    ```bash
    docker-compose up --build
    ```
3. Приложение будет доступно по адресам:
    - Swagger UI: `http://localhost:8080/swagger-ui.html`
    - PgAdmin: `http://localhost:5050` (email: admin@crm.com, password: admin)


### Настройка базы данных в PgAdmin

Для работы проекта с PostgreSQL через PgAdmin:

1. Запустите PgAdmin.
2. Войдите с учетными данными:
* Email: `admin@crm.com`
* Password: `admin`
3. В дереве объектов выберите **Servers → Create → Server**.
4. Добавьте новый сервер в PgAdmin:
* **Name**: любое имя, например `CRM_DB`
* **Connection → Host name/address**: `postgres` (имя сервиса PostgreSQL в `docker-compose.yml`)
* **Port**: `5432`
* **DB**: `crm_system`
* **Username**: `crm_user`
* **Password**: `crm_password`

4. Нажмите **Save**. Теперь сервер будет отображаться в списке подключений.
5. Внутри сервера создайте базу данных с именем, указанным в `application.yml` (например, `crm_db`).


## Качество кода

* Покрытие тестами: >70%
* Централизованная обработка ошибок через `GlobalExceptionHandler`
* Валидация через кастомные исключения
* Логирование настроено для DEBUG режима

### Коды ошибок

| Код | Описание                  |
| --- | ------------------------- |
| 400 | Ошибка валидации          |
| 404 | Ресурс не найден          |
| 409 | Конфликт (дубликат)       |
| 500 | Внутренняя ошибка сервера |

### Тесты

* Запуск всех тестов:

```bash
./gradlew test
```

* С генерацией отчета покрытия:

```bash
./gradlew jacocoTestReport
```

Отчет доступен в `build/reports/jacoco/test/html/`.

## API Документация

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Примеры использования

* Получение всех продавцов: `GET /api/sellers`
* Создание нового продавца: `POST /api/sellers`
* Создание транзакции: `POST /api/transactions`
* Получение аналитики: `GET /api/analytics/top-seller?period=MONTH`


## Структура проекта

```
crm-system/
├── src/
│   ├── main/java/com/crm/
│   │   ├── CrmApplication.java
│   │   ├── config/OpenApiConfig.java
│   │   ├── controller/
│   │   │   ├── SellerController.java
│   │   │   └── TransactionController.java
│   │   ├── service/
│   │   │   ├── SellerService.java
│   │   │   ├── TransactionService.java
│   │   │   └── AnalyticsService.java
│   │   ├── repository/
│   │   │   ├── SellerRepository.java
│   │   │   └── TransactionRepository.java
│   │   ├── entity/
│   │   │   ├── Seller.java
│   │   │   ├── Transaction.java
│   │   │   └── PaymentType.java
│   │   ├── dto/
│   │   │   ├── SellerDto.java
│   │   │   ├── TransactionDto.java
│   │   │   └── AnalyticsDto.java
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       ├── ValidationException.java
│   │       ├── DuplicateResourceException.java
│   │       ├── AnalyticsException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       ├── application-test.yml
│       └── logback-spring.xml
├── test/java/com/crm/
│   ├── controller/
│   │   ├── SellerControllerTest.java
│   │   └── TransactionControllerTest.java
│   ├── service/
│   │   ├── SellerServiceTest.java
│   │   ├── TransactionServiceTest.java
│   │   └── AnalyticsServiceTest.java
│   ├── repository/
│   │   ├── SellerRepositoryTest.java
│   │   └── TransactionRepositoryTest.java
│   ├── entity/EntityTest.java
│   ├── dto/DtoTest.java
│   └── exception/GlobalExceptionHandlerTest.java
├── build.gradle
├── gradlew
├── gradlew.bat
├── gradle/wrapper/
├── Dockerfile
├── docker-compose.yml
├── .gitignore
└── README.md
```
