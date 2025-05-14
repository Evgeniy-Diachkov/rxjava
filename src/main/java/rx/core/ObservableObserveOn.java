// rx/core/ObservableObserveOn.java
package rx.core;

import rx.*;
import rx.schedulers.Scheduler;

public class ObservableObserveOn<T> extends Observable<T> {

    private final Observable<T> source;
    private final Scheduler scheduler;

    public ObservableObserveOn(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                scheduler.execute(() -> observer.onNext(item));
            }

            @Override
            public void onError(Throwable error) {
                scheduler.execute(() -> observer.onError(error));
            }

            @Override
            public void onComplete() {
                scheduler.execute(observer::onComplete);
            }
        });
    }
}
