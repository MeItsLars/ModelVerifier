package ru.informationsystems.objects.population;

/**
 * This generic class represents a pair of two values.
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class Pair<K, V> {

    // The key element
    private K k;
    // The value element
    private V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    /**
     * @return The key of this pair
     */
    public K getKey() {
        return k;
    }

    /**
     * @return The value of this pair
     */
    public V getValue() {
        return v;
    }
}
