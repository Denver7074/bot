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
https://github.com/Denver7074/bot.git
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
![image](https://github.com/Denver7074/bot/assets/119703412/3b91ba15-9359-4ed8-8b49-a42e73cb239f)
### Старт
![image](https://github.com/Denver7074/bot/assets/119703412/4c552880-4189-4ff3-999f-3efa5b5cee66)
### Помощь
![image](https://github.com/Denver7074/bot/assets/119703412/e7fee217-e82e-474e-9c02-568fcb3fc27e)
### О себе
![image](https://github.com/Denver7074/bot/assets/119703412/5fbf25ff-0b5a-4f9e-97f5-14c7efb452ed)
### Работа с поверкой
![image](https://github.com/Denver7074/bot/assets/119703412/8f6d51ed-4539-4fac-89a8-d633036cbaf0)
### Управление e-mail оповещением
В данном разделе вы можете:
- добавлять email на который необходимо отправлять оповещение об окончании срока поверки на приборы
- отключать оповещение от конкретного email адреса
![image](https://github.com/Denver7074/bot/assets/119703412/71c94619-1182-48cc-aaa9-d66885e12150)

Для подключения оповещения на конкретный email необходимо ответным сообщением ввести почту и отправить. Данная почта будет в дальнейшем отображаться в виде кнопки к такому же сообщению. Чтобы отключить оповещение от конкретной почты нажмите на соответствующую кнопку с данным адресом и данная почта удалиться.
### Просмотр списка CИ
В данном разделе СИ, которые стоят у вас на контроле выгружаются в формате excel.
![image](https://github.com/Denver7074/bot/assets/119703412/a003e369-37b0-4c0c-aaa6-be5a2ff7734d)
![image](https://github.com/Denver7074/bot/assets/119703412/f7f1616b-facc-4290-90dd-3ddf8952dd99)
### Изменить название СИ
В данном разделе предоставляется возможность изменения названия и модификации вашего прибора. Что бы внести изменения вам предоставляется excel файл с общим перечнем приборов, которые стоят закреплены у вас. Необходимо в необходимых приборах изменить название и/или модификацию и в ответном сообщении прикрепить этот файл.
![image](https://github.com/Denver7074/bot/assets/119703412/4dbe3037-5386-4852-8702-b312233ba819)
![image](https://github.com/Denver7074/bot/assets/119703412/bc9a56f4-ebbd-41bf-b19a-fa5592a6d6d3)
### Поиск и добавление СИ
В данном разделе в можете провести поиск СИ:
-  ручной ввод ответного сообщения как показано в примере ввода;
-  автоматический массовый поиск оборудования и сохранение в бд с помощью заполнения excel фйла и ответной отправки.

![image](https://github.com/Denver7074/bot/assets/119703412/db21c645-aaf1-4a26-9047-757366a100c8)
![image](https://github.com/Denver7074/bot/assets/119703412/a0cc3379-2d6a-408b-8c28-3ebe87701312)
![image](https://github.com/Denver7074/bot/assets/119703412/27413fd2-507e-419e-8a21-fb035033c90c)
