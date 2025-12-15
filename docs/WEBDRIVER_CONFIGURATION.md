# Настройка WebDriver

## Два режима работы с WebDriver

Фреймворк поддерживает два режима управления WebDriver:

1. **Автоматический (по умолчанию)** - использует WebDriverManager
2. **Ручной** - использует локальные файлы драйверов

## Режим 1: Автоматический (рекомендуется)

### Описание
По умолчанию фреймворк использует **WebDriverManager**, который автоматически:
- Определяет версию установленного браузера
- Скачивает соответствующий драйвер
- Кэширует драйверы локально (`~/.cache/selenium/`)
- Настраивает system properties

### Преимущества
✅ Не требует ручной настройки  
✅ Автоматическое обновление драйверов  
✅ Поддержка всех основных браузеров  
✅ Кросс-платформенность (Windows, Linux, macOS)

### Конфигурация
```properties
# В default.properties или через -D параметр
webdriver.use.local=false
```

### Пример запуска
```bash
# WebDriverManager автоматически управляет драйверами
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
```

---

## Режим 2: Локальные драйверы

### Когда использовать
- Ограниченный доступ к интернету
- Корпоративная среда с proxy
- Нужна конкретная версия драйвера
- Драйверы уже загружены локально

### Шаг 1: Скачать драйверы

#### Chrome
```bash
# Официальный сайт
https://chromedriver.chromium.org/downloads

# Или через WebDriverManager (однократно)
webdrivermanager chromiumdriver --cache
```

#### Firefox
```bash
# Официальный сайт
https://github.com/mozilla/geckodriver/releases

# Или через WebDriverManager
webdrivermanager firefoxdriver --cache
```

#### Edge
```bash
# Официальный сайт
https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/

# Или через WebDriverManager
webdrivermanager edgedriver --cache
```

```bash
# Официальный сайт
```

### Шаг 2: Разместить драйверы

Рекомендуемая структура:

```
qa-ui-framework/
├── src/test/resources/drivers/
│   ├── windows/
│   │   ├── chromedriver.exe
│   │   ├── geckodriver.exe
│   │   └── msedgedriver.exe
│   ├── linux/
│   │   ├── chromedriver
│   │   ├── geckodriver
│   │   └── msedgedriver
│   └── macos/
│       ├── chromedriver
│       ├── geckodriver
│       └── msedgedriver
```

**Важно:** Сделайте файлы исполняемыми на Linux/macOS:
```bash
chmod +x src/test/resources/drivers/linux/*
chmod +x src/test/resources/drivers/macos/*
```

### Шаг 3: Настроить конфигурацию

#### Вариант A: Через properties файл

**local.properties:**
```properties
# Включить режим локальных драйверов
webdriver.use.local=true

# Указать пути к драйверам

# Для Windows:
webdriver.chrome.driver=C:/qa-ui-framework/src/test/resources/drivers/windows/chromedriver.exe
webdriver.firefox.driver=C:/qa-ui-framework/src/test/resources/drivers/windows/geckodriver.exe
webdriver.edge.driver=C:/qa-ui-framework/src/test/resources/drivers/windows/msedgedriver.exe

# Для Linux:
webdriver.chrome.driver=/home/user/qa-ui-framework/src/test/resources/drivers/linux/chromedriver
webdriver.firefox.driver=/home/user/qa-ui-framework/src/test/resources/drivers/linux/geckodriver
webdriver.edge.driver=/home/user/qa-ui-framework/src/test/resources/drivers/linux/msedgedriver

# Для macOS:
webdriver.chrome.driver=/Users/user/qa-ui-framework/src/test/resources/drivers/macos/chromedriver
webdriver.firefox.driver=/Users/user/qa-ui-framework/src/test/resources/drivers/macos/geckodriver
webdriver.edge.driver=/Users/user/qa-ui-framework/src/test/resources/drivers/macos/msedgedriver
```

#### Вариант B: Через .env файл

**.env:**
```env
WEBDRIVER_USE_LOCAL=true
WEBDRIVER_CHROME_DRIVER=/path/to/chromedriver
WEBDRIVER_FIREFOX_DRIVER=/path/to/geckodriver
WEBDRIVER_EDGE_DRIVER=/path/to/msedgedriver
```

#### Вариант C: Через командную строку

```bash
mvn clean test \
  -Dwebdriver.use.local=true \
  -Dwebdriver.chrome.driver=/path/to/chromedriver
```

#### Вариант D: Через переменные окружения

**Windows:**
```cmd
set WEBDRIVER_USE_LOCAL=true
set WEBDRIVER_CHROME_DRIVER=C:\drivers\chromedriver.exe
mvn clean test
```

**Linux/macOS:**
```bash
export WEBDRIVER_USE_LOCAL=true
export WEBDRIVER_CHROME_DRIVER=/usr/local/bin/chromedriver
mvn clean test
```

### Шаг 4: Запустить тесты

```bash
# С локальным Chrome драйвером
mvn clean test -Dbrowser=chrome

# С локальным Firefox драйвером
mvn clean test -Dbrowser=firefox
```

---

## Рекомендации по путям

### Абсолютные vs Относительные пути

**Абсолютные (рекомендуется для CI/CD):**
```properties
# Windows
webdriver.chrome.driver=C:/tools/drivers/chromedriver.exe

# Linux
webdriver.chrome.driver=/opt/selenium/chromedriver

# macOS
webdriver.chrome.driver=/usr/local/bin/chromedriver
```

**Относительные (удобно для локальной разработки):**
```properties
# Относительно корня проекта
webdriver.chrome.driver=src/test/resources/drivers/linux/chromedriver
```

### Системные пути

Если драйверы в системном PATH, можно указать только имя:
```properties
webdriver.chrome.driver=chromedriver
webdriver.firefox.driver=geckodriver
```

---

## Использование в разных окружениях

### Локальная разработка

**local.properties:**
```properties
webdriver.use.local=false  # Используем WebDriverManager
```

### CI/CD (GitHub Actions, Jenkins)

**ci.properties:**
```properties
webdriver.use.local=true
webdriver.chrome.driver=/usr/local/bin/chromedriver
webdriver.firefox.driver=/usr/local/bin/geckodriver
```

### Docker

В Dockerfile уже настроены драйверы через WebDriverManager.  
Если нужны локальные драйверы:

```dockerfile
# Копировать драйверы в образ
COPY src/test/resources/drivers/linux/* /usr/local/bin/
RUN chmod +x /usr/local/bin/chromedriver /usr/local/bin/geckodriver

ENV WEBDRIVER_USE_LOCAL=true
ENV WEBDRIVER_CHROME_DRIVER=/usr/local/bin/chromedriver
ENV WEBDRIVER_FIREFOX_DRIVER=/usr/local/bin/geckodriver
```

---

## Проверка конфигурации

### Способ 1: Через логи

При запуске тестов в логах будет видно:
```
Using local Chrome driver from: /path/to/chromedriver
```
или
```
Using WebDriverManager for Chrome
```

### Способ 2: Создать тестовый скрипт

**test-driver.sh:**
```bash
#!/bin/bash

echo "Testing WebDriver configuration..."

# Тест с WebDriverManager
mvn test -Dtest=LoginSuccessTests -Dwebdriver.use.local=false

# Тест с локальным драйвером
mvn test -Dtest=LoginSuccessTests -Dwebdriver.use.local=true
```

---

## Troubleshooting

### Проблема: Driver executable does not exist

**Решение:**
```bash
# Проверьте путь к файлу
ls -la /path/to/chromedriver

# Проверьте права доступа (Linux/macOS)
chmod +x /path/to/chromedriver

# Проверьте правильность пути в конфигурации
cat src/main/resources/config/local.properties | grep webdriver
```

### Проблема: Driver version mismatch

**Решение:**
```bash
# Проверьте версию браузера
google-chrome --version  # Chrome
firefox --version        # Firefox

# Скачайте соответствующую версию драйвера
# Например, для Chrome 120.x.x.x нужен ChromeDriver 120.x.x.x
```

### Проблема: Permission denied (Linux/macOS)

**Решение:**
```bash
# Сделайте файл исполняемым
chmod +x /path/to/chromedriver

# Или для всех драйверов
chmod +x src/test/resources/drivers/linux/*
```

### Проблема: Не работает на macOS (Gatekeeper)

**Решение:**
```bash
# Разблокируйте драйвер
xattr -d com.apple.quarantine /path/to/chromedriver

# Или через System Preferences
# Security & Privacy → Allow chromedriver
```

---

## Приоритет настроек

Фреймворк проверяет конфигурацию в следующем порядке:

1. **System properties** (`-Dkey=value`)
2. **Environment variables**
3. **Properties файлы** (`local.properties`, `ci.properties`)
4. **Default properties** (`default.properties`)

Пример:
```bash
# System property имеет высший приоритет
mvn clean test \
  -Dwebdriver.use.local=true \
  -Dwebdriver.chrome.driver=/custom/path/chromedriver
```

---

## Примеры конфигураций

### Конфигурация 1: Только WebDriverManager
```properties
webdriver.use.local=false
```

### Конфигурация 2: Локальный Chrome, остальные через WDM
```properties
webdriver.use.local=true
webdriver.chrome.driver=/path/to/chromedriver
```

### Конфигурация 3: Все локальные
```properties
webdriver.use.local=true
webdriver.chrome.driver=/drivers/chromedriver
webdriver.firefox.driver=/drivers/geckodriver
webdriver.edge.driver=/drivers/msedgedriver
```

### Конфигурация 4: CI/CD с предустановленными драйверами
```properties
webdriver.use.local=true
webdriver.chrome.driver=chromedriver  # В PATH
webdriver.firefox.driver=geckodriver  # В PATH
```

---

## Best Practices

1. ✅ **Для локальной разработки** - используйте WebDriverManager (default)
2. ✅ **Для CI/CD** - используйте предустановленные драйверы в Docker образе
3. ✅ **Версионируйте драйверы** - храните в Git LFS или artifact repository
4. ✅ **Документируйте версии** - какой драйвер для какого браузера
5. ✅ **Автоматизируйте обновление** - создайте скрипт для скачивания
6. ✅ **Проверяйте права** - убедитесь что драйверы executable
7. ✅ **Используйте относительные пути** - для переносимости

---

## Дополнительные ресурсы

- [Selenium WebDriver Documentation](https://www.selenium.dev/documentation/webdriver/)
- [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)
- [ChromeDriver Downloads](https://chromedriver.chromium.org/downloads)
- [GeckoDriver (Firefox) Releases](https://github.com/mozilla/geckodriver/releases)
- [EdgeDriver Downloads](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
