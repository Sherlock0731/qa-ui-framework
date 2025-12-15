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
- **Selenide** - удобная обертка над Selenium WebDriver
- **Selenium WebDriver** - автоматизация браузеров
- **WebDriverManager** - автоматическое управление драйверами браузеров
- **AssertJ** - fluent assertions библиотека
- **Allure Report** - система отчетности
- **Lombok** - уменьшение boilerplate кода
- **SLF4J/Logback** - логирование с поддержкой многопоточности
- **Owner** - управление конфигурациями
- **Docker** - контейнеризация
- **GitHub Actions** - CI/CD pipeline

## Структура проекта

```
qa-ui-framework/
├── src/
│   ├── main/
│   │   ├── java/qa/autotest/
│   │   │   ├── app/dto/              # Data Transfer Objects
│   │   │   ├── pages/                # Page Object Model
│   │   │   └── framework/            # Core framework
│   │   │       ├── drivers/          # WebDriver management
│   │   │       └── config/           # Configuration
│   │   └── resources/
│   │       ├── config/               # Environment configs
│   │       └── logback.xml           # Logging config
│   └── test/
│       ├── java/examples/            # Test cases
│       │   ├── login/                # Login tests
│       │   ├── inventory/            # Inventory tests
│       │   ├── cart/                 # Cart tests
│       │   ├── checkout/             # Checkout tests
│       │   └── navigation/           # Navigation tests
│       └── resources/
├── docker/                           # Docker configs
│   ├── Dockerfile
│   └── docker-compose.yml
├── .github/workflows/                # CI/CD pipelines
│   ├── test-all.yml
│   ├── test-login.yml
│   ├── test-inventory.yml
│   ├── test-cart.yml
│   ├── test-checkout.yml
│   └── test-navigation.yml
├── docs/                             # Documentation
└── pom.xml                           # Maven config
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

# Запустить все тесты (один тест за раз, один браузер)
mvn clean test

# Явно указать последовательный режим
mvn clean test -Psequential

# ======================================
# ПАРАЛЛЕЛЬНОЕ ВЫПОЛНЕНИЕ
# ======================================

# Запустить тесты с 3 потоками (3-4 браузера одновременно)
mvn clean test -Pparallel -Dthread.count=3

# Запустить тесты с 5 потоками (5-6 браузеров одновременно)
mvn clean test -Pparallel -Dthread.count=5

# Запустить тесты с 10 потоками (10-11 браузеров одновременно)
mvn clean test -Pparallel -Dthread.count=10

# ВАЖНО: может быть N±1 браузеров кратковременно (это нормально!)

# ======================================
# ЗАПУСК ПО ГРУППАМ ТЕСТОВ
# ======================================

# Запустить только login тесты
mvn clean test -Plogin

# Запустить только inventory тесты
mvn clean test -Pinventory

# Запустить только cart тесты
mvn clean test -Pcart

# Запустить только checkout тесты
mvn clean test -Pcheckout

# Запустить только navigation тесты
mvn clean test -Pnavigation

# Запустить smoke тесты
mvn clean test -Psmoke

# Smoke тесты параллельно в 3 потока
mvn clean test -Psmoke,parallel -Dthread.count=3

# ======================================
# ALLURE ОТЧЕТЫ
# ======================================

# Сгенерировать и открыть Allure отчет
mvn allure:serve
```

**Важно:**
- По умолчанию тесты запускаются **последовательно** (один браузер за раз)
- Для параллельного запуска обязательно используйте профиль **`-Pparallel`**
- Параметр `-Dthread.count` работает только с профилем `-Pparallel`
- Без `-Pparallel` будет открываться только один браузер, независимо от `-Dthread.count`
- При параллельном запуске может быть **N±1 браузеров** кратковременно (это нормальное поведение JUnit 5)

### Выбор браузера

```bash
# Chrome (default)
mvn clean test -Pbrowser=chrome

# Firefox
mvn clean test -Pbrowser=firefox

# Edge
mvn clean test -Pbrowser=edge

# Safari (только macOS)
mvn clean test -Pbrowser=safari


# Headless mode
mvn clean test -Pheadless
```

### Запуск в Docker

```bash
# Запустить все тесты
docker-compose -f docker/docker-compose.yml up

# Запустить с параметрами
THREAD_COUNT=4 TEST_GROUPS=login docker-compose -f docker/docker-compose.yml up
```

## Переменные окружения

Создайте файл `.env` в корне проекта:

```env
# Application URL
SAUCEDEMO_BASE_URL=[нужное значение]

# User Credentials
USER_STANDARD_USERNAME=[нужное значение]
USER_STANDARD_PASSWORD=[нужное значение]
USER_LOCKED_USERNAME=[нужное значение]
USER_LOCKED_PASSWORD=[нужное значение]

# Checkout Information
CHECKOUT_FIRSTNAME=[нужное значение]
CHECKOUT_LASTNAME=[нужное значение]
CHECKOUT_ZIPCODE=[нужное значение]

# Browser Configuration
BROWSER=chrome
BROWSER_HEADLESS=false
THREAD_COUNT=1
```

## Поддерживаемые браузеры

- ✅ Chrome (Windows, Linux, macOS)
- ✅ Firefox (Windows, Linux, macOS)
- ✅ Edge (Windows, macOS)
- ✅ Safari (macOS)

## Многопоточность

Фреймворк полностью поддерживает многопоточное выполнение тестов:

```bash
# Запустить с 8 потоками
mvn clean test -Dthread.count=8
```

Каждый поток создает свой экземпляр WebDriver и имеет отдельный лог-файл.

## CI/CD

Проект интегрирован с GitHub Actions для автоматического запуска тестов:

- **test-all.yml** - запуск всех тестов
- **test-login.yml** - только login тесты
- **test-inventory.yml** - только inventory тесты
- **test-cart.yml** - только cart тесты
- **test-checkout.yml** - только checkout тесты
- **test-navigation.yml** - только navigation тесты

## Отчеты

После выполнения тестов генерируются:

1. **Allure Report** - подробная визуализация результатов
2. **Логи** - детальная информация о выполнении (target/logs/)
3. **Screenshots** - скриншоты при падении тестов (target/screenshots/)

## Документация

Подробная документация доступна в папке `docs/`:

- [Архитектура](docs/ARCHITECTURE.md)
- [Примеры команд](docs/COMMANDS_EXAMPLES.md)
- [Инструкция по запуску](docs/RUN_INSTRUCTIONS.md)
- [Параллельное выполнение тестов](docs/PARALLEL_EXECUTION.md)
- [Настройка WebDriver](docs/WEBDRIVER_CONFIGURATION.md)
- [Матрица тест-кейсов](docs/TEST_CASES_MATRIX.md)

## License

This project is licensed under the MIT License.

## Authors

- **Vitaliy Popravka** - QA Automation Engineer

## Контакты

Для вопросов и предложений создавайте Issue в репозитории.
