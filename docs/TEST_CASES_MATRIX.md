# Матрица тест-кейсов

## Обзор

Данный фреймворк реализует **30 критичных функциональных UI тест-кейсов** для тестирования сайта [SauceDemo](https://www.saucedemo.com).

## Распределение по модулям

| Модуль | Количество тестов | Критичность |
|--------|-------------------|-------------|
| Login | 5 | Critical |
| Inventory | 6 | Critical/High |
| Cart | 8 | Critical |
| Checkout | 8 | Critical/High |
| Navigation | 3 | Critical/High |
| **Итого** | **30** | - |

## Детальная матрица тест-кейсов

### МОДУЛЬ: АВТОРИЗАЦИЯ (Login)

| ID | Название | Приоритет | Тег |
|----|----------|-----------|-----|
| TC-001 | Успешная авторизация со стандартным пользователем | Critical | login, smoke |
| TC-002 | Авторизация с невалидным username | Critical | login |
| TC-003 | Авторизация с невалидным password | Critical | login |
| TC-004 | Авторизация с пустыми полями | High | login |
| TC-005 | Авторизация с заблокированным пользователем | Critical | login |

### МОДУЛЬ: КАТАЛОГ ТОВАРОВ (Inventory)

| ID | Название | Приоритет | Тег |
|----|----------|-----------|-----|
| TC-006 | Отображение всех товаров на странице каталога | Critical | inventory |
| TC-007 | Сортировка товаров по имени (A to Z) | High | inventory |
| TC-008 | Сортировка товаров по имени (Z to A) | High | inventory |
| TC-009 | Сортировка товаров по цене (Low to High) | Critical | inventory |
| TC-010 | Сортировка товаров по цене (High to Low) | Critical | inventory |
| TC-011 | Переход на страницу детальной информации о товаре | Critical | inventory |

### МОДУЛЬ: КОРЗИНА (Cart)

| ID | Название | Приоритет | Тег |
|----|----------|-----------|-----|
| TC-012 | Добавление товара в корзину из каталога | Critical | cart, smoke |
| TC-013 | Добавление товара в корзину со страницы детальной информации | Critical | cart |
| TC-014 | Удаление товара из корзины через каталог | Critical | cart |
| TC-015 | Добавление нескольких товаров в корзину | Critical | cart |
| TC-016 | Переход в корзину через иконку корзины | Critical | cart |
| TC-017 | Отображение товаров в корзине | Critical | cart |
| TC-018 | Удаление товара из корзины на странице корзины | Critical | cart |
| TC-019 | Кнопка "Continue Shopping" в корзине | High | cart |

### МОДУЛЬ: ЧЕКАУТ (Checkout)

| ID | Название | Приоритет | Тег |
|----|----------|-----------|-----|
| TC-021 | Заполнение информации о покупателе с валидными данными | Critical | checkout, smoke |
| TC-022 | Попытка продолжить чекаут без заполнения First Name | High | checkout |
| TC-023 | Попытка продолжить чекаут без заполнения Last Name | High | checkout |
| TC-024 | Попытка продолжить чекаут без заполнения Zip Code | High | checkout |
| TC-025 | Проверка корректности расчета общей суммы заказа | Critical | checkout |
| TC-026 | Завершение покупки (кнопка Finish) | Critical | checkout, smoke |
| TC-027 | Кнопка Cancel на странице информации о покупателе | High | checkout |
| TC-028 | Кнопка Cancel на странице обзора заказа | High | checkout |

### МОДУЛЬ: НАВИГАЦИЯ (Navigation)

| ID | Название | Приоритет | Тег |
|----|----------|-----------|-----|
| TC-020 | Навигация через "All Items" в бургер-меню | High | navigation |
| TC-029 | Logout через бургер-меню | Critical | navigation |
| TC-030 | Сброс состояния приложения через Reset App State | High | navigation |

## Статистика покрытия

### По приоритетам

| Приоритет | Количество | Процент |
|-----------|------------|---------|
| Critical | 21 | 70% |
| High | 9 | 30% |

### По smoke тестам

Smoke тесты (5 тестов):
- TC-001: Login success
- TC-012: Add to cart
- TC-021: Checkout info
- TC-026: Complete order

### Реализованные паттерны

1. **Page Object Model** - для всех страниц приложения
2. **Data Transfer Objects (DTO)** - для моделирования данных
3. **Fluent Interface** - для улучшения читаемости тестов
4. **Builder Pattern** - для создания тестовых данных
5. **Factory Pattern** - для управления конфигурацией

### Поддерживаемые возможности

- ✅ Параллельное выполнение тестов
- ✅ Поддержка 4 браузера (Chrome, Firefox, Edge, Safari)
- ✅ Headless режим
- ✅ Настраиваемое количество потоков
- ✅ Allure отчеты
- ✅ Детальное логирование
- ✅ Скриншоты при ошибках
- ✅ CI/CD интеграция
- ✅ Docker поддержка

### Контакты

- Framework Owner: Vitaliy Popravka
- Repository: https://github.com/Sherlock0731/qa-ui-framework
- Documentation: docs/
- Issues: https://github.com/Sherlock0731/qa-ui-framework/issues