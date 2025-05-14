// rx/Emitter.java
package rx;

public interface Emitter<T> {
    void onNext(T item);
    void onError(Throwable error);
    void onComplete();
}
