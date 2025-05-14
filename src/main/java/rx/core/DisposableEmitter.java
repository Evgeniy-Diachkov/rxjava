// rx/core/DisposableEmitter.java
package rx.core;

import rx.Disposable;
import rx.Emitter;
import rx.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class DisposableEmitter<T> implements Emitter<T>, Disposable, Observer<T> {

    private final Observer<? super T> observer;

    private final AtomicBoolean disposed = new AtomicBoolean(false);
    private final AtomicBoolean terminated = new AtomicBoolean(false);

    public DisposableEmitter(Observer<? super T> observer) {
        this.observer = observer;
    }

    @Override
    public void onNext(T item) {
        if (!isDisposed() && !terminated.get()) {
            observer.onNext(item);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (!isDisposed() && terminated.compareAndSet(false, true)) {
            observer.onError(error);
            dispose();
        }
    }

    @Override
    public void onComplete() {
        if (!isDisposed() && terminated.compareAndSet(false, true)) {
            observer.onComplete();
            dispose();
        }
    }

    @Override
    public void dispose() {
        disposed.set(true);
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }
}
