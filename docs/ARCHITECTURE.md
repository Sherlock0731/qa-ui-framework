# Архитектура фреймворка

## Обзор

UI Test Automation Framework - это современный фреймворк для автоматизации тестирования веб-приложений, построенный на базе Selenide, JUnit 5 и поддерживающий многопоточное выполнение тестов.

## Структура проекта

```
qa-ui-framework/
├── src/main/java/qa/autotest/
│   ├── app/dto/                 # Data Transfer Objects
│   │   ├── UserDto.java
│   │   ├── ProductDto.java
│   │   ├── CartDto.java
│   │   └── CheckoutDto.java
│   ├── pages/                   # Page Object Model
│   │   ├── BasePage.java
│   │   ├── LoginPage.java
│   │   ├── InventoryPage.java
│   │   ├── ProductDetailsPage.java
│   │   ├── CartPage.java
│   │   ├── CheckoutStepOnePage.java
│   │   ├── CheckoutStepTwoPage.java
│   │   └── CheckoutCompletePage.java
│   └── framework/
│       ├── drivers/
│       │   └── DriverManager.java  # WebDriver management
│       ├── config/
│       │   ├── TestConfig.java    # Configuration interface
│       │   └── ConfigFactory.java  # Config factory
│       └── utils/                  # Utility classes
└── src/test/java/examples/
    ├── BaseTest.java
    ├── login/
    ├── inventory/
    ├── cart/
    ├── checkout/
    └── navigation/
```

## Ключевые компоненты

### 1. Data Transfer Objects (DTO)

**Назначение:** Моделирование данных предметной области

**Особенности:**
- Использование Lombok для минимизации boilerplate кода
- Builder pattern для гибкого создания объектов
- Immutable объекты (где применимо)

**Примеры:**
```java
@Data
@Builder
public class UserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
```

### 2. Page Object Model

**Назначение:** Инкапсуляция логики взаимодействия с UI

**Иерархия:**
```
BasePage (общие элементы: header, cart, burger menu)
  ├── LoginPage
  ├── InventoryPage
  ├── ProductDetailsPage
  ├── CartPage
  ├── CheckoutStepOnePage
  ├── CheckoutStepTwoPage
  └── CheckoutCompletePage
```

**Принципы:**
- Одна страница = один класс
- Методы возвращают Page Objects для fluent interface
- Использование Selenide для упрощения работы с элементами
- Allure Steps для лучшей отчетности

**Пример:**
```java
@Step("Login with username: {username}")
public InventoryPage login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    loginButton.click();
    return new InventoryPage();
}
```

### 3. WebDriver Management

**DriverManager** управляет жизненным циклом WebDriver:

**Возможности:**
- Автоматическое управление драйверами (WebDriverManager)
- Поддержка 4 браузера: Chrome, Firefox, Edge, Safari
- Thread-safe реализация через ThreadLocal
- Поддержка headless режима
- Интеграция с Selenium Grid
- Настройка timeouts и размера окна

**Пример:**
```java
public static void initDriver(TestConfig config) {
    String browser = config.browser();
    WebDriver webDriver = createLocalDriver(browser, config.browserHeadless());
    driver.set(webDriver);
    WebDriverRunner.setWebDriver(webDriver);
}
```

### 4. Configuration Management

**Использует Owner library для управления конфигурациями:**

**Приоритет загрузки:**
1. System properties (-Dkey=value)
2. Environment variables
3. Environment-specific properties (local.properties, ci.properties)
4. Default properties (default.properties)

**Пример:**
```java
@Config.Sources({
    "system:properties",
    "system:env",
    "classpath:config/${env}.properties",
    "classpath:config/default.properties"
})
public interface TestConfig extends Config {
    @Key("browser")
    String browser();
}
```

### 5. Test Base Class

**BaseTest** предоставляет:
- Инициализацию WebDriver перед каждым тестом
- Очистку WebDriver после теста
- Доступ к конфигурации
- Логирование
- Поддержку параллельного выполнения

## Паттерны проектирования

### 1. Page Object Pattern
Инкапсуляция логики страниц в отдельные классы

### 2. Builder Pattern
Гибкое создание DTO объектов

### 3. Factory Pattern
Создание конфигураций и драйверов

### 4. Singleton Pattern
Управление конфигурацией (thread-safe)

### 5. Fluent Interface
Цепочка вызовов методов для читаемости

## Многопоточность

### Thread-Local WebDriver
Каждый поток имеет свой экземпляр WebDriver:
```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
```

### Параллельное выполнение JUnit 5
```java
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {
    // ...
}
```

### Логирование
Каждый поток пишет в отдельный лог-файл:
```xml
<appender name="SIFT" class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>threadName</key>
    </discriminator>
</appender>
```

## Интеграция с Allure

### Аннотации
- `@Epic` - группировка по эпикам
- `@Feature` - группировка по функциональности
- `@Story` - группировка по user stories
- `@Step` - описание шагов
- `@Severity` - приоритет теста

### Вложения
- Скриншоты при ошибках
- Логи выполнения
- HTML исходный код страницы

## CI/CD Integration

### GitHub Actions
- Автоматический запуск тестов
- Параллельное выполнение
- Сохранение артефактов (отчеты, логи, скриншоты)
- Поддержка разных браузеров

### Docker
- Изолированное окружение
- Предустановленные браузеры
- Headless режим по умолчанию

## Лучшие практики

1. **Использование Page Objects** для инкапсуляции логики UI
2. **DTO для моделирования данных** вместо Map/JSON
3. **Fluent interface** для читаемости тестов
4. **Thread-safe** реализация для параллельности
5. **Централизованная конфигурация** через Owner
6. **Детальное логирование** с поддержкой многопоточности
7. **Allure steps** для прозрачных отчетов
8. **Автоматическое управление драйверами** через WebDriverManager
