import java.util.Objects;

public class Pair<K,V> {
    private K key;
    private V value;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Check if both references are the same
        if (obj == null || getClass() != obj.getClass()) return false; // Check type

        Pair<?, ?> other = (Pair<?, ?>) obj; // Cast to generic Pair
        return Objects.equals(key, other.key) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
