# RxJava Clone — Реактивная библиотека на Java

## 📘 Описание проекта

Реализация с нуля собственной версии библиотеки `RxJava`, демонстрирующей ключевые концепции реактивного программирования: `Observable`, `Observer`, `Schedulers`, `Disposable`, и операторы `map`, `filter`, `flatMap`.

---

## 🧱 Архитектура

### 🔹 Интерфейсы

| Интерфейс    | Назначение |
|--------------|------------|
| `Observer<T>` | Получает события: `onNext`, `onError`, `onComplete` |
| `Emitter<T>` | Источник событий |
| `Disposable` | Позволяет отменить подписку |
| `Scheduler`  | Выполняет задачи в нужных потоках |

### 🔹 Классы

| Класс                      | Назначение |
|----------------------------|------------|
| `Observable<T>`            | Абстракция потока данных |
| `ObservableCreate<T>`      | Источник потока |
| `ObservableMap<T, R>`      | Преобразует данные |
| `ObservableFilter<T>`      | Фильтрует элементы |
| `FlatMapOperator<T, R>`    | Разворачивает потоки |
| `ObservableSubscribeOn<T>` | Подписка в другом потоке |
| `ObservableObserveOn<T>`   | Обработка в другом потоке |
| `DisposableEmitter<T>`     | Контролирует поток и отмену |

---

## ⛓️ Работа Schedulers

| Scheduler                | Потоковый пул          | Аналог в RxJava |
|--------------------------|------------------------|------------------|
| `IOThreadScheduler`      | `CachedThreadPool`     | `Schedulers.io()` |
| `ComputationScheduler`   | `FixedThreadPool`      | `Schedulers.computation()` |
| `SingleThreadScheduler`  | `SingleThreadExecutor` | `Schedulers.single()` |

- `subscribeOn(...)` — выполнение подписки в нужном Scheduler
- `observeOn(...)` — обработка событий в другом Scheduler

---

## 🧪 Тестирование

| Тест                             | Назначение                        |
|----------------------------------|-----------------------------------|
| `testMapOperator()`              | Проверка `map()`                  |
| `testFilterOperator()`           | Проверка `filter()`               |
| `testFlatMapOperator()`          | Проверка `flatMap()`              |
| `testDisposableStopsEmission()`  | Проверка `Disposable.dispose()`   |
| `testErrorHandling()`            | Проверка `onError()`              |
| `testSchedulersConcurrency()`    | Проверка работы `Schedulers`      |

Фреймворк: **JUnit 5**  
Все тесты проходят успешно ✅

---

## ▶️ Пример использования

```java
Observable<Integer> numbers = Observable.create(em -> {
    for (int i = 1; i <= 5; i++) {
        em.onNext(i);
    }
    em.onComplete();
});

numbers
    .subscribeOn(new IOThreadScheduler())
    .observeOn(new ComputationScheduler())
    .filter(n -> n % 2 == 1)
    .map(n -> "Число: " + n)
    .flatMap(str -> Observable.create(em -> {
        em.onNext(str);
        em.onNext(str + "!");
        em.onComplete();
    }))
    .subscribeWith(new Subscriber<String>() {
        public void onSubscribe(Disposable d) { ... }
        public void onNext(String item) { System.out.println(item); }
        public void onError(Throwable t) { ... }
        public void onComplete() { ... }
    });
```

---

## 📁 Структура проекта

```
rxjava/
├── pom.xml
├── src/
│   ├── main/java/rx/...         # код библиотеки
│   └── test/java/rx/RxTests.java  # unit-тесты
```

---

## ✅ Статус

- Полностью соответствует техническому заданию
- Проект протестирован и готов к сдаче или публикации