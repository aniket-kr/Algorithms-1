package com.company.aniketkr.algorithms1.map.symbol;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.OrderMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
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
  public String toString() {
    return "OrderedMap{"
        + "length=" + length
        + ", keys=" + Arrays.toString(keys)
        + ", values=" + Arrays.toString(values)
        + '}';
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
    return null;
  }

  @Override
  public K max() {
    return null;
  }

  @Override
  public K floor(K key) {
    return null;
  }

  @Override
  public K ceil(K key) {
    return null;
  }

  @Override
  public int rank(K key) {
    return 0;
  }

  @Override
  public K select(int rank) {
    return null;
  }

  @Override
  public void deleteMin() {

  }

  @Override
  public void deleteMax() {

  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  @Override
  public OrderMap<K, V> copy() {
    return null;
  }

  @Override
  public OrderMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, Function<? super V, V> valueCopyFn) {
    return null;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return null;
  }

  @Override
  public Iterable<K> keys(K low, K high) {
    return null;
  }

  @Override
  public Iterable<V> values() {
    return null;
  }

  @Override
  public Iterable<Entry<? extends K, ? extends V>> entries() {
    return null;
  }

  @Override
  public Iterable<Entry<? extends K, ? extends V>> entries(K low, K high) {
    return null;
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
}
