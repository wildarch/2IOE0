package nl.tue.c2IOE0.group5.engine;

/**
 * @author s152717
 * a class that holds a value and tracks its previous value.
 * it does not register when any of its values is internally changed
 * @param <T> the object that can be updated, for structure it should be immutable
 */
public class Updatable<T> {
    private T current;
    private T previous;

    public Updatable(T current, T previous) {
        this.current = current;
        this.previous = previous;
    }

    public Updatable(T initial) {
        this.current = initial;
        this.previous = initial;
    }

    public T current() {
        return current;
    }

    public T previous() {
        return previous;
    }

    @Override
    public String toString() {
        return "[" + previous + " -> " + current + "]";
    }

    /**
     * sets the old current value to the new previous field, and the new current field to the new element
     * @param newElement the new current value
     */
    public void update(T newElement) {
        previous = current;
        current = newElement;
    }
}
