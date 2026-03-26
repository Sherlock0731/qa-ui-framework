# Архитектура фреймворка

## Обзор

UI Test Automation Framework - это современный фреймворк для автоматизации тестирования веб-приложений, построенный на базе Selenide, JUnit 5 и поддерживающий многопоточное выполнение тестов.

## Структура проекта

```
qa-ui-framework/
├── src/main/java/qa/autotest/
│   ├── domain/
│   │   ├── dto/                     # UserDto, ProductDto, CartDto, CheckoutDto
│   │   └── enums/                   # SauceDemoProduct
│   └── framework/
│       ├── assertions/              # CartAssert, CheckoutAssert, ProductAssert
│       ├── config/                  # TestConfig, BrowserConfig, CredentialConfig,
│       │                            # CheckoutConfig, ExecutionConfig, TimeoutConfig,
│       │                            # ConfigFactory
│       ├── drivers/
│       │   ├── browser/             # BrowserProvider (interface),
│       │   │                        # BrowserProviderRegistry,
│       │   │                        # ChromeProvider, FirefoxProvider,
│       │   │                        # EdgeProvider, SafariProvider
│       │   ├── DriverFactory.java
│       │   └── DriverManager.java
│       ├── listeners/               # AllureSelenideListener
│       ├── pages/                   # BasePage, LoginPage, InventoryPage,
│       │                            # ProductDetailsPage, CartPage,
│       │                            # CheckoutStepOnePage, CheckoutStepTwoPage,
│       │                            # CheckoutCompletePage
│       ├── steps/                   # AuthSteps, CartSteps, CheckoutSteps,
│       │                            # InventorySteps
│       └── utils/                   # PriceParser
├── src/main/resources/
│   ├── config/                      # default.properties, local.properties,
│   │                                # ci.properties
│   └── logback.xml
├── src/test/java/qa/autotest/
│   ├── extensions/                  # FlakyDetectionExtension
│   └── tests/
│       ├── BaseTest.java
│       ├── cart/                    # CartOperationsTests
│       ├── checkout/                # CheckoutFlowTests
│       ├── inventory/               # InventoryDisplayTests, InventorySortingTests
│       ├── login/                   # LoginSuccessTests, LoginFailureTests
│       └── navigation/              # NavigationTests
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── .github/workflows/
│   ├── run-tests.yml                # Reusable workflow (единственный источник логики)
│   ├── test-all.yml                 # Caller: все тесты
│   ├── test-login.yml               # Caller: login-группа
│   ├── test-inventory.yml           # Caller: inventory-группа
│   ├── test-cart.yml                # Caller: cart-группа
│   ├── test-checkout.yml            # Caller: checkout-группа
│   └── test-navigation.yml          # Caller: navigation-группа
├── docs/
└── pom.xml
```

## Ключевые компоненты

### 1. Domain Model

**Назначение:** типизированное представление данных предметной области.

- `UserDto`, `ProductDto`, `CartDto`, `CheckoutDto` — Lombok `@Builder`, используются в Steps и тестах
- `SauceDemoProduct` — enum с полями `displayName` (UI-название) и `buttonId` (суффикс `data-test` атрибута). Устраняет runtime string transformation и делает несоответствие между именем и атрибутом видимым на этапе компиляции.

### 2. Page Object Model

**Назначение:** инкапсуляция логики взаимодействия с UI.

**Иерархия:**
```
BasePage (общие элементы: header, cart, burger menu)
  ├── InventoryPage
  ├── ProductDetailsPage
  ├── CartPage
  ├── CheckoutStepOnePage
  ├── CheckoutStepTwoPage
  └── CheckoutCompletePage

LoginPage (не наследует BasePage — страница без authenticated header)
```

**Принципы:**
- Одна страница = один класс
- Методы возвращают Page Objects для fluent interface
- Explicit outcome-контракты: `submitForSuccess()` / `submitExpectingError()` вместо одного `clickLoginButton()` с неопределённым результатом
- Коллекции — method-locators (защита от StaleElementReferenceException при переиспользовании PO)
- Одиночные стабильные элементы — instance fields (lazy Selenide-прокси, без рисков)

**Burger menu (BasePage):**

React-burger-menu анимирует боковую панель CSS-переходом (~300 мс). Элемент `.bm-menu` присутствует в DOM всегда. `openBurgerMenu()` ожидает видимости `#inventory_sidebar_link` — конкретного интерактивного дочернего элемента, который становится доступным только после завершения анимации. Явный `Duration.ofSeconds(15)` защищает от медленного headless CI без зависимости от глобального `Configuration.timeout`.

### 3. Steps Layer

**Назначение:** бизнес-уровень операций поверх Page Objects.

- Stateless: каждый метод принимает нужный Page Object через параметр и возвращает результирующий
- Безопасны при параллельном выполнении — нет shared state
- `AuthSteps` принимает `CredentialConfig` (ISP), не полный `TestConfig`

### 4. Custom AssertJ Assertions

- `CartAssert` — загрузка страницы, количество товаров, наличие конкретного товара
- `CheckoutAssert` — ошибки валидации, расчёт суммы (subtotal + tax == total), успешность заказа
- `ProductAssert` — имя, цена, описание, изображение

### 5. WebDriver Management

**DriverFactory** создаёт `WebDriver`-инстансы через `BrowserProvider` из `BrowserProviderRegistry`. Расширение на новый браузер — реализовать `BrowserProvider` и вызвать `register()`. `DriverFactory` не изменяется (OCP).

**DriverManager** управляет ThreadLocal жизненным циклом:

```java
private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();
```

`initDriver()` также устанавливает `Configuration.timeout`, `Configuration.screenshots`, `Configuration.reportsFolder`, `Configuration.browserSize`. Эти поля — статические, не ThreadLocal. Их запись сосредоточена в одном месте и выполняется только из `initDriver()`, который вызывается из `@BeforeEach` одного потока в рамках `parallel-strict`-форка.

### 6. Configuration Management

Owner MERGE policy, приоритет (высший → низший):

1. System properties (`-Dkey=value`)
2. Environment variables
3. `classpath:config/${env}.properties`
4. `classpath:config/default.properties`

ISP-разделение:

| Интерфейс | Ответственность | Потребители |
|-----------|-----------------|-------------|
| `BrowserConfig` | браузер, headless, viewport, Grid URL | `DriverFactory`, `DriverManager` |
| `TimeoutConfig` | page load, implicit (0), explicit | `DriverManager` |
| `CredentialConfig` | URL приложения, credentials | `AuthSteps` |
| `CheckoutConfig` | данные формы | `CheckoutSteps` |
| `ExecutionConfig` | потоки, retry, скриншоты | `BaseTest` |
| `TestConfig` | композит всех выше | `BaseTest`, `ConfigFactory` |

### 7. BaseTest

`@BeforeEach setUp()`:
1. `DriverManager.initDriver(config)` — создаёт WebDriver, устанавливает `Configuration.*`
2. Проверяет, что WebDriver запущен
3. Инициализирует `loginPage`, `authSteps`, `cartSteps`, `checkoutSteps`, `inventorySteps`

`@BeforeAll setUpAll()`:
- Double-checked locking на `BaseTest.class` — регистрирует `AllureSelenideListener` ровно один раз

`@AfterEach tearDown()`:
- `DriverManager.quitDriver()` — quit + ThreadLocal.remove()

### 8. FlakyDetectionExtension

JUnit 5 `TestWatcher`. Хранит историю исходов в `ConcurrentHashMap<String, Outcome>`. Тест становится `FLAKY`, если в рамках одного прогона (с `rerunFailingTestsCount`) он имел противоположные исходы. Аннотирует Allure через `lifecycle API`, выводит сводку в shutdown hook.

## Параллелизм

### Поддерживаемый режим: `parallel-strict`

```bash
mvn clean test -Pparallel-strict -Dthread.count=4
```

`forkCount=4` запускает 4 независимых JVM-процесса. Каждый процесс имеет собственный class loader и собственную копию `com.codeborne.selenide.Configuration`. Внутри форка выполняется один поток — запись в `Configuration` однопоточна и безопасна.

## CI/CD

### Структура workflows

```
run-tests.yml          ← reusable, on: workflow_call
    ↑            ↑
test-all.yml   test-login.yml / test-cart.yml / ...
```

`run-tests.yml` принимает inputs: `suite_name`, `test_groups`, `thread_count`, `browser`, `deploy_pages`. Содержит всю логику: установка браузеров, backup/restore Allure history, запуск Maven, создание `executor.json`, деплой на gh-pages, upload артефактов.

Caller-workflows содержат только триггеры и передачу параметров. Изменение любого шага пайплайна вносится в одном файле.

### Allure history

Между прогонами история сохраняется в ветке `gh-pages/history`. Перед тестами копируется в `/tmp/allure-history`, после тестов (включая `always()`) восстанавливается в `target/allure-results/history` перед генерацией отчёта.

### Артефакты

| Артефакт | Retention | Условие |
|----------|-----------|---------|
| `allure-results-{suite}` | 30 дней | always |
| `allure-report-{suite}` | 30 дней | always |
| `test-logs-{suite}` | 7 дней | always |
| `screenshots-{suite}` | 7 дней | failure |

## Паттерны проектирования

| Паттерн | Применение |
|---------|------------|
| Page Object + Fluent Interface | все Page классы |
| Strategy | `BrowserProvider` / `BrowserProviderRegistry` |
| Factory Method | `DriverFactory.create()` |
| Registry | `BrowserProviderRegistry` |
| Builder | все DTO |
| Singleton | `ConfigFactory` (double-checked locking) |
| Custom Assertion Builder | `CartAssert`, `CheckoutAssert`, `ProductAssert` |
| TestWatcher Extension | `FlakyDetectionExtension` |

## Логирование

SLF4J + Logback. `SiftingAppender` создаёт отдельный файл для каждого потока по ключу `threadName`:

```xml
<appender name="SIFT" class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>threadName</key>
    </discriminator>
</appender>
```

При `parallel-strict` (forked JVM) каждый форк имеет собственный процесс — логи не конкурируют.

## Интеграция с Allure

Аннотации: `@Epic`, `@Feature`, `@Story`, `@Step`, `@Severity`, `@Description`, `@DisplayName`.

Вложения добавляет `AllureSelenideListener` при каждом упавшем Selenide-действии:
- скриншот (`image/png`)
- page source (`text/html`)
- текст ошибки (`text/plain`)

`FlakyDetectionExtension` добавляет label `flaky=true/false` и текстовое вложение с историей исходов.
