# Учебная платформа (LMS) на Spring Boot

## Описание проекта

Проект представляет собой учебную платформу (Learning Management System), разработанную в рамках учебного задания. Система позволяет управлять курсами, пользователями, заданиями, тестами и отслеживать прогресс обучения. Проект реализует полный цикл CRUD-операций для всех сущностей, демонстрирует работу с ORM (Hibernate/JPA), Spring Data JPA и PostgreSQL.

## Технологический стек

- **Java 17+** - язык программирования
- **Spring Boot 3.x** - фреймворк для создания приложений
- **Spring Data JPA** - работа с базами данных
- **Hibernate (JPA)** - ORM для маппинга объектов в БД
- **PostgreSQL** - основная база данных
- **H2 Database** - база данных для тестов
- **Maven** - система сборки проекта
- **Lombok** - генерация boilerplate кода
- **Jakarta Persistence** - API для работы с персистентностью

## Функциональные возможности

1. **Управление пользователями** - регистрация, аутентификация, роли (STUDENT, TEACHER, ADMIN, MODERATOR)
2. **Управление курсами** - создание, редактирование, удаление курсов с категориями
3. **Структура обучения** - курсы содержат модули, модули содержат уроки
4. **Задания и решения** - создание заданий, отправка решений, оценка работ
5. **Тестирование** - создание тестов с вопросами и вариантами ответов, прохождение тестов
6. **Отзывы о курсах** - студенты могут оставлять отзывы и рейтинги
7. **Система тегов** - категоризация курсов с помощью тегов
8. **REST API** - полный набор API для взаимодействия с системой

## Структура базы данных

Проект содержит 18 сущностей с различными типами связей:
- User (Пользователь) - Profile (Профиль) - OneToOne
- User - Course (Преподаватель) - ManyToOne
- User - Enrollment (Запись на курс) - ManyToMany через сущность Enrollment
- Course - Module (Модуль) - OneToMany
- Module - Lesson (Урок) - OneToMany
- Lesson - Assignment (Задание) - OneToMany
- Assignment - Submission (Решение) - OneToMany
- Module - Quiz (Тест) - OneToOne
- Quiz - Question (Вопрос) - OneToMany
- Question - AnswerOption (Вариант ответа) - OneToMany
- Course - Tag (Тег) - ManyToMany

## Требования к окружению

- Java 17 или выше
- Maven 3.6 или выше
- PostgreSQL 12 или выше
- Git

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/timantion1231/LMSHW.git
```

### 2. Настройка базы данных PostgreSQL

Создайте базу данных в PostgreSQL:

```sql
CREATE DATABASE lmsdb;
CREATE USER lmsuser WITH PASSWORD 'lmspassword';
GRANT ALL PRIVILEGES ON DATABASE lmsdb TO lmsuser;
```

### 3. Настройка приложения

Отредактируйте файл `src/main/resources/application.yaml` при необходимости:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lmsdb
    username: lmsuser
    password: lmspassword
```

### 4. Сборка и запуск

#### С помощью Maven:

```bash
# Сборка проекта
mvn clean package

# Запуск приложения
mvn spring-boot:run
```

#### С помощью IDE:
- Импортируйте проект как Maven проект
- Запустите класс `com.tima.lms.LmsApplication`

### 5. Проверка работы

Приложение запустится на порту 8080. Проверить можно по адресу:
- http://localhost:8080/api/users (должен вернуть пустой список или ошибку 404)

## API Endpoints

### Пользователи
- `POST /api/users` - создание пользователя
- `GET /api/users/{id}` - получение пользователя по ID
- `GET /api/users` - получение всех пользователей
- `PUT /api/users/{id}` - обновление пользователя
- `DELETE /api/users/{id}` - удаление пользователя

### Категории
- `POST /api/categories` - создание категории
- `GET /api/categories/{id}` - получение категории по ID
- `GET /api/categories` - получение всех категорий
- `PUT /api/categories/{id}` - обновление категории
- `DELETE /api/categories/{id}` - удаление категории

### Курсы
- `POST /api/courses` - создание курса
- `GET /api/courses/{id}` - получение курса по ID
- `GET /api/courses` - получение всех курсов
- `PUT /api/courses/{id}` - обновление курса
- `DELETE /api/courses/{id}` - удаление курса

### Модули
- `POST /api/modules` - создание модуля
- `GET /api/modules/{id}` - получение модуля по ID
- `GET /api/modules` - получение всех модулей
- `GET /api/modules/by-course/{courseId}` - получение модулей по курсу
- `PUT /api/modules/{id}` - обновление модуля
- `DELETE /api/modules/{id}` - удаление модуля

### Уроки
- `POST /api/lessons` - создание урока
- `GET /api/lessons/{id}` - получение урока по ID
- `GET /api/lessons` - получение всех уроков
- `GET /api/lessons/by-module/{moduleId}` - получение уроков по модулю
- `PUT /api/lessons/{id}` - обновление урока
- `DELETE /api/lessons/{id}` - удаление урока

### Записи на курсы
- `POST /api/enrollments` - запись на курс
- `GET /api/enrollments/{id}` - получение записи по ID
- `GET /api/enrollments` - получение всех записей
- `PATCH /api/enrollments/{id}/status` - обновление статуса записи
- `DELETE /api/enrollments/{id}` - удаление записи

### Задания
- `POST /api/assignments` - создание задания
- `GET /api/assignments/{id}` - получение задания по ID
- `GET /api/assignments` - получение всех заданий
- `PUT /api/assignments/{id}` - обновление задания
- `DELETE /api/assignments/{id}` - удаление задания

### Решения заданий
- `POST /api/submissions` - отправка решения
- `GET /api/submissions/{id}` - получение решения по ID
- `GET /api/submissions` - получение всех решений
- `PUT /api/submissions/{id}/grade` - оценка решения
- `DELETE /api/submissions/{id}` - удаление решения

### Демонстрация LAZY загрузки
- `GET /api/demo/lazy-exception/{courseId}` - демонстрация LazyInitializationException
- `GET /api/demo/lazy-fixed/{courseId}` - пример решения проблемы LAZY

## Тестирование

### Запуск тестов

```bash
# Запуск всех тестов
mvn test

# Запуск конкретного теста
mvn test -Dtest=IntegrationTest
mvn test -Dtest=LazyLoadingTest
```

### Описание тестов

1. **IntegrationTest** - интеграционные тесты, проверяющие:
   - Создание схемы БД
   - CRUD операции для всех сущностей
   - Каскадное удаление
   - Бизнес-логику (запись на курс, отправка решений)
   - Полный путь пользователя

2. **LazyLoadingTest** - тесты для демонстрации LazyInitializationException
   - Демонстрация проблемы LAZY загрузки
   - Решение через @Transactional аннотацию

3. **LmsApplicationTests** - базовые тесты контекста Spring

## Демонстрация Lazy Loading

Проект специально настроен для демонстрации проблемы LazyInitializationException:
- Все связи `@OneToMany` и `@ManyToMany` используют `FetchType.LAZY`
- Контроллер `/api/demo/lazy-exception/{id}` демонстрирует ошибку
- В тестах показываются способы решения проблемы

## Структура проекта

```
src/main/java/com/tima/lms/
├── LmsApplication.java                    # Главный класс приложения
├── entity/                                # Сущности JPA (18 классов)
│   ├── User.java
│   ├── Profile.java
│   ├── Course.java
│   └── ...
├── repository/                            # Репозитории Spring Data JPA (15 интерфейсов)
│   ├── UserRepository.java
│   ├── CourseRepository.java
│   └── ...
├── service/                               # Сервисы (8 классов)
│   ├── UserService.java
│   ├── CourseService.java
│   └── ...
├── controller/                            # Контроллеры REST API (8 классов)
│   ├── UserController.java
│   ├── CourseController.java
│   └── ...
└── dto/                                   # DTO классы (12 классов)
    ├── request/                           # DTO для запросов
    └── response/                          # DTO для ответов

src/main/resources/
├── application.yaml                       # Конфигурация приложения
└── application-test.yaml                  # Конфигурация для тестов

src/test/java/com/tima/lms/                # Интеграционные тесты
├── IntegrationTest.java                   # Основные интеграционные тесты
├── LazyLoadingTest.java                   # Тесты LAZY загрузки
└── LmsApplicationTests.java               # Базовые тесты
```

## Использование с Postman

Полная коллекция запросов для тестирования доступна в файле `postman_collection.json`. Основной сценарий тестирования:

1. Создать категорию
2. Создать преподавателя и студентов
3. Создать курс
4. Создать модуль и урок
5. Записать студентов на курс
6. Создать задание
7. Отправить решения
8. Оценить решения
9. Проверить LAZY загрузку
