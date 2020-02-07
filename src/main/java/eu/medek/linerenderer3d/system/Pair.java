package eu.medek.linerenderer3d.system;

/**
 * Class containing a pair of values.
 * @param <U> type of the first object
 * @param <V> type of the second object
 */
public class Pair <U, V> {
    /**
     * First object.
     */
    private U first;
    /**
     * Second object.
     */
    private V second;

    /**
     * Basic constructor
     * @param first value of the first object
     * @param second value of the second object
     */
    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Getter for the first object.
     * @return value of the first object
     */
    public U getFirst() {
        return first;
    }

    /**
     * Getter for the second object.
     * @return value of the second object
     */
    public V getSecond() {
        return second;
    }

    /**
     * Setter for the first object.
     * @param first new value for the first object
     */
    public void setFirst(U first) {
        this.first = first;
    }

    /**
     * Setter for the second object.
     * @param second new value for the second object
     */
    public void setSecond(V second) {
        this.second = second;
    }
}
