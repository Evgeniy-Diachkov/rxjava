// rx/core/ObservableSubscribeOn.java
package rx.core;

import rx.*;
import rx.schedulers.Scheduler;

public class ObservableSubscribeOn<T> extends Observable<T> {

    private final Observable<T> source;
    private final Scheduler scheduler;

    public ObservableSubscribeOn(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        scheduler.execute(() -> source.subscribe(observer));
    }
}
