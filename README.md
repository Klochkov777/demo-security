# Реализация аутентификации и авторизации с использованием Spring Security и JWT

## 1. Введение

**Название проекта:** Реализация аутентификации и авторизации с использованием Spring Security и JWT  
**Цель:** Создать пример использования Spring Security для аутентификации и авторизации с использованием JWT.

## 2. Описание проекта

Проект представляет собой простое Spring Boot приложение, которое управляет доступом к данным с помощью JWT авторизации. В приложении реализована система аутентификации и авторизации с JWT.

## 3. Структура проекта

### Настройка проекта

Проект создан с помощью Spring Initializr.  
В `pom.xml` добавлены зависимости:

- Spring Web
- Spring Data JPA
- Spring Security
- Spring Open Api для формирования документации API
- Зависимости для работы JWT

### Создание сущностей

- **User**
  - Поля: `id`, `username`, `email`, `password` (реализует интерфейс `UserDetails` для авторизации пользователя)
  
- **RefreshToken**
  - Поля: `id`, `token`, `user`, `expiryDate` (служит для хранения информации о refreshToken для обновления accessToken)
  - Связь между сущностями: у одного токена один юзер.

### Создание репозиториев

- **UserRepository:** Интерфейс для работы с сущностью `User`, расширяющий `JpaRepository`.
- **RefreshTokenRepository:** Интерфейс для работы с сущностью `RefreshToken`, расширяющий `JpaRepository`.

### Создание сервисов

- **AuthenticationService:**
  - Методы: Регистрация пользователя, вход по логину и паролю ранее зарегистрированного пользователя.
  
- **JwtService:**
  - Методы: публичные для получения имени пользователя, генерации `accessToken` и `refreshToken`, проверки валидности токена.
  
- **RefreshTokenService:**
  - Методы: для создания `refreshToken`, поиска entity по токену, проверки времени действия, удаления по username.

### Создание контроллеров

- **AuthController:**
  - Созданы endpoints для регистрации, входа, обновления `accessToken` по `refreshToken`, выхода.
  - Документация API с помощью аннотаций через Open API.

- **TestController:**
  - Созданы endpoints для ручного тестирования. Три endpoints для захода под ролями `USER`, `ADMIN` или `SUPER_ADMIN`.

Также созданы фильтры, перечисления, DTO, исключения и их обработчик.

## 4. Развертывание приложения (пример)

Для быстрого развертывания приложения был написан `Dockerfile`. Предлагается развертывание по следующим шагам:

1. Клонировать проект с репозитория: `git clone git@github.com:Klochkov777/demo-security.git`
2. Перейти в корень проекта.
3. Ввести команду: `mvn clean verify` (для этого должен быть установлен Maven).
4. Запустить сборку image командой: `docker build -t demo-security:latest .` (можно использовать своё название image, если не указать, Docker сгенерирует его случайным образом).
5. Запустить сборку и осуществить запуск контейнера командой: `docker run --name demo-security --network=host -td demo-security:latest` (где `demo-security:latest` — название image, вместо него можно поставить ID).

Другой способ:

1. Клонировать проект с репозитория.
2. Перейти в корень проекта.
3. Ввести команду: `mvn clean verify` (для этого должен быть установлен Maven).
4. Ввести команду: `java -jar ./target/demo-security-0.0.1-SNAPSHOT.jar`.

> **Внимание:** Использовалась Java 21, поэтому для корректной работы должна быть установлена аналогичная версия.

Для работы приложения необходимо использовать СУБД PostgreSQL с настройками, указанными в файле `application.yml` (либо изменить их).

SQL скрипты создания отношений в БД:

```sql
create table public.users
(
    id       uuid                      not null
        primary key,
    username varchar(255)              not null,
    email    varchar(255)              not null,
    password varchar(255)              not null,
    role     varchar(255) default USER not null
);

create table public.refresh_tokens
(
    id          bigserial
        primary key,
    token       varchar(255) not null
        unique,
    user_id     uuid         not null
        constraint fk_user_id
            references public.users,
    expiry_date timestamp    not null
);

```
## 5. Дополнительная информация

В рамках данной задачи не было предусмотрено логгирование или миграция, поэтому это не реализовано. Проект демонстрирует работу аутентификации и авторизации с использованием JWT. При регистрации пользователь создается с правами `USER`, смена прав на `ADMIN` или `SUPER_ADMIN` производится вручную в базе данных.

**Примечание:** В Java-приложениях права обычно указываются с префиксом `ROLE_` (например: `ROLE_ADMIN`). В данном примере конфигурация настроек **без** этого префикса (например: `ADMIN`).






