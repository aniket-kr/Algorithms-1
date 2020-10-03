package com.company.aniketkr.algorithms1.map.symbol;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.OrderMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


/**
 * Implement the {@link Map} interface using internal resizing array. The keys
 * are not required to define any other method except
 * {@link Object#equals(Object obj) equals} method.
 *
 * @param <K> The type of key in the map. The key MUST override {@code equals}
 *            method. Keys can be {code null}.
 * @param <V> The type of value to be associated with the keys in the map.
 * @author Aniket Kumar
 */
public final class UnorderedMap<K, V> implements Map<K, V> {
  private static final int INIT_CAPACITY = 4;  // default init capacity of map

  private K[] keys;  // array holding keys of the map
  private V[] values;  // array holding values of the map
  private int length = 0;  // number of key-value pairs in the map

  /**
   * Instantiate an empty UnorderedMap instance which has the capacity
   * to hold {@value INIT_CAPACITY} entries before having to resize.
   */
  public UnorderedMap() {
    this(INIT_CAPACITY);
  }

  /**
   * Instantiate an empty UnorderedMap instance which has the capacity
   * to hold {@code capacity} entries before having to resize.
   *
   * @param capacity The desired number of entries that the map should be able
   *                 to hold without needing to resize.
   * @throws IllegalArgumentException If {@code capacity} is less than or equal
   *                                  to {code 0}.
   */
  @SuppressWarnings("unchecked")
  public UnorderedMap(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("invalid capacity: " + capacity);
    }

    keys = (K[]) new Object[capacity];
    values = (V[]) new Object[capacity];
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

  /**
   * Check if the given object {@code obj} is "deeply" equal to "this" map.
   * Takes quadratic time in the worst case.
   *
   * @return {@code true} if all the key-value pairs (or "entries") in both
   *     the maps are the same, {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (this == obj) {
      result = true;
    } else if (obj instanceof OrderMap) {
      result = obj.equals(this);
    } else if (obj instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) obj;
      if (map.size() == this.size()) {// compare all elements of two orderless maps - two instances of Map
        result = mapEquals(map);
      }
    }

    return result;
  }

  /**
   * Compare all the entries in this 'unordered-map' with another 'unordered-map'
   * and check if they are equal. Takes quadratic time in the worst case.
   */
  @SuppressWarnings("unchecked")
  private boolean mapEquals(Map<?, ?> map) {
    try {
      for (Entry<?, ?> entry : map.entries()) {
        if (!Objects.deepEquals(entry.value(), this.get((K) entry.key()))) {
          // at least one key-value pair exists with same key but different values
          return false;
        }
      }
    } catch (ClassCastException | NoSuchElementException ok) {
      // ClassCastException     -> keys of the maps are not interconvertible
      // NoSuchElementException -> key not found in the map
      return false;
    }

    return true;  // all key-value pairs are equal
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

  /**
   * {@inheritDoc}
   * Takes constant time.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    keys = (K[]) new Object[INIT_CAPACITY];
    values = (V[]) new Object[INIT_CAPACITY];
    length = 0;
  }

  /**
   * {@inheritDoc}
   * Takes linear time in both the average and the worst case.
   */
  @Override
  public boolean contains(K key) {
    return findKeyIndex(key) >= 0;
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes linear time to find the key.
   */
  @Override
  public V get(K key) {
    int i = findKeyIndex(key);
    if (i >= 0) {
      return values[i];
    }

    // `key` doesn't exist
    String keyS = (key instanceof Object[]) ? Arrays.deepToString((Object[]) key) : key.toString();
    throw new NoSuchElementException(String.format("key '%s' doesn't exist in the map", keyS));
  }

  /**
   * {@inheritDoc}
   * Takes linear time to find the key.
   */
  @Override
  public V get(K key, V fallback) {
    int i = findKeyIndex(key);
    return (i >= 0) ? values[i] : fallback;
  }

  /**
   * {@inheritDoc}
   * It is a linear time operation in both average and worst case.
   */
  @Override
  public boolean put(K key, V value) {
    int i = findKeyIndex(key);

    if (i >= 0) {  // `key` exists
      values[i] = value;
      return false;
    }

    // `key` doesn't exist
    if (size() == keys.length) {
      resize(keys.length * 2);
    }

    keys[length] = key;
    values[length++] = value;
    return true;
  }

  /**
   * {@inheritDoc}
   * This operation takes linear time in both average and worst case.
   */
  @Override
  public boolean delete(K key) {
    int i = findKeyIndex(key);
    if (i < 0) {  // `key` doesn't exist
      return false;
    }

    // `key` does exist
    shiftLeftByOnePosition(i + 1);
    keys[--length] = null;
    values[length] = null;

    if (size() == keys.length / 4) {
      resize(keys.length / 2);
    }

    return true;  // `key` existed and was deleted
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  @Override
  public UnorderedMap<K, V> copy() {
    UnorderedMap<K, V> cp = new UnorderedMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY);
    System.arraycopy(this.keys, 0, cp.keys, 0, this.size());
    System.arraycopy(this.values, 0, cp.values, 0, this.size());
    cp.length = this.length;

    return cp;
  }

  @Override
  public UnorderedMap<K, V> deepcopy(Function<? super K, K> keyCopyFn,  //
                                     Function<? super V, V> valueCopyFn) {
    if (keyCopyFn == null || valueCopyFn == null) {
      throw new IllegalArgumentException("at least one argument to deepcopy() is null");
    }

    UnorderedMap<K, V> cp = new UnorderedMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY);
    // deepcopy all the pairs
    this.entries().forEach(entry -> {
      cp.put(keyCopyFn.apply(entry.key()), valueCopyFn.apply(entry.value()));
    });

    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and time to construct.
   */
  @Override
  public Iterable<K> keys() {
    return () -> new MapIterator<>(keys, length - 1);
  }

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and time to construct.
   */
  @Override
  public Iterable<V> values() {
    return () -> new MapIterator<>(values, length - 1);
  }

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and time to construct.
   */
  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new EntryIterator<>(keys, values, length - 1);
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ****************************************************************************/

  /**
   * Find the index at which {@code key} is located in the {@code keys} array.
   * The time taken by the operation is linear.
   *
   * @param key The key to look for.
   * @return The index at which {@code key} is at, or {@code -1} if not present.
   */
  private int findKeyIndex(K key) {
    for (int i = 0; i < length; i++) {
      if (Objects.equals(key, keys[i])) {
        return i;
      }
    }

    // `key` wasn't found
    return -1;
  }

  /**
   * Resize the internal {@code keys} and {@code values} arrays to the given
   * new size {@code newSize}. Resizing is achieved by copying over the elements
   * from the original arrays to the new arrays. Expectedly, the operation is a
   * linear time one.
   *
   * @param newSize The new desired size of the internal arrays.
   */
  @SuppressWarnings("unchecked")
  private void resize(int newSize) {
    V[] newValues = (V[]) new Object[newSize];
    System.arraycopy(values, 0, newValues, 0, size());
    values = newValues;

    K[] newKeys = (K[]) new Object[newSize];
    System.arraycopy(keys, 0, newKeys, 0, size());
    keys = newKeys;
  }

  /**
   * Shift the elements of both the key and value arrays over to the left
   * by one position.
   *
   * @param fromIndex The index to start shifting elements from.
   */
  private void shiftLeftByOnePosition(int fromIndex) {
    System.arraycopy(values, fromIndex, values, fromIndex - 1, size() - fromIndex);
    System.arraycopy(keys, fromIndex, keys, fromIndex - 1, size() - fromIndex);
  }

  private static class MapIterator<T> implements Iterator<T> {
    private final T[] arr;
    private final int stop;
    private int current = 0;

    private MapIterator(T[] array, int stopInclusive) {
      stop = stopInclusive;
      arr = array;
    }

    @Override
    public boolean hasNext() {
      return current <= stop;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      return arr[current++];
    }
  }

  private static class EntryIterator<K, V> implements Iterator<Entry<K, V>> {
    private final K[] keys;
    private final V[] values;
    private final int stop;
    private int current = 0;

    private EntryIterator(K[] keys, V[] values, int stopInclusive) {
      this.keys = keys;
      this.values = values;
      stop = stopInclusive;
    }

    @Override
    public boolean hasNext() {
      return current <= stop;
    }

    @Override
    public Entry<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      return new Entry<>(keys[current], values[current++]);
    }
  }
}
