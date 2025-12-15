# Параллельное выполнение тестов

## Обзор

Фреймворк поддерживает два режима выполнения:
1. **Последовательный** (по умолчанию) - один тест за раз, один браузер
2. **Параллельный** - несколько тестов одновременно, несколько браузеров

## Последовательное выполнение (по умолчанию)

По умолчанию все тесты запускаются **последовательно** - один за другим.

### Примеры:

```bash
# Просто запуск - один браузер
mvn clean test

# Явно указать последовательный режим
mvn clean test -Psequential

# С указанием браузера
mvn clean test -Dbrowser=chrome

# С группой тестов
mvn clean test -Plogin
```

### Поведение:
- ✅ Открывается **только один браузер**
- ✅ Тесты выполняются **один за другим**
- ✅ Меньше потребление ресурсов
- ✅ Проще отлаживать
- ❌ Медленнее, чем параллельное выполнение

---

## Параллельное выполнение

Фреймворк предлагает **два режима** параллельного выполнения:

1. **`-Pparallel`** - Быстрый режим с JUnit 5 ForkJoinPool
2. **`-Pparallel-strict`** - Строгий режим с Maven forkCount

### Режим `-Pparallel` (рекомендуется)

Использует JUnit 5 `ForkJoinPool` для параллельного выполнения тестов.

**Примеры:**

```bash
# 3 теста одновременно - 3 браузера
mvn clean test -Pparallel -Dthread.count=3

# 5 тестов одновременно - 5 браузеров
mvn clean test -Pparallel -Dthread.count=5

# 10 тестов одновременно - 10 браузеров
mvn clean test -Pparallel -Dthread.count=10

# Smoke тесты в 4 потока
mvn clean test -Psmoke,parallel -Dthread.count=4

# Login тесты в 2 потока в Firefox
mvn clean test -Plogin,parallel -Dthread.count=2 -Dbrowser=firefox

# Headless режим с 8 потоками
mvn clean test -Pparallel,headless -Dthread.count=8
```

**Поведение:**
- ✅ **Быстрое выполнение** (оптимальное переиспользование потоков)
- ✅ Меньше потребление памяти (один JVM процесс)
- ✅ Общий Allure отчет
- ⚠️ Может кратковременно запустить **N+1 браузеров** (при переходе между тестами)
- ⚠️ Если у вас 5 тестов и `thread.count=4`, может быть 4-5 браузеров

**Когда использовать:**
- Для большинства случаев ✅
- Когда важна скорость выполнения
- Когда допустимы кратковременные всплески потоков
- Для CI/CD pipeline

---

### Режим `-Pparallel-strict` (строгий контроль)

Использует Maven Surefire `forkCount` для **строгого контроля** количества браузеров.

**Примеры:**

```bash
# СТРОГО 4 браузера одновременно (не больше!)
mvn clean test -Pparallel-strict -Dthread.count=4

# СТРОГО 3 браузера
mvn clean test -Pparallel-strict -Dthread.count=3

# СТРОГО 2 браузера для login тестов
mvn clean test -Plogin,parallel-strict -Dthread.count=2

# СТРОГО 5 браузеров в headless режиме
mvn clean test -Pparallel-strict,headless -Dthread.count=5
```

**Поведение:**
- ✅ **Гарантирует СТРОГО N браузеров** одновременно (никогда не превысит)
- ✅ Предсказуемое потребление ресурсов
- ❌ Медленнее чем `-Pparallel` (создает N отдельных JVM процессов)
- ❌ Больше потребление памяти (каждый fork = новая JVM)
- ❌ N отдельных Allure отчетов (объединяются при `allure:serve`)

**Когда использовать:**
- Когда критично **точное количество** браузеров
- При ограниченных ресурсах (RAM/CPU)
- Для стабильности в production тестировании
- Когда нужна изоляция между тестами

---

## Технические детали

### Как работает `-Pparallel`:

```xml
<parallel>methods</parallel>
<threadCount>${thread.count}</threadCount>
<junit.jupiter.execution.parallel.config.strategy>fixed</junit.jupiter.execution.parallel.config.strategy>
<junit.jupiter.execution.parallel.config.fixed.parallelism>${thread.count}</junit.jupiter.execution.parallel.config.fixed.parallelism>
```

- JUnit создает **ForkJoinPool** с N воркерами
- Тесты выполняются в общей JVM
- Воркеры переиспользуются между тестами
- Может быть кратковременный overlap при переходе

### Как работает `-Pparallel-strict`:

```xml
<forkCount>${thread.count}</forkCount>
<reuseForks>false</reuseForks>
<parallel>none</parallel>
```

- Maven создает **N отдельных JVM процессов**
- Каждый процесс запускает тесты последовательно
- Процессы НЕ переиспользуются (`reuseForks=false`)
- **Гарантирует строго N одновременных тестов**

---

## Важные правила

### ⚠️ Параметр `-Dthread.count` работает ТОЛЬКО с `-Pparallel`

```bash
# ❌ НЕПРАВИЛЬНО - откроется ОДИН браузер
mvn clean test -Dthread.count=5

# ❌ НЕПРАВИЛЬНО - thread.count игнорируется
mvn clean test -Dthread.count=10

# ✅ ПРАВИЛЬНО - откроется 5 браузеров
mvn clean test -Pparallel -Dthread.count=5
```

### О количестве одновременных браузеров

**Важно понимать:** `thread.count=4` означает **максимум 4 одновременных потока**, но:

- Если у вас 5 тестов и `thread.count=4`:
  - Первые 4 теста запустятся одновременно
  - 5-й тест запустится, когда освободится поток
  - **Пиковое количество браузеров: 4-5** (кратковременно может быть 5)

- Если у вас 10 тестов и `thread.count=4`:
  - Одновременно будет **не более 4-5 браузеров**
  - Тесты выполняются волнами: 4, потом еще 4, потом 2

Это **нормальное поведение** JUnit 5 - он эффективно использует пул потоков.

**Если нужен СТРОГО 4 одновременных браузера:** используйте комбинацию с группировкой тестов или запускайте меньше тестов.

### Значение thread.count по умолчанию

В `pom.xml` установлено:
```xml
<thread.count>1</thread.count>
```

Это означает:
- Без `-Pparallel`: **всегда 1 браузер** (последовательно)
- С `-Pparallel` без `-Dthread.count`: **1 поток** (бессмысленно)
- С `-Pparallel -Dthread.count=N`: **N потоков** параллельно

---

## Рекомендации по выбору количества потоков

### Для локального запуска:

| Количество тестов | thread.count | Описание |
|-------------------|--------------|----------|
| 1-10 | 2-3 | Быстро, низкая нагрузка |
| 11-30 | 4-5 | Оптимально для большинства ПК |
| 31-50 | 6-8 | Требует 16GB+ RAM |
| 50+ | 10+ | Требует мощный ПК или CI/CD |

### Для CI/CD:

```bash
# GitHub Actions (2 CPU cores)
mvn clean test -Pparallel -Dthread.count=2

# GitLab CI (4 CPU cores)
mvn clean test -Pparallel -Dthread.count=4

# Jenkins (8 CPU cores)
mvn clean test -Pparallel -Dthread.count=8
```

---

## Профили для комбинирования

Можно комбинировать несколько профилей:

```bash
# Smoke тесты + параллельно + headless + Firefox
mvn clean test -Psmoke,parallel,headless,firefox -Dthread.count=5

# Login + Cart тесты + параллельно + Chrome
mvn clean test -Plogin,cart,parallel,chrome -Dthread.count=3

# Все тесты + параллельно + headless
mvn clean test -Pparallel,headless -Dthread.count=10
```

---

## Мониторинг параллельного выполнения

### В логах:

```
[INFO] --- Running tests in parallel mode ---
[INFO] Thread count: 5
[INFO] Parallel mode: methods
[INFO] 
[INFO] Running LoginSuccessTests
[INFO] Running LoginFailureTests
[INFO] Running InventorySortingTests
[INFO] Running CartOperationsTests
[INFO] Running CheckoutFlowTests
```

### В Task Manager / Activity Monitor:

- Откройте диспетчер задач
- Найдите процессы `chromedriver.exe` (или `geckodriver`, `msedgedriver`)
- Количество процессов = `thread.count`

---

## Технические детали

### Последовательный режим (default):

```xml
<parallel>none</parallel>
<threadCount>1</threadCount>
<junit.jupiter.execution.parallel.enabled>false</junit.jupiter.execution.parallel.enabled>
<junit.jupiter.execution.parallel.mode.default>same_thread</junit.jupiter.execution.parallel.mode.default>
```

### Параллельный режим (-Pparallel):

```xml
<parallel>methods</parallel>
<threadCount>${thread.count}</threadCount>
<junit.jupiter.execution.parallel.enabled>true</junit.jupiter.execution.parallel.enabled>
<junit.jupiter.execution.parallel.mode.default>concurrent</junit.jupiter.execution.parallel.mode.default>
<junit.jupiter.execution.parallel.config.strategy>fixed</junit.jupiter.execution.parallel.config.strategy>
<junit.jupiter.execution.parallel.config.fixed.parallelism>${thread.count}</junit.jupiter.execution.parallel.config.fixed.parallelism>
```

---

## Troubleshooting

### Проблема: "Запускается много браузеров при mvn clean test"

**Причина:** Возможно был изменен default режим в pom.xml

**Решение:**
```bash
# Убедитесь что используете обновленный pom.xml
mvn clean test  # должен запускать 1 браузер
```

### Проблема: "-Dthread.count=5 не работает"

**Причина:** Забыли добавить `-Pparallel`

**Решение:**
```bash
# Неправильно
mvn clean test -Dthread.count=5

# Правильно
mvn clean test -Pparallel -Dthread.count=5
```

### Проблема: "OutOfMemoryError при большом thread.count"

**Причина:** Недостаточно RAM

**Решение:**
```bash
# Увеличьте heap size
export MAVEN_OPTS="-Xmx4g"
mvn clean test -Pparallel -Dthread.count=8

# Или уменьшите thread.count
mvn clean test -Pparallel -Dthread.count=3
```

---

## Примеры для разных сценариев

### Дебаг одного теста:
```bash
# Последовательно, один тест, видим всё в браузере
mvn clean test -Dtest=LoginSuccessTests#testLoginWithValidCredentials
```

### Smoke прогон перед коммитом:
```bash
# Smoke тесты в 3 потока, headless
mvn clean test -Psmoke,parallel,headless -Dthread.count=3
```

### Полный регресс на CI:
```bash
# Все тесты в 8 потоков, headless, Chrome
mvn clean test -Pparallel,headless,chrome -Dthread.count=8
```

### Полный регресс со строгим контролем:
```bash
# СТРОГО 6 браузеров, headless
mvn clean test -Pparallel-strict,headless -Dthread.count=6
```

### Тестирование в разных браузерах:
```bash
# Firefox в 4 потока (быстрый режим)
mvn clean test -Pparallel,firefox -Dthread.count=4

# Edge в 2 потока (строгий режим)
mvn clean test -Pparallel-strict,edge -Dthread.count=2
```

### Стресс-тест со строгим контролем:
```bash
# СТРОГО 10 браузеров для нагрузочного тестирования
mvn clean test -Pparallel-strict -Dthread.count=10

# СТРОГО 5 Chrome браузеров в headless
mvn clean test -Pparallel-strict,chrome,headless -Dthread.count=5
```

---

## Summary

| Команда | Браузеров | Режим | Использование |
|---------|-----------|-------|---------------|
| `mvn clean test` | 1 | Последовательный | По умолчанию |
| `mvn clean test -Psequential` | 1 | Последовательный | Явно |
| `mvn clean test -Dthread.count=5` | 1 | Последовательный | ⚠️ thread.count игнорируется |
| `mvn clean test -Pparallel` | 1 | Параллельный | ⚠️ Бессмысленно (нет thread.count) |
| `mvn clean test -Pparallel -Dthread.count=3` | 3-4 | Параллельный (быстрый) | ✅ Рекомендуется |
| `mvn clean test -Pparallel -Dthread.count=10` | 10-11 | Параллельный (быстрый) | ✅ Рекомендуется |
| `mvn clean test -Pparallel-strict -Dthread.count=3` | **Строго 3** | Параллельный (строгий) | ✅ Точный контроль |
| `mvn clean test -Pparallel-strict -Dthread.count=10` | **Строго 10** | Параллельный (строгий) | ✅ Точный контроль |

- Хотите параллельно - используйте `-Pparallel` (быстро) или `-Pparallel-strict` (точно).
- Хотите **строго N браузеров** - используйте `-Pparallel-strict`.
