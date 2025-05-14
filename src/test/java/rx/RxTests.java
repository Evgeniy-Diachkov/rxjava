package rx;

import org.junit.jupiter.api.Test;
import rx.schedulers.ComputationScheduler;
import rx.schedulers.IOThreadScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class RxTests {

    @Test
    void testMapOperator() {
        List<String> result = new ArrayList<>();

        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onComplete();
        });

        observable
                .map(i -> "Item: " + i)
                .subscribe(new Observer<String>() {
                    public void onNext(String item) { result.add(item); }
                    public void onError(Throwable t) { fail("Should not error"); }
                    public void onComplete() {}
                });

        assertEquals(List.of("Item: 1", "Item: 2"), result);
    }

    @Test
    void testFilterOperator() {
        List<Integer> result = new ArrayList<>();

        Observable<Integer> observable = Observable.create(emitter -> {
            for (int i = 1; i <= 5; i++) emitter.onNext(i);
            emitter.onComplete();
        });

        observable
                .filter(i -> i % 2 == 0)
                .subscribe(new Observer<Integer>() {
                    public void onNext(Integer item) { result.add(item); }
                    public void onError(Throwable t) { fail("Should not error"); }
                    public void onComplete() {}
                });

        assertEquals(List.of(2, 4), result);
    }

    @Test
    void testFlatMapOperator() {
        List<String> result = new ArrayList<>();

        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onComplete();
        });

        observable
                .<String>flatMap(i -> Observable.<String>create(em -> {
                    em.onNext("A" + i);
                    em.onNext("B" + i);
                    em.onComplete();
                }))
                .subscribe(new Observer<String>() {
                    public void onNext(String item) { result.add(item); }
                    public void onError(Throwable t) { fail("Should not error"); }
                    public void onComplete() {}
                });

        assertEquals(List.of("A1", "B1"), result);
    }

    @Test
    void testDisposableStopsEmission() {
        List<Integer> result = new ArrayList<>();

        Observable<Integer> observable = Observable.create(emitter -> {
            for (int i = 1; i <= 5; i++) {
                emitter.onNext(i);
                if (emitter instanceof Disposable d && d.isDisposed()) break;
            }
            emitter.onComplete();
        });

        observable.subscribeWith(new Subscriber<Integer>() {
            private Disposable disposable;

            public void onSubscribe(Disposable d) { this.disposable = d; }
            public void onNext(Integer item) {
                result.add(item);
                if (item == 2) disposable.dispose();
            }
            public void onError(Throwable t) {}
            public void onComplete() {}
        });

        assertEquals(List.of(1, 2), result);
    }

    @Test
    void testErrorHandling() {
        List<String> log = new ArrayList<>();

        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onError(new RuntimeException("Test error"));
        });

        observable.subscribe(new Observer<Integer>() {
            public void onNext(Integer item) { log.add("OK " + item); }
            public void onError(Throwable t) { log.add("ERR " + t.getMessage()); }
            public void onComplete() { log.add("DONE"); }
        });

        assertEquals(List.of("OK 1", "ERR Test error"), log);
    }

    @Test
    void testSchedulersConcurrency() throws InterruptedException {
        List<String> result = new ArrayList<>();
        CountDownLatch onNextLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(1);

        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(42);
            emitter.onComplete();
        });

        observable
                .subscribeOn(new IOThreadScheduler())
                .observeOn(new ComputationScheduler())
                .subscribe(new Observer<Integer>() {
                    public void onNext(Integer item) {
                        result.add("Item: " + item + " Thread: " + Thread.currentThread().getName());
                        onNextLatch.countDown();
                    }

                    public void onError(Throwable t) {
                        fail("Should not error: " + t.getMessage());
                    }

                    public void onComplete() {
                        completeLatch.countDown();
                    }
                });

        boolean gotValue = onNextLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
        boolean completed = completeLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);

        assertTrue(gotValue, "onNext was not called in time");
        assertTrue(completed, "onComplete was not called in time");
        assertFalse(result.isEmpty(), "Result should contain at least one item");
        assertTrue(result.get(0).contains("Item: 42"), "Expected output not found");
    }
}
