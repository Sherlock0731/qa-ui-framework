# Примеры команд запуска тестов

## Базовые команды

### Запуск всех тестов
```bash
mvn clean test
```

### Запуск тестов с генерацией отчета
```bash
mvn clean test
mvn allure:serve
```

## Запуск по группам тестов

### Login тесты
```bash
mvn clean test -Plogin
```

### Inventory тесты
```bash
mvn clean test -Pinventory
```

### Cart тесты
```bash
mvn clean test -Pcart
```

### Checkout тесты
```bash
mvn clean test -Pcheckout
```

### Navigation тесты
```bash
mvn clean test -Pnavigation
```

### Smoke тесты
```bash
mvn clean test -Psmoke
```

## Выбор браузера

### Chrome (по умолчанию)
```bash
mvn clean test -Dbrowser=chrome
```

### Firefox
```bash
mvn clean test -Dbrowser=firefox
```

### Edge
```bash
mvn clean test -Dbrowser=edge
```

### Safari (только macOS)
```bash
mvn clean test -Dbrowser=safari
```

```bash
```

## Headless режим

```bash
mvn clean test -Dheadless=true
```

## Многопоточность

### 2 потока
```bash
mvn clean test -Dthread.count=2
```

### 4 потока
```bash
mvn clean test -Dthread.count=4
```

### 8 потоков
```bash
mvn clean test -Dthread.count=8
```

## Комбинированные команды

### Firefox в headless режиме с 4 потоками
```bash
mvn clean test -Dbrowser=firefox -Dheadless=true -Dthread.count=4
```

### Login тесты в Chrome с 2 потоками
```bash
mvn clean test -Plogin -Dbrowser=chrome -Dthread.count=2
```

### Smoke тесты в headless Firefox
```bash
mvn clean test -Psmoke -Dbrowser=firefox -Dheadless=true
```

## Использование run-tests.sh

### Все тесты с defaults
```bash
./run-tests.sh
```

### Firefox с 4 потоками
```bash
./run-tests.sh -b firefox -t 4
```

### Login тесты в headless режиме
```bash
./run-tests.sh -g login --headless
```

### Smoke тесты с отчетом
```bash
./run-tests.sh -g smoke -t 2 --allure
```

### Checkout тесты в Edge
```bash
./run-tests.sh -g checkout -b edge
```

## Docker

### Запуск всех тестов
```bash
docker-compose -f docker/docker-compose.yml up
```

### Запуск с параметрами
```bash
BROWSER=firefox THREAD_COUNT=4 docker-compose -f docker/docker-compose.yml up
```

### Login тесты в Docker
```bash
TEST_GROUPS=login docker-compose -f docker/docker-compose.yml up
```

## CI/CD переменные

### GitHub Actions (добавьте в secrets)
```
SAUCEDEMO_BASE_URL=[нужное значение]
USER_STANDARD_USERNAME=[нужное значение]
USER_STANDARD_PASSWORD=[нужное значение]
CHECKOUT_FIRSTNAME=[нужное значение]
CHECKOUT_LASTNAME=[нужное значение]
CHECKOUT_ZIPCODE=[нужное значение]
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

### Отключить скриншоты при ошибках
```bash
mvn clean test -Dscreenshot.on.failure=false
```
