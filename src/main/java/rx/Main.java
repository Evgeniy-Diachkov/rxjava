package rx;// Main.java

import rx.schedulers.*;


public class Main {
    public static void main(String[] args) {
        Scheduler io = new IOThreadScheduler();
        Scheduler computation = new ComputationScheduler();

        Observable<Integer> numbers = Observable.create(emitter -> {
            Disposable disposable = (Disposable) emitter;

            for (int i = 1; i <= 5; i++) {
                // Первая проверка — перед onNext
                if (disposable.isDisposed()) {
                    System.out.println("⛔ Отмена на i = " + i + " (до onNext, thread: " + Thread.currentThread().getName() + ")");
                    return;
                }

                emitter.onNext(i);

                // Добавляем задержку после onNext
                try {
                    Thread.sleep(100); // ← ключевой момент!
                } catch (InterruptedException e) {
                    emitter.onError(e);
                    return;
                }

                // Вторая проверка — после onNext, до следующей итерации
                if (disposable.isDisposed()) {
                    System.out.println("⛔ Отмена на i = " + (i + 1) + " (после onNext, thread: " + Thread.currentThread().getName() + ")");
                    return;
                }
            }

            emitter.onComplete();
        });

        numbers
                .subscribeOn(io)
                .observeOn(computation)
                .filter(n -> n % 2 == 1)
                .map(n -> "Число: " + n)
                .<String>flatMap(str -> Observable.create(em -> {
                    em.onNext(str);
                    em.onNext(str + "!");
                    em.onComplete();
                }))
                .subscribeWith(new Subscriber<String>() {
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.disposable = d;
                        System.out.println("📌 Подписка началась (thread: " + Thread.currentThread().getName() + ")");
                    }

                    @Override
                    public void onNext(String item) {
                        System.out.println("➡ " + item + " (thread: " + Thread.currentThread().getName() + ")");
                        if (item.contains("3")) {
                            System.out.println("🛑 Отписываюсь на " + item);
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("❌ Ошибка: " + error.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("✅ Поток завершён");
                    }
                });

        try {
            Thread.sleep(2000); // дать завершиться всем потокам
        } catch (InterruptedException ignored) {}

        System.out.println("🏁 Main завершён (thread: " + Thread.currentThread().getName() + ")");
    }
}
