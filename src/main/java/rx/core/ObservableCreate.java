// rx/core/ObservableCreate.java
package rx.core;

import rx.*;

public class ObservableCreate<T> extends Observable<T> {

    private final OnSubscribe<T> source;

    public ObservableCreate(OnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        DisposableEmitter<T> emitter = new DisposableEmitter<>(observer);
        try {
            source.subscribe(emitter);
        } catch (Throwable t) {
            emitter.onError(t);
        }
    }
}
