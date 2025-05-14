// rx/Subscriber.java
package rx;

public interface Subscriber<T> extends Observer<T> {
    void onSubscribe(Disposable d);
}
