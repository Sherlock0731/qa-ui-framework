# Maven Profiles - Quick Reference

## Execution Modes (Режимы выполнения)

### Sequential (Последовательный) - по умолчанию
```bash
# Без профиля (default)
mvn clean test

# Явно указать
mvn clean test -Psequential
```
- Один тест за раз
- Минимум ресурсов
- Легко отлаживать
- Медленно для больших наборов

---

### Parallel (Параллельный) - быстрый
```bash
mvn clean test -Pparallel -Dthread.count=4
```
- Быстрое выполнение
- Умеренное потребление ресурсов
- Может быть N+1 браузеров кратковременно
- **Рекомендуется для большинства случаев**

**Технология:** JUnit 5 ForkJoinPool, один JVM процесс

---

### Parallel Strict (Строгий параллельный) - точный контроль
```bash
mvn clean test -Pparallel-strict -Dthread.count=4
```
- **СТРОГО N браузеров** (никогда не превысит!)
- Больше потребление RAM (N отдельных JVM)
- Медленнее чем `-Pparallel`
- Используйте когда важна точность

**Технология:** Maven Surefire forkCount, N отдельных JVM процессов

---

## Browser Profiles (Профили браузеров)

```bash
-Pchrome      # Chrome (default)
-Pfirefox     # Firefox
-Pedge        # Microsoft Edge
-Psafari      # Safari (только macOS)
```

**Комбинирование:**
```bash
mvn clean test -Pparallel,firefox -Dthread.count=3
mvn clean test -Pparallel-strict,edge -Dthread.count=2
```

---

## Test Group Profiles (Профили групп тестов)

```bash
-Plogin       # Только login тесты (5 тестов)
-Pinventory   # Только inventory тесты (6 тестов)
-Pcart        # Только cart тесты (5 тестов)
-Pcheckout    # Только checkout тесты (6 тестов)
-Pnavigation  # Только navigation тесты (5 тестов)
-Psmoke       # Только smoke тесты (5 тестов)
```

**Комбинирование:**
```bash
mvn clean test -Plogin,parallel -Dthread.count=4
mvn clean test -Psmoke,parallel-strict -Dthread.count=3
```

---

## Other Profiles (Другие профили)

### Headless Mode
```bash
-Pheadless    # Запуск без GUI (быстрее, меньше ресурсов)
```

**Примеры:**
```bash
mvn clean test -Pparallel,headless -Dthread.count=8
mvn clean test -Pparallel-strict,headless,chrome -Dthread.count=5
```

---

## Real-World Examples (Реальные примеры)

### Разработка - быстрая проверка
```bash
# Smoke тесты в 3 потока
mvn clean test -Psmoke,parallel,headless -Dthread.count=3
```

### Pre-commit - быстрая валидация
```bash
# Login тесты параллельно
mvn clean test -Plogin,parallel -Dthread.count=4
```

### CI/CD - полный регресс
```bash
# Все тесты в 8 потоков, headless
mvn clean test -Pparallel,headless -Dthread.count=8
```

### Production - стабильный запуск
```bash
# СТРОГО 5 браузеров, без перегрузки
mvn clean test -Pparallel-strict,headless -Dthread.count=5
```

### Стресс-тест - максимальная нагрузка
```bash
# СТРОГО 10 браузеров для нагрузочного тестирования
mvn clean test -Pparallel-strict -Dthread.count=10
```

### Кросс-браузерное тестирование
```bash
# Firefox в 4 потока
mvn clean test -Pparallel,firefox -Dthread.count=4

# Edge в 2 потока со строгим контролем
mvn clean test -Pparallel-strict,edge -Dthread.count=2
```

---

## Common Mistakes (Частые ошибки)

### НЕПРАВИЛЬНО:
```bash
# thread.count без профиля - ИГНОРИРУЕТСЯ!
mvn clean test -Dthread.count=5

# Профиль parallel без thread.count - бессмысленно
mvn clean test -Pparallel

# Попытка использовать оба профиля одновременно
mvn clean test -Pparallel,parallel-strict -Dthread.count=4
```

### ПРАВИЛЬНО:
```bash
# Быстрый параллельный запуск
mvn clean test -Pparallel -Dthread.count=5

# Строгий контроль
mvn clean test -Pparallel-strict -Dthread.count=5

# Последовательный (явно)
mvn clean test -Psequential
```

---

## Decision Tree (Дерево решений)

```
Нужно запустить тесты?
│
├─ Один тест / отладка?
│  └─ mvn clean test (или -Psequential)
│
├─ Несколько тестов быстро?
│  └─ Допустимо N+1 браузеров кратковременно?
│     ├─ ДА  → mvn clean test -Pparallel -Dthread.count=N
│     └─ НЕТ → mvn clean test -Pparallel-strict -Dthread.count=N
│
├─ Полный регресс?
│  └─ mvn clean test -Pparallel,headless -Dthread.count=8
│
└─ Production / важный прогон?
   └─ mvn clean test -Pparallel-strict,headless -Dthread.count=5
```

---

## Profile Priority (Приоритет профилей)

Если указано несколько конфликтующих профилей, применяется **последний**:

```bash
# Будет использован parallel-strict (последний)
mvn clean test -Pparallel -Pparallel-strict -Dthread.count=4

# Будет использован sequential (последний)
mvn clean test -Pparallel -Psequential
```

**Рекомендация:** Используйте **только один** execution mode профиль!

---

## Quick Commands Cheat Sheet

```bash
# Самые частые команды:

# Разработка (быстро)
mvn clean test -Psmoke,parallel -Dthread.count=3

# Pre-commit (валидация)
mvn clean test -Plogin,parallel -Dthread.count=4

# CI/CD (полный прогон)
mvn clean test -Pparallel,headless -Dthread.count=8

# Production (стабильно)
mvn clean test -Pparallel-strict,headless -Dthread.count=5

# Отладка (один тест)
mvn clean test -Dtest=LoginSuccessTests

# Отчет
mvn allure:serve
```

---

## Performance Tips (Советы по производительности)

1. **Headless всегда быстрее:** `-Pheadless` экономит 20-30% времени
2. **Оптимальный thread.count:** 
   - Локально: 3-5 потоков
   - CI/CD: 6-10 потоков
   - Production: 4-6 потоков (strict)
3. **Smoke тесты первыми:** Быстрая валидация перед полным прогоном
4. **Используйте группы:** Не запускайте все тесты, если нужна только одна область

---

## Troubleshooting

**Проблема:** Открывается 1 браузер при `-Dthread.count=5`  
**Решение:** Добавьте `-Pparallel`

**Проблема:** Открывается 6 браузеров при `-Dthread.count=5`  
**Решение:** Используйте `-Pparallel-strict` вместо `-Pparallel`

**Проблема:** OutOfMemoryError  
**Решение:** Уменьшите thread.count или используйте `-Pheadless`

**Проблема:** Тесты падают в параллельном режиме  
**Решение:** Проблемы с thread-safety, используйте `-Psequential` для отладки

---

## Summary

| Задача | Команда |
|--------|---------|
| **Обычная разработка** | `mvn clean test -Pparallel -Dthread.count=3` |
| **Точный контроль** | `mvn clean test -Pparallel-strict -Dthread.count=4` |
| **CI/CD pipeline** | `mvn clean test -Pparallel,headless -Dthread.count=8` |
| **Production тесты** | `mvn clean test -Pparallel-strict,headless -Dthread.count=5` |
| **Отладка** | `mvn clean test` |

**Главное правило:** 90% случаев используйте `-Pparallel`, для точного контроля - `-Pparallel-strict`.
