package ru.trjamus.HW;

public class MegaHashMap <K,V>{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;

    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

    }

    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Node<K, V> node = table[index];

        while (node != null) {
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            node = node.next;
        }

        table[index] = new Node<>(hash, key, value, table[index]);
        size++;

        if (size > threshold) {
            resize();
        }
        return null;
    }

    public V get(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Node<K, V> node = table[index];

        while (node != null) {
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V remove(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Node<K, V> node = table[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public MegaHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public MegaHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);

        this.loadFactor = DEFAULT_LOAD_FACTOR;

        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;

        this.threshold = (int) (cap * loadFactor);
        this.table = new Node[cap];
    }

    private int hash(K key) {
        if (key == null) return 0;

        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCap = oldTable.length;
        int newCap = oldCap << 1;
        if (newCap == 0) newCap = DEFAULT_INITIAL_CAPACITY;

        Node<K, V>[] newTable = new Node[newCap];
        threshold = (int) (newCap * loadFactor);

        for (int i = 0; i < oldCap; i++) {
            Node<K, V> node = oldTable[i];
            while (node != null) {
                Node<K, V> next = node.next;
                int index = indexFor(node.hash, newCap);
                node.next = newTable[index];
                newTable[index] = node;
                node = next;
            }
        }
        table = newTable;
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
}