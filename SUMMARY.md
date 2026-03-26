# UI Test Automation Framework — Краткое описание

## Обзор проекта

**UI Test Automation Framework** — полнофункциональный фреймворк для автоматизации UI-тестирования веб-приложения [SauceDemo](https://www.saucedemo.com), реализующий 30 функциональных тест-кейсов.

## Ключевые особенности

### Технологический стек
- **Java 17** — современная LTS-версия
- **Maven** — управление зависимостями и сборка
- **JUnit 5.10.1** — фреймворк тестирования с поддержкой параллельности
- **Selenide 7.0.4** — обёртка над Selenium WebDriver
- **Selenium 4.25** — автоматизация браузеров
- **WebDriverManager** — автоматическое управление драйверами
- **Allure 2.25** — отчётность
- **Lombok** — снижение boilerplate
- **SLF4J/Logback** — гибкое логирование
- **Owner** — type-safe конфигурация

### Архитектурные решения

1. **Page Object Model (POM)**
   - Инкапсуляция логики страниц
   - Fluent interface с explicit outcome-контрактами (`submitForSuccess` / `submitExpectingError`)
   - Иерархическая структура с BasePage
   - Method-locators вместо instance fields для коллекций — защита от StaleElement

2. **Steps Layer**
   - Stateless бизнес-шаги (AuthSteps, CartSteps, CheckoutSteps, InventorySteps)
   - Передача Page Object через параметры — безопасно при параллельном выполнении

3. **Custom AssertJ Assertions**
   - `CartAssert`, `CheckoutAssert`, `ProductAssert` — domain-семантика вместо сырых значений

4. **Domain Model**
   - `SauceDemoProduct` enum с `displayName` и `buttonId` — устранение runtime string transformation
   - DTO с Builder pattern: `UserDto`, `ProductDto`, `CartDto`, `CheckoutDto`

5. **Параллельное выполнение**
   - Модель: `parallel-strict` (forkCount) — каждый форк является отдельным JVM-процессом
   - ThreadLocal WebDriver в `DriverManager` — изоляция драйвера per-thread внутри форка
   - `Configuration.*` устанавливается в `DriverManager.initDriver()` — единственная точка записи

6. **Flaky Detection**
   - `FlakyDetectionExtension` (JUnit 5 `TestWatcher`) — детектирует тесты с разными исходами в рамках одного прогона
   - Аннотирует Allure-отчёт через `lifecycle API`, печатает сводку в shutdown hook

7. **CI/CD**
   - Reusable workflow `run-tests.yml` — единственный источник логики пайплайна
   - Шесть caller-workflow (test-all + пять suite) делегируют в него через `workflow_call`
   - Allure history сохраняется между прогонами через gh-pages

## Структура тестов

| Модуль | Тестов | Описание |
|--------|--------|----------|
| **Login** | 5 | Авторизация: успешная, с ошибками, валидация |
| **Inventory** | 6 | Каталог: отображение, сортировка, навигация |
| **Cart** | 8 | Корзина: добавление, удаление, отображение |
| **Checkout** | 8 | Чекаут: форма, валидация, завершение |
| **Navigation** | 3 | Бургер-меню, logout, reset app state |
| **Итого** | **30** | |

### Приоритизация

- **Critical** — 21 тест
- **Normal** — 9 тестов
- **Smoke** — 5 тестов (подмножество)

## Запуск тестов

```bash
# Все тесты последовательно
mvn clean test

# Параллельно, 4 JVM-форка
mvn clean test -Pparallel-strict -Dthread.count=4

# Группа
mvn clean test -Plogin
mvn clean test -Psmoke

# Через скрипт
./run-tests.sh -g smoke -b firefox -t 4 --allure
```

## CI/CD

### GitHub Actions

| Файл | Триггер | Назначение |
|------|---------|------------|
| `run-tests.yml` | `workflow_call` | Reusable: вся логика пайплайна |
| `test-all.yml` | push, PR, schedule (04:00 UTC) | Все тесты, деплой Allure на gh-pages |
| `test-login.yml` | workflow_dispatch | Login-группа |
| `test-inventory.yml` | workflow_dispatch | Inventory-группа |
| `test-cart.yml` | workflow_dispatch | Cart-группа |
| `test-checkout.yml` | workflow_dispatch | Checkout-группа |
| `test-navigation.yml` | workflow_dispatch | Navigation-группа |

### Docker

```bash
docker-compose -f docker/docker-compose.yml up
BROWSER=firefox THREAD_COUNT=4 TEST_GROUPS=smoke docker-compose -f docker/docker-compose.yml up
```

## Отчётность

### Allure Report
- Детальная визуализация результатов
- История прогонов (хранится в gh-pages)
- Группировка по Epic/Feature/Story
- Скриншоты при ошибках (AllureSelenideListener)
- Flaky-метки через FlakyDetectionExtension

### Логирование
- Общий лог: `target/logs/test-execution.log`
- Per-thread логи: `target/logs/thread-*.log` (SiftingAppender)

## Конфигурация

### Приоритет загрузки (Owner MERGE)
1. System properties (`-Dkey=value`)
2. Environment variables
3. `classpath:config/${env}.properties`
4. `classpath:config/default.properties`

### ISP-разделение конфигурации
- `BrowserConfig` — браузер, headless, viewport, Grid URL
- `TimeoutConfig` — page load, implicit (всегда 0), explicit
- `CredentialConfig` — URL приложения, credentials
- `CheckoutConfig` — данные формы чекаута
- `ExecutionConfig` — потоки, retry, скриншоты

## Статистика

- **Page Objects:** 8 классов
- **Steps:** 4 класса
- **Custom Assertions:** 3 класса
- **DTO:** 4 класса
- **Enum:** 1 (SauceDemoProduct, 6 товаров)
- **Test Classes:** 7 классов
- **Test Cases:** 30 тестов

## Поддерживаемые браузеры

- ✅ Chrome (Windows, Linux, macOS)
- ✅ Firefox (Windows, Linux, macOS)
- ✅ Edge (Windows, macOS)
- ✅ Safari (macOS)

## Требования

- Java 17+
- Maven 3.8+
- 4 GB RAM (8 GB рекомендуется при 4+ форках)

## Производительность

| Конфигурация | Время |
|--------------|-------|
| 30 тестов, 1 поток | ~15–20 мин |
| 30 тестов, 4 форка (`parallel-strict`) | ~5–7 мин |
| Smoke (5 тестов), 1 поток | ~3–4 мин |

---

**Version:** 1.0.0  
**Author:** Vitaliy Popravka  
**Status:** ✅ Production Ready
