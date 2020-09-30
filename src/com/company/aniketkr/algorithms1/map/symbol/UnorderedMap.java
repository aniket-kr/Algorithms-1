package com.company.aniketkr.algorithms1.map.symbol;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.OrderMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


public class UnorderedMap<K, V> implements Map<K, V> {
  private static final int INIT_CAPACITY = 4;

  private K[] keys;
  private V[] values;
  private int length = 0;

  public UnorderedMap() {
    this(INIT_CAPACITY);
  }

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
    // TODO: implementation of hashCode()
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    // TODO: job for later.
    return super.equals(obj);
  }

  private boolean equals(OrderMap<?, ?> orderMap) {
    return false; // placeholder
  }

  private boolean equals(Map<?, ?> map) {
    return false;  // placeholder
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[0]{ }";
    }

    StringBuilder sb = new StringBuilder("[").append(size()).append("]{ ");
    this.entries().forEach(kv -> sb
        .append(kv.key())
        .append(": ")
        .append(kv.value())
        .append(", ")
    );

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
    keys = (K[]) new Object[INIT_CAPACITY];
    values = (V[]) new Object[INIT_CAPACITY];
    length = 0;
  }

  @Override
  public boolean contains(K key) {
    return findKeyIndex(key) >= 0;
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  @Override
  public V get(K key) {
    int i = findKeyIndex(key);
    if (i >= 0) {
      return values[i];
    }

    // `key` doesn't exist
    throw new NoSuchElementException(String.format("key '%s' doesn't exist in the map", key));
  }

  @Override
  public V get(K key, V fallback) {
    int i = findKeyIndex(key);
    return (i >= 0) ? values[i] : fallback;
  }

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
  public UnorderedMap<K, V> deepcopy(Function<? super K, K> keyCopyFn,
                                     Function<? super V, V> valueCopyFn) {
    if (keyCopyFn == null || valueCopyFn == null) {
      throw new IllegalArgumentException("at least one argument to deepcopy() is null");
    }

    UnorderedMap<K, V> cp = new UnorderedMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY);
    // deepcopy all the pairs
    this.entries().forEach(kv -> {
      cp.put(keyCopyFn.apply(kv.key()), valueCopyFn.apply(kv.value()));
    });

    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return () -> new MapIterator<>(keys, length - 1);
  }

  @Override
  public Iterable<V> values() {
    return () -> new MapIterator<>(values, length - 1);
  }

  @Override
  public Iterable<Entry<? extends K, ? extends V>> entries() {
    return () -> new EntryIterator<>(keys, values, length - 1);
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ****************************************************************************/

  private int findKeyIndex(K key) {
    for (int i = 0; i < length; i++) {
      if (Objects.equals(key, keys[i])) {
        return i;
      }
    }

    // `key` wasn't found
    return -1;
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

  private void shiftLeftByOnePosition(int fromIndex) {
    System.arraycopy(keys, fromIndex, keys, fromIndex - 1, size() - fromIndex);
    System.arraycopy(values, fromIndex, values, fromIndex - 1, size() - fromIndex);
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

  private static class EntryIterator<K, V> implements Iterator<Entry<? extends K, ? extends V>> {
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
    public Entry<? extends K, ? extends V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      return new Entry<>(keys[current], values[current++]);
    }
  }
}
