// rx/Disposable.java
package rx;

@FunctionalInterface
public interface Disposable {
    void dispose();

    default boolean isDisposed() {
        return false;
    }
}
