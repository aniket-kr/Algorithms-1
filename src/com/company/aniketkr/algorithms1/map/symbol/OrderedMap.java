package com.company.aniketkr.algorithms1.map.symbol;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.OrderMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public class OrderedMap<K, V> implements OrderMap<K, V> {
  private static final int INIT_CAPACITY = 4;
  private static final byte LEFT = -1;
  private static final byte RIGHT = 1;

  private final Comparator<K> comp;
  private int length = 0;
  private K[] keys;
  private V[] values;

  public OrderedMap() {
    this(INIT_CAPACITY, null);
  }

  public OrderedMap(int capacity) {
    this(capacity, null);
  }

  public OrderedMap(Comparator<K> comparator) {
    this(INIT_CAPACITY, comparator);
  }

  @SuppressWarnings("unchecked")
  public OrderedMap(int capacity, Comparator<K> comparator) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("invalid capacity: " + capacity);
    }

    keys = (K[]) new Object[capacity];
    values = (V[]) new Object[capacity];

    comp = comparator;
  }

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  public int hashCode() {
    long hash = 0L;
    for (Entry<K, V> entry : this.entries()) {
      hash += entry.hashCode();
    }

    return (int) (hash % Integer.MAX_VALUE);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> map = (Map<?, ?>) obj;
    if (map.size() != this.size()) {
      return false;
    }

    return (map instanceof OrderMap) ? orderMapEquals((OrderMap<?, ?>) map) : mapEquals(map);
  }

  @SuppressWarnings("unchecked")
  private boolean mapEquals(Map<?, ?> map) {
    try {
      for (Entry<?, ?> entry : map.entries()) {
        if (!Objects.deepEquals(entry.value(), this.get((K) entry.key()))) {
          // the values corresponding to a particular key don't match
          return false;
        }
      }
      return true;  // the maps have equal entries

    } catch (ClassCastException | IllegalArgumentException | NoSuchElementException ok) {
      // ClassCastException       -> keys of the maps are not interconvertible
      // IllegalArgumentException -> key from UnorderedMap `map` must be null
      // NoSuchElementException   -> key not found in the map
      return false;
    }
  }

  private boolean orderMapEquals(OrderMap<?, ?> orderMap) {
    Iterator<Entry<K, V>> itor = this.entries().iterator();
    for (Entry<?, ?> entry : orderMap.entries()) {
      if (!entry.equals(itor.next())) {
        return false;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[0]{ }";
    }

    StringBuilder sb = new StringBuilder("[").append(size()).append("]{ ");
    this.entries().forEach(entry -> sb.append(entry).append(", "));
    sb.setLength(sb.length() - 2);
    return sb.append(" }").toString();
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
    keys = (K[]) new Object[INIT_CAPACITY];
    values = (V[]) new Object[INIT_CAPACITY];
  }

  @Override
  public boolean contains(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    return search(key) >= 0;
  }

  @Override
  public Comparator<K> comparator() {
    return comp;
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  @Override
  public V get(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = search(key);
    if (i >= 0) {
      // key was found
      return values[i];
    }

    // key wasn't found
    throw new NoSuchElementException(String.format("key '%s' doesn't exist in map", key));
  }

  @Override
  public V get(K key, V fallback) {
    try {
      return get(key);
    } catch (NoSuchElementException ok) {
      // key not found, no problem
      return fallback;
    }
  }

  @Override
  public boolean put(K key, V value) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = search(key);
    if (i >= 0) {
      // key already exists, update value
      values[i] = value;
      return false;
    }

    // key doesn't exist

    if (size() == keys.length) {
      resize(keys.length * 2);
    }

    i = -(i + 1);  // normalize index

    shift(i, RIGHT);
    keys[i] = key;
    values[i] = value;
    length++;
    return true;
  }

  @Override
  public boolean delete(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = search(key);
    if (i < 0) {
      // key doesn't exist, return false
      return false;
    }

    // key exists

    shift(i + 1, LEFT);
    keys[--length] = null;
    values[length] = null;

    if (size() == keys.length / 4) {
      resize(keys.length / 2);
    }

    return true;  // key-value pair was deleted
  }

  /* **************************************************************************
   * Section: OrderMap Operations
   ************************************************************************** */

  @Override
  public K min() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't find minimum, map is empty");
    }

    return keys[0];
  }

  @Override
  public K max() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't find maximum, map is empty");
    }

    return keys[length - 1];
  }

  @Override
  public K floor(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = floorIndex(key);
    return (i < 0) ? null : keys[i];
  }

  @Override
  public K ceil(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = ceilIndex(key);
    return (i < 0) ? null : keys[i];
  }

  @Override
  public int rank(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = search(key);
    // If key exists, index is rank. Else normalize index.
    return (i >= 0) ? i : -(i + 1);
  }

  @Override
  public K select(int rank) {
    Objects.checkIndex(rank, size());

    return keys[rank];
  }

  @Override
  public void deleteMin() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't delete-minimum from empty map");
    }

    shift(1, LEFT);
    keys[--length] = null;
    values[length] = null;

    if (size() == keys.length / 4) {
      resize(keys.length / 2);
    }
  }

  @Override
  public void deleteMax() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't delete-maximum from empty map");
    }

    keys[--length] = null;
    values[length] = null;

    if (size() == keys.length / 4) {
      resize(keys.length / 2);
    }
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  @Override
  public OrderedMap<K, V> copy() {
    OrderedMap<K, V> cp = new OrderedMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY, comp);
    System.arraycopy(this.keys, 0, cp.keys, 0, this.size());
    System.arraycopy(this.values, 0, cp.values, 0, this.size());
    cp.length = this.length;

    return cp;
  }

  @Override
  public OrderedMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                   Function<? super V, V> valueCopyFn) {
    if (keyCopyFn == null) {
      throw new IllegalArgumentException("param 'keyCopyFn' cannot be null");
    }
    if (valueCopyFn == null) {
      throw new IllegalArgumentException("param 'valueCopyFn' cannot be null");
    }

    keyCopyFn = keyCopyFn.andThen(key -> //
        Objects.requireNonNull(key, "'keyCopyFn' returned null"));

    OrderedMap<K, V> cp = new OrderedMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY, comp);
    for (Entry<? extends K, ? extends V> kv : this.entries()) {
      cp.put(keyCopyFn.apply(kv.key()), valueCopyFn.apply(kv.value()));
    }

    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return () -> new MapIterator<>(keys, 0, length - 1);
  }

  @Override
  public Iterable<K> keys(K low, K high) {
    if (low == null) {
      throw new IllegalArgumentException("param 'low' cannot be null");
    }
    if (high == null) {
      throw new IllegalArgumentException("param 'high' cannot be null");
    }

    return () -> new MapIterator<>(keys, ceilIndex(low), floorIndex(high));
  }

  @Override
  public Iterable<V> values() {
    return () -> new MapIterator<>(values, 0, length - 1);
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new EntryIterator<>(keys, values, 0, length - 1);
  }

  @Override
  public Iterable<Entry<K, V>> entries(K low, K high) {
    if (low == null) {
      throw new IllegalArgumentException("param 'low' cannot be null");
    }
    if (high == null) {
      throw new IllegalArgumentException("param 'high' cannot be null");
    }

    return () -> new EntryIterator<>(keys, values, ceilIndex(low), floorIndex(high));
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ************************************************************************** */

  private int search(K key) {
    return Arrays.binarySearch(keys, 0, length, key, comp);
  }

  @SuppressWarnings("unchecked")
  private void resize(int newSize) {
    K[] newKeys = (K[]) new Object[newSize];
    System.arraycopy(keys, 0, newKeys, 0, size());
    keys = newKeys;

    V[] newValues = (V[]) new Object[newSize];
    System.arraycopy(values, 0, newValues, 0, size());
    values = newValues;
  }

  private void shift(int fromIndex, int direction) {
    System.arraycopy(keys, fromIndex, keys, fromIndex + direction, size() - fromIndex);
    System.arraycopy(values, fromIndex, values, fromIndex + direction, size() - fromIndex);
  }

  private int floorIndex(K key) {
    int i = search(key);
    if (i >= 0) {
      // key exists in the map
      return i;
    }

    // when key doesn't exist
    i = -(i + 1);  // normalize index
    return (i == 0) ? -1 : i - 1;
  }

  private int ceilIndex(K key) {
    int i = search(key);
    if (i >= 0) {
      // key exists in the map
      return i;
    }

    // when key doesn't exist
    i = -(i + 1);  // normalize index
    return (i == size()) ? -1 : i;
  }

  private static class MapIterator<T> implements Iterator<T> {
    private final T[] arr;
    private final int stop;
    private int current;

    private MapIterator(T[] array, int startInclusive, int stopInclusive) {
      arr = array;
      this.current = startInclusive >= 0 ? startInclusive : -1;
      this.stop = (stopInclusive >= 0) ? stopInclusive : -2;
    }

    @Override
    public boolean hasNext() {
      return current <= stop;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException("iterator depleted");
      }

      return arr[current++];
    }
  }

  private static class EntryIterator<K, V> implements Iterator<Entry<K, V>> {
    private final K[] keys;
    private final V[] values;
    private final int stop;
    private int current;

    private EntryIterator(K[] keys, V[] values, int startInclusive, int stopInclusive) {
      this.keys = keys;
      this.values = values;
      this.current = startInclusive >= 0 ? startInclusive : -1;
      this.stop = (stopInclusive >= 0) ? stopInclusive : -2;
    }

    @Override
    public boolean hasNext() {
      return current <= stop;
    }

    @Override
    public Entry<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException("iterator depleted");
      }

      return new Entry<>(keys[current], values[current++]);
    }
  }
}
