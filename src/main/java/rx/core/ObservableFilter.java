// rx/core/ObservableFilter.java
package rx.core;

import rx.Observable;
import rx.Observer;

import java.util.function.Predicate;

public class ObservableFilter<T> extends Observable<T> {
    private final Observable<T> source;
    private final Predicate<T> predicate;

    public ObservableFilter(Observable<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                try {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
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
