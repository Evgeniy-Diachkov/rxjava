// rx/operators/FlatMapOperator.java
package rx.operators;

import rx.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class FlatMapOperator<T, R> extends Observable<R> {
    private final Observable<T> source;
    private final Function<T, Observable<R>> mapper;

    public FlatMapOperator(Observable<T> source, Function<T, Observable<R>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    protected void subscribeActual(Observer<R> downstream) {
        AtomicBoolean terminated = new AtomicBoolean(false);

        source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    Observable<R> inner = mapper.apply(item);
                    inner.subscribe(new Observer<R>() {
                        @Override
                        public void onNext(R innerItem) {
                            if (!terminated.get()) downstream.onNext(innerItem);
                        }

                        @Override
                        public void onError(Throwable error) {
                            if (terminated.compareAndSet(false, true)) {
                                downstream.onError(error);
                            }
                        }

                        @Override
                        public void onComplete() {
                            // no-op
                        }
                    });
                } catch (Throwable e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                if (terminated.compareAndSet(false, true)) {
                    downstream.onError(error);
                }
            }

            @Override
            public void onComplete() {
                if (terminated.compareAndSet(false, true)) {
                    downstream.onComplete();
                }
            }
        });
    }
}
