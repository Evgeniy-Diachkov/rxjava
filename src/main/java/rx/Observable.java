// rx/Observable.java
package rx;

import rx.core.*;
import rx.operators.FlatMapOperator;
import rx.schedulers.Scheduler;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Observable<T> {

    public static <T> Observable<T> create(OnSubscribe<T> source) {
        return new ObservableCreate<>(source);
    }

    public Disposable subscribe(Observer<T> observer) {
        subscribeInternal(observer, false);
        return () -> {};
    }

    @SuppressWarnings("unchecked")
    public Disposable subscribeWith(Subscriber<? super T> subscriber) {
        DisposableEmitter<T> emitter = (DisposableEmitter<T>) new DisposableEmitter<>(subscriber);
        subscriber.onSubscribe(emitter);
        try {
            subscribeInternal(emitter, true);
        } catch (Throwable t) {
            emitter.onError(t);
        }
        return emitter;
    }

    protected void subscribeInternal(Observer<T> observer, boolean withDisposable) {
        this.subscribeActual(observer);
    }

    protected abstract void subscribeActual(Observer<T> observer);

    public <R> Observable<R> map(Function<T, R> mapper) {
        return new ObservableMap<>(this, mapper);
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return new ObservableFilter<>(this, predicate);
    }

    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        return new FlatMapOperator<>(this, mapper);
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new ObservableSubscribeOn<>(this, scheduler);
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return new ObservableObserveOn<>(this, scheduler);
    }

    public interface OnSubscribe<T> {
        void subscribe(Emitter<T> emitter);
    }
}
