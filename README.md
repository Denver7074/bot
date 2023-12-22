# Телеграмм бот для отслеживания сроков поверки приборов
Телеграмм бот берет информацию о приборах во ФГИС Аршин по номеру ФГРСИ и заводскому номеру. Бот распознает команды с помощью yandex speech kit.
## Пользователь может следующее: 
1. поставить прибор на контроль для отслеживания сроков поверки (сохранить в базу данных); 
2. получать оповещение на e-mail и в телеграмм, если срок поверки истекает через 30 дней и/или менее;
3. выгружать весь список приборов в формате excel;
4. изменять название прибора и модификации, если она не соответствует руководству по эксплуатации.
## Требование к запуску
- Java 17
- Spring Boot 3.2.0
- Spring Vault 1.13.3
- Redis latest
- PostgreSQL 15-alpine
- Docker 24.0.6
- Сборщик проектов Gradle
## Развертывание PostgreSQL, Redis, Spring Vault с помощью Docker Compose
Это приложение использует Docker Compose для удобного развертывания. Вот пример файла `docker-compose.yml`:
```yaml
version: "3.9"

services:
  postgres:
    container_name: postgres
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: "bot"
      POSTGRES_USER: "test"
      POSTGRES_PASSWORD: "test"
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U test -d bot"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s
    restart: unless-stopped

  redis:
    image: "redis:latest"
    container_name: "redis"
    ports:
      - "6379:6379"
    networks:
      - postgres

  vault:
    image: vault:1.13.3
    container_name: vault_dev
    ports:
      - "8200:8200"
    environment:
      VAULT_DEV_ROOT_TOKEN_ID: myroot
      VAULT_DEV_LISTEN_ADDRESS: "0.0.0.0:8200"
    cap_add:
      - IPC_LOCK
    command: server -dev

networks:
  postgres:
    driver: bridge
```
## Конфигурации application.properties
### PostgreSQL
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/bot
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=test
spring.datasource.password=test
spring.datasource.hikari.schema=test
```
### Redis
```bash
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type= redis
```
### Vault
```bash
spring.cloud.vault.uri=http://localhost:8200
spring.cloud.vault.token=myroot
spring.cloud.vault.path=Путь к вашим ключам
```
## Настройка ключей в Vault
![image](https://github.com/Denver7074/bot/assets/119703412/d38273e3-b0f5-49ab-97fe-9501b17bd7d9)

Для работы вам необходимо создать следующие ключи
- botName - название телеграмм-бота
- botToken - токен телеграмм-бота
- userName - email с какого будет производиться рассылка
- password - пароль (ключ) от данной почты
- yandexKey - ключ от yandex speech

botName и botToken получают в телеграмм-боте BotFather.

userName и password в данном приложении использовалось от сервиса Google Mail.
## Запуск
Клонируйте репозиторий из Git
```bash
git clone https://github.com/Denver7074/test.git
```
Используя сборщик проектов `Gradle` соберите проект.
## Exception
- E001 "Cущность по идентификатору не найдена";
- E002 "Прибор с данными не найден. Проверьте введенные данные и повторите ввод или вернитесь в главное меню.";
- E003 "Неккоректный ввод почты. Повторите ввод почты.";
- E004 "Прибор уже стоит на контроле у вас. Повторите поиск или выберите команду из меню.";
- E005 "У вас пока нет приборов чтобы выполнить это действие";
## Работа с приложением
### Главное меню
### Помощь
### О себе
### Работа с поверкой
### Управление e-mail оповещением
### Просмотр списка CИ
### Изменить название СИ
### Поиск и добавление СИ

