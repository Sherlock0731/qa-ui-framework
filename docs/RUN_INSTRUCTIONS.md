# Инструкция по запуску тестов

## Предварительные требования

### Обязательное ПО

1. **Java 17** или выше
   ```bash
   java -version
   # Должно быть: java version "17" или выше
   ```

2. **Maven 3.8+**
   ```bash
   mvn -version
   # Должно быть: Apache Maven 3.8.x или выше
   ```

### Опциональное ПО

3. **Docker** (для запуска в контейнерах)
4. **Allure CLI** (для локальной генерации отчетов)
   ```bash
   npm install -g allure-commandline
   ```

## Настройка окружения

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd qa-ui-framework
```

### 2. Настройка переменных окружения

Скопируйте `.env.example` в `.env` и настройте значения:

```bash
cp .env.example .env
```

Отредактируйте файл `.env`:
```env
SAUCEDEMO_BASE_URL=[нужное значение]
USER_STANDARD_USERNAME=[нужное значение]
USER_STANDARD_PASSWORD=[нужное значение]
BROWSER=chrome
THREAD_COUNT=1
```

### 3. Установка зависимостей

```bash
mvn clean install -DskipTests
```

## Локальный запуск

### Базовые команды

#### Запустить все тесты
```bash
mvn clean test
```

#### Запустить с использованием скрипта
```bash
./run-tests.sh
```

#### Запустить с помощью Maven Wrapper (если есть)
```bash
./mvnw clean test
```

### Запуск по модулям

#### Login тесты
```bash
mvn clean test -Plogin
# или
./run-tests.sh -g login
```

#### Inventory тесты
```bash
mvn clean test -Pinventory
```

#### Cart тесты
```bash
mvn clean test -Pcart
```

#### Checkout тесты
```bash
mvn clean test -Pcheckout
```

#### Navigation тесты
```bash
mvn clean test -Pnavigation
```

#### Smoke тесты
```bash
mvn clean test -Psmoke
# или
./run-tests.sh -g smoke
```

### Выбор браузера

#### Chrome (по умолчанию)
```bash
mvn clean test -Dbrowser=chrome
```

#### Firefox
```bash
mvn clean test -Dbrowser=firefox
./run-tests.sh -b firefox
```

#### Edge
```bash
mvn clean test -Dbrowser=edge
./run-tests.sh -b edge
```

#### Safari (только на macOS)
```bash
mvn clean test -Dbrowser=safari
./run-tests.sh -b safari
```

### Headless режим

```bash
mvn clean test -Dheadless=true
./run-tests.sh --headless
```

### Многопоточность

#### 2 потока
```bash
mvn clean test -Dthread.count=2
./run-tests.sh -t 2
```

#### 4 потока (рекомендуется для CI)
```bash
mvn clean test -Dthread.count=4
./run-tests.sh -t 4
```

#### 8 потоков (для мощных машин)
```bash
mvn clean test -Dthread.count=8
./run-tests.sh -t 8
```

### Комбинированные команды

#### Firefox в headless с 4 потоками
```bash
mvn clean test -Dbrowser=firefox -Dheadless=true -Dthread.count=4
./run-tests.sh -b firefox --headless -t 4
```

#### Smoke тесты в Chrome с 2 потоками
```bash
mvn clean test -Psmoke -Dbrowser=chrome -Dthread.count=2
./run-tests.sh -g smoke -b chrome -t 2
```

## Генерация отчетов

### Allure Report

#### Сгенерировать и открыть отчет
```bash
mvn allure:serve
```

#### Только сгенерировать отчет
```bash
mvn allure:report
```

#### Открыть уже сгенерированный отчет
```bash
allure open target/site/allure-maven-plugin
```

#### С помощью скрипта
```bash
./run-tests.sh -g smoke --allure
```

### Просмотр логов

Логи сохраняются в:
- `target/logs/test-execution.log` - общий лог
- `target/logs/thread-*.log` - логи по потокам

```bash
# Просмотр общего лога
cat target/logs/test-execution.log

# Просмотр логов конкретного потока
cat target/logs/thread-pool-1-thread-1.log
```

### Просмотр скриншотов

Скриншоты при ошибках сохраняются в:
```bash
ls -la target/screenshots/
```

## Запуск в Docker

### Сборка образа
```bash
docker build -f docker/Dockerfile -t ui-tests .
```

### Запуск через docker-compose
```bash
# Все тесты
docker-compose -f docker/docker-compose.yml up

# С параметрами
BROWSER=firefox THREAD_COUNT=4 docker-compose -f docker/docker-compose.yml up

# Конкретная группа тестов
TEST_GROUPS=login docker-compose -f docker/docker-compose.yml up
```

### Запуск отдельного контейнера
```bash
docker run --rm \
  -e BROWSER=chrome \
  -e THREAD_COUNT=4 \
  -v $(pwd)/target:/app/target \
  ui-tests
```

## Запуск в CI/CD

### GitHub Actions

Тесты автоматически запускаются при:
- Push в `main` или `develop`
- Pull Request в `main` или `develop`
- Ручной запуск через workflow_dispatch

#### Ручной запуск workflow

1. Перейдите в раздел "Actions" на GitHub
2. Выберите нужный workflow (например, "UI Tests - All")
3. Нажмите "Run workflow"
4. Выберите параметры:
   - Browser (chrome, firefox, edge)
   - Thread count (1, 2, 4, 8)
5. Нажмите "Run workflow"

### Настройка секретов

В настройках репозитория добавьте секреты:

```
SAUCEDEMO_BASE_URL=[нужное значение]
USER_STANDARD_USERNAME=[нужное значение]
USER_STANDARD_PASSWORD=[нужное значение]
CHECKOUT_FIRSTNAME=[нужное значение]
CHECKOUT_LASTNAME=[нужное значение]
CHECKOUT_ZIPCODE=[нужное значение]
```

## Troubleshooting

### Проблема: WebDriver не найден

**Решение:**
```bash
# Очистить кэш Maven
mvn clean install -U

# Вручную скачать драйвер
# Chrome
webdrivermanager chromiumdriver

# Firefox
webdrivermanager firefoxdriver
```

### Проблема: Тесты падают с timeout

**Решение:**
Увеличьте таймауты в `default.properties`:
```properties
timeout.page.load=60000
timeout.implicit=20000
timeout.explicit=20000
```

### Проблема: Safari не запускается

**Решение (macOS):**
```bash
# Включить Remote Automation
safaridriver --enable
```

### Проблема: Headless режим не работает

**Решение:**
Проверьте, что используется правильный флаг:
```bash
# Правильно
mvn clean test -Dheadless=true

# Неправильно
mvn clean test -Dheadless=yes
```

### Проблема: Не генерируется Allure отчет

**Решение:**
```bash
# Убедитесь, что AspectJ weaver подключен
mvn clean test
mvn allure:report

# Проверьте наличие результатов
ls -la target/allure-results/
```

## Дополнительные опции

### Изменить размер окна браузера
```bash
mvn clean test -Dbrowser.width=1280 -Dbrowser.height=720
```

### Включить детальное логирование
```bash
mvn clean test -Dlogging.detailed=true
```

### Отключить скриншоты
```bash
mvn clean test -Dscreenshot.on.failure=false
```

### Использовать Selenium Grid
```bash
mvn clean test -Dbrowser.remote.url=http://localhost:4444/wd/hub
```

## Полезные ссылки

- [Selenide Documentation](https://selenide.org/)
- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
