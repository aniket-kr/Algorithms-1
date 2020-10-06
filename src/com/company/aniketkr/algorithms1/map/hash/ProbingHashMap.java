package com.company.aniketkr.algorithms1.map.hash;


import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public final class ProbingHashMap<K, V> extends Map<K, V> {
  private static final byte INIT_CAPACITY = 4;  // byte saves space
  private static final byte LOAD_PERCENT = 70;  // similar to 0.70 load factor, byte saves space

  private final float loadFactor;
  private int length = 0;
  private Node<K, V>[] table;

  public ProbingHashMap() {
    this(INIT_CAPACITY, LOAD_PERCENT / 100D);
  }

  public ProbingHashMap(int capacity) {
    this(capacity, LOAD_PERCENT / 100D);
  }

  public ProbingHashMap(double loadFactor) {
    this(INIT_CAPACITY, loadFactor);
  }

  @SuppressWarnings("unchecked")
  public ProbingHashMap(int capacity, double loadFactor) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("invalid capacity: " + capacity);
    } else if (loadFactor <= 0.25 || loadFactor > 1) {
      throw new IllegalArgumentException("'loadFactor' is not in range (0.25, 1]");
    }

    this.loadFactor = (float) loadFactor;
    table = (Node<K, V>[]) new Node[capacity];
  }

  @Override
  public String toString() {
    return "PHM{" + "lF=" + loadFactor + ", len=" + length + ", table=" + Arrays.toString(table) + '}';
  }

  /* **************************************************************************
   * Section: Basic Operations
   ************************************************************************** */

  @Override
  public int size() {
    return length;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    length = 0;
    table = (Node<K, V>[]) new Node[INIT_CAPACITY];
  }

  @Override
  public boolean contains(K key) {
    return false;
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  @Override
  public V get(K key) {
    int i = probeToFind(hash(key), key);
    if (i >= 0) {
      return table[i].value;
    }

    // key doesn't exist, throw exception
    String keyStr;
    if (key instanceof Object[]) {
      keyStr = Arrays.deepToString((Object[]) key);
    } else {
      keyStr = Objects.toString(key);
    }
    throw new NoSuchElementException(String.format("key '%s' does not exist in map", keyStr));
  }

  @Override
  public V get(K key, V fallback) {
    int i = probeToFind(hash(key), key);
    return (i >= 0) ? table[i].value : fallback;
  }

  @Override
  public boolean put(K key, V value) {
    if (size() >= loadFactor * table.length) {
      rehash(table.length * 2);
    }

    int i = probeToInsert(hash(key), key);
    if (i < 0) {  // key already exists
      table[-(i + 1)].value = value;  // normalize index and set value
      return false;
    }

    // key doesn't exist
    if (table[i] == null) {  // create a new Node
      table[i] = new Node<>(key, value);
    } else {  // revive a dead node
      table[i].revive(key, value);
    }
    length++;
    return true;
  }

  @Override
  public boolean delete(K key) {
    if (size() <= 0.25 * table.length) {
      rehash(table.length / 2);
    }

    int i = probeToFind(hash(key), key);
    if (i < 0) {
      return false;  // key doesn't exist, do nothing
    }

    // key exists, delete it
    if (table[nextIndex(i)] == null) {
      table[i] = null;  // remove node entirely, next is null
    } else {
      table[i].kill();  // leave a dead node behind, next is a node
    }
    length--;
    return true;
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  @Override
  public ProbingHashMap<K, V> copy() {
    return (ProbingHashMap<K, V>) super.copy();
  }

  @Override
  public ProbingHashMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                       Function<? super V, V> valueCopyFn) {
    return deepcopyHelper(new ProbingHashMap<>(size()), keyCopyFn, valueCopyFn);
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return () -> new ProbingMapIterator<>(node -> node.key);
  }

  @Override
  public Iterable<V> values() {
    return () -> new ProbingMapIterator<>(node -> node.value);
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new ProbingMapIterator<>(node -> new Entry<>(node.key, node.value));
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ************************************************************************** */

  private int hash(K key) {
    int h = (key instanceof Object[]) ? Arrays.deepHashCode((Object[]) key) : Objects.hashCode(key);
    return (h & 0x7f_fff_fff) % table.length;
  }

  private void rehash(int newSize) {
    ProbingHashMap<K, V> cp = new ProbingHashMap<>(newSize, loadFactor);
    for (Entry<K, V> entry : this.entries()) {
      cp.put(entry.key(), entry.value());
    }
    this.table = cp.table;
  }

  private int nextIndex(int index) {
    return (index == table.length - 1) ? 0 : index + 1;
  }

  private int probeToFind(int fromIndex, K key) {
    int i = fromIndex;
    for (int counter = 0; counter < table.length; counter++, i = nextIndex(i)) {
      if (table[i] == null) {
        return -1;  // key not found
      } else if (!table[i].isDead && Objects.deepEquals(table[i].key, key)) {
        return i;  // key found
      }
    }

    return -1; // key not found (worst case, loadPercent == 100)
  }

  private int probeToInsert(int fromIndex, K key) {
    int i = fromIndex;
    for (int counter = 0; counter < table.length; counter++, i = nextIndex(i)) {
      if (table[i] == null || table[i].isDead) {
        return i;
      } else if (Objects.deepEquals(table[i].key, key)) {
        return -(i + 1);  // return negative of index + 1, when key exists
      }
    }

    // THIS SHOULD NEVER BE REACHED
    throw new InternalError("loadPercent is way too high, map is full");
  }

  private static class Node<K, V> {
    K key;
    V value;
    boolean isDead = false;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public String toString() {
      return (isDead ? 'D' : 'A') + "(" + key + ": " + value + ')';
    }

    void revive(K key, V value) {
      assert isDead;

      this.key = key;
      this.value = value;
      this.isDead = false;
    }

    void kill() {
      assert !isDead;

      this.key = null;
      this.value = null;
      this.isDead = true;
    }
  }

  private class ProbingMapIterator<R> implements Iterator<R> {
    private final Function<? super Node<K, V>, R> resultFn;
    private int iterated = 0;
    private int probedTill = 0;

    private ProbingMapIterator(Function<? super Node<K, V>, R> resultFn) {
      this.resultFn = resultFn;
    }

    @Override
    public boolean hasNext() {
      return iterated < size();
    }

    @Override
    public R next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      while (table[probedTill] == null || table[probedTill].isDead) {
        probedTill++;
      }

      iterated++;
      return resultFn.apply(table[probedTill++]);
    }
  }
}