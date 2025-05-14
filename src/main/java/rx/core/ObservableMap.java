// rx/core/ObservableMap.java
package rx.core;

import rx.Observable;
import rx.Observer;

import java.util.function.Function;

public class ObservableMap<T, R> extends Observable<R> {
    private final Observable<T> source;
    private final Function<T, R> mapper;

    public ObservableMap(Observable<T> source, Function<T, R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    protected void subscribeActual(Observer<R> observer) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    observer.onNext(mapper.apply(item));
                } catch (Throwable e) {
                    observer.onError(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                observer.onError(error);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }
        });
    }
}
