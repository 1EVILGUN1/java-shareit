# ShareIt - сервис совместного использования вещей 🛠️📦

![Shareit Banner](image_shareit.jpeg)

## О проекте

ShareIt - это микросервисное Spring Boot приложение для организации шеринга вещей между пользователями. Сервис решает проблему временного доступа к предметам без необходимости их покупки.

**Основной функционал:**
- Аренда вещей на определенный период
- Управление списком доступных предметов
- Система бронирования с защитой от двойного бронирования
- Запросы на отсутствующие в системе вещи

## 🛠 Технологический стек

### Основные технологии
| Компонент       | Технология                          |
|-----------------|-------------------------------------|
| Бэкенд          | Java 21, Spring Boot 3.x            |
| База данных     | PostgreSQL 16                       |
| Контейнеризация | Docker                              |
| Сборка          | Maven                               |

### Зависимости проекта
```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Вспомогательные -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Тестирование -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 🚀 Запуск проекта

### Системные требования
- JDK 21+
- Docker и Docker Compose
- Maven 3.8+

### Инструкция по развертыванию

1. Собрать проект:
```bash
mvn clean package
```

2. Запустить контейнеры:
```bash
docker-compose up -d
```

Сервисы будут доступны:
- Gateway: http://localhost:8080
- Server: http://localhost:9090
- PostgreSQL: порт 6541

## 📡 API Endpoints

Основные конечные точки:
- `/users` - управление пользователями
- `/items` - работа с предметами
- `/bookings` - бронирование вещей
- `/requests` - запросы на новые предметы

## 🧪 Тестирование

Проект включает:
- Юнит-тесты с Mockito
- Интеграционные тесты
- Проверка качества кода через:
    - Checkstyle
    - SpotBugs
    - JaCoCo (покрытие кода)

## 🔮 Планы по развитию

1. **Авторизация и безопасность**:
    - Добавить JWT аутентификацию
    - Реализовать ролевую модель (USER, ADMIN)

2. **Улучшения API**:
    - Пагинация для эндпоинтов
    - Расширенный поиск с фильтрами

3. **Оптимизации**:
    - Кеширование с Redis
    - Асинхронная обработка запросов

4. **Мониторинг**:
    - Prometheus + Grafana
    - Логирование в ELK-стек

## 📄 Конфигурация

Основные настройки:
```properties
# Server
server.port=9090
spring.datasource.url=jdbc:postgresql://localhost:6541/shareit
spring.datasource.username=shareit
spring.datasource.password=shareit

# Gateway
shareit-server.url=http://localhost:9090
```

## 📦 Структура проекта

```
shareit/
├── gateway/        # API Gateway
├── server/         # Основной сервис
└── docker-compose.yml
```