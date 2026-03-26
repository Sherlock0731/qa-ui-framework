# UI Test Automation Framework

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JUnit](https://img.shields.io/badge/JUnit-5.10.1-red.svg)](https://junit.org/junit5/)
[![Selenide](https://img.shields.io/badge/Selenide-7.0.4-green.svg)](https://selenide.org/)
[![Allure](https://img.shields.io/badge/Allure-2.25.0-yellow.svg)](http://allure.qatools.ru/)

![Tests](https://github.com/Sherlock0731/qa-ui-framework/actions/workflows/test-all.yml/badge.svg)    
[![Allure Report](https://img.shields.io/badge/Allure-Report-orange)](https://sherlock0731.github.io/qa-ui-framework/)

Многопоточный фреймворк для автоматизации UI-тестирования веб-приложений с использованием современного стека технологий.

## Технологический стек

- **Java 17** - язык программирования
- **Maven** - система сборки и управления зависимостями
- **JUnit 5** - фреймворк для тестирования
- **Selenide 7.0.4** - обёртка над Selenium WebDriver
- **Selenium WebDriver 4.25** - автоматизация браузеров
- **WebDriverManager** - автоматическое управление драйверами браузеров
- **AssertJ** - fluent assertions
- **Allure 2.25** - отчётность
- **Lombok** - снижение boilerplate
- **SLF4J/Logback** - логирование с поддержкой многопоточности
- **Owner** - type-safe конфигурация
- **Docker** - контейнеризация
- **GitHub Actions** - CI/CD

## Структура проекта

```
qa-ui-framework/
├── src/
│   ├── main/
│   │   ├── java/qa/autotest/
│   │   │   ├── domain/
│   │   │   │   ├── dto/              # UserDto, ProductDto, CartDto, CheckoutDto
│   │   │   │   └── enums/            # SauceDemoProduct
│   │   │   └── framework/
│   │   │       ├── assertions/       # CartAssert, CheckoutAssert, ProductAssert
│   │   │       ├── config/           # TestConfig, BrowserConfig, CredentialConfig,
│   │   │       │                     # CheckoutConfig, ExecutionConfig, TimeoutConfig,
│   │   │       │                     # ConfigFactory
│   │   │       ├── drivers/
│   │   │       │   ├── browser/      # BrowserProvider, BrowserProviderRegistry,
│   │   │       │   │                 # ChromeProvider, FirefoxProvider,
│   │   │       │   │                 # EdgeProvider, SafariProvider
│   │   │       │   ├── DriverFactory.java
│   │   │       │   └── DriverManager.java
│   │   │       ├── listeners/        # AllureSelenideListener
│   │   │       ├── pages/            # BasePage, LoginPage, InventoryPage,
│   │   │       │                     # ProductDetailsPage, CartPage,
│   │   │       │                     # CheckoutStepOnePage, CheckoutStepTwoPage,
│   │   │       │                     # CheckoutCompletePage
│   │   │       ├── steps/            # AuthSteps, CartSteps, CheckoutSteps,
│   │   │       │                     # InventorySteps
│   │   │       └── utils/            # PriceParser
│   │   └── resources/
│   │       ├── config/               # default.properties, local.properties,
│   │       │                         # ci.properties
│   │       └── logback.xml
│   └── test/
│       └── java/qa/autotest/
│           ├── extensions/           # FlakyDetectionExtension
│           └── tests/
│               ├── BaseTest.java
│               ├── cart/             # CartOperationsTests
│               ├── checkout/         # CheckoutFlowTests
│               ├── inventory/        # InventoryDisplayTests, InventorySortingTests
│               ├── login/            # LoginSuccessTests, LoginFailureTests
│               └── navigation/       # NavigationTests
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── .github/workflows/
│   ├── run-tests.yml                 # Reusable workflow (единственный источник логики CI)
│   ├── test-all.yml                  # Все тесты — push/PR/schedule
│   ├── test-login.yml
│   ├── test-inventory.yml
│   ├── test-cart.yml
│   ├── test-checkout.yml
│   └── test-navigation.yml
├── docs/
│   ├── ARCHITECTURE.md
│   ├── COMMANDS_EXAMPLES.md
│   ├── PARALLEL_EXECUTION.md
│   ├── RUN_INSTRUCTIONS.md
│   ├── TEST_CASES_MATRIX.md
│   └── WEBDRIVER_CONFIGURATION.md
├── .env.example
├── run-tests.sh
└── pom.xml
```

## Быстрый старт

### Предварительные требования

- Java 17 или выше
- Maven 3.8+
- Docker (опционально)

### Локальный запуск

```bash
# Клонировать репозиторий
git clone <repository-url>
cd qa-ui-framework

# ======================================
# ПОСЛЕДОВАТЕЛЬНОЕ ВЫПОЛНЕНИЕ (по умолчанию)
# ======================================

# Все тесты, один браузер
mvn clean test

# Явно последовательный режим
mvn clean test -Psequential

# ======================================
# ПАРАЛЛЕЛЬНОЕ ВЫПОЛНЕНИЕ
# ======================================
# Используется профиль parallel-strict: каждый форк — отдельный JVM-процесс.
# Selenide Configuration изолирована per-process, race condition исключён.

# 4 параллельных JVM-форка (рекомендуется)
mvn clean test -Pparallel-strict -Dthread.count=4

# 8 форков
mvn clean test -Pparallel-strict -Dthread.count=8

# ======================================
# ЗАПУСК ПО ГРУППАМ ТЕСТОВ
# ======================================

mvn clean test -Plogin
mvn clean test -Pinventory
mvn clean test -Pcart
mvn clean test -Pcheckout
mvn clean test -Pnavigation
mvn clean test -Psmoke

# Группа + параллельно
mvn clean test -Psmoke,parallel-strict -Dthread.count=2

# ======================================
# ALLURE ОТЧЁТЫ
# ======================================

mvn allure:serve
```

### Выбор браузера

```bash
# Chrome (по умолчанию)
mvn clean test -Dbrowser=chrome

# Firefox
mvn clean test -Dbrowser=firefox

# Edge
mvn clean test -Dbrowser=edge

# Safari (только macOS)
mvn clean test -Dbrowser=safari

# Headless
mvn clean test -Dheadless=true
```

### Запуск в Docker

```bash
# Все тесты
docker-compose -f docker/docker-compose.yml up

# С параметрами
THREAD_COUNT=4 TEST_GROUPS=login docker-compose -f docker/docker-compose.yml up
```

## Переменные окружения

Создайте файл `.env` в корне проекта (см. `.env.example`):

```env
SAUCEDEMO_BASE_URL=[нужное значение]

USER_STANDARD_USERNAME=[нужное значение]
USER_STANDARD_PASSWORD=[нужное значение]
USER_LOCKED_USERNAME=[нужное значение]
USER_LOCKED_PASSWORD=[нужное значение]

CHECKOUT_FIRSTNAME=[нужное значение]
CHECKOUT_LASTNAME=[нужное значение]
CHECKOUT_ZIPCODE=[нужное значение]

BROWSER=chrome
BROWSER_HEADLESS=false
THREAD_COUNT=1
```

## Поддерживаемые браузеры

- ✅ Chrome (Windows, Linux, macOS)
- ✅ Firefox (Windows, Linux, macOS)
- ✅ Edge (Windows, macOS)
- ✅ Safari (macOS)

## CI/CD

Конфигурация GitHub Actions разделена на два уровня:

- **`run-tests.yml`** — reusable workflow, единственный источник логики выполнения тестов (установка браузеров, Allure history, деплой на gh-pages, upload артефактов). Любое изменение пайплайна вносится только здесь.
- **`test-all.yml`** — триггеры push/PR/schedule, запускает все тесты, деплоит отчёт на gh-pages.
- **`test-login/inventory/cart/checkout/navigation.yml`** — ручной запуск конкретной группы через `workflow_dispatch`, отчёт не деплоится.

## Отчёты

После выполнения тестов:

1. **Allure Report** — детальная визуализация, история прогонов, группировка по Epic/Feature/Story
2. **Логи** — `target/logs/` (per-thread через SiftingAppender)
3. **Screenshots** — `target/screenshots/` (только при падении, прикрепляются к Allure)

## Документация

- [Архитектура](docs/ARCHITECTURE.md)
- [Примеры команд](docs/COMMANDS_EXAMPLES.md)
- [Инструкция по запуску](docs/RUN_INSTRUCTIONS.md)
- [Параллельное выполнение](docs/PARALLEL_EXECUTION.md)
- [Настройка WebDriver](docs/WEBDRIVER_CONFIGURATION.md)
- [Матрица тест-кейсов](docs/TEST_CASES_MATRIX.md)

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

## Authors

- **Vitaliy Popravka** - QA Automation Engineer
