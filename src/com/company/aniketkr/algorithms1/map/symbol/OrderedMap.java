package com.company.aniketkr.algorithms1.map.symbol;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.OrderMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


/**
 * Extend the {@link OrderMap} abstract class using internal resizing arrays. The arrays resize by
 * a factor of 2. If nothing is specified during construction, the default capacity of the map is
 * {@value INIT_CAPACITY}.
 *
 * <p>Certain methods of the class will throw {@link IllegalStateException} if the keys do not
 * satisfy the condition of being comparable (naturally or via comparator). If this happens then
 * the behaviour is undefined.</p>
 *
 * @param <K> The type of key in the map. This type must implement the {@link Comparable} interface.
 *            If it does not, then a {@link Comparator} must be provided at the time of
 *            construction. Keys must be non-{@code null}.
 * @param <V> The type of value that should be associated with the keys in the map. These can be
 *            {@code null}.
 * @author Aniket Kumar
 */
public final class OrderedMap<K, V> extends OrderMap<K, V> {
  private static final int INIT_CAPACITY = 4;  // default capacity of map
  private static final byte LEFT = -1;  // helper for `shift` method
  private static final byte RIGHT = 1;  // helper for `shift` method

  private final Comparator<K> comp;
  private int length = 0;
  private K[] keys;
  private V[] values;

  /**
   * Instantiate an empty OrderedMap instance that has capacity to hold {@value INIT_CAPACITY}
   * key-value pairs (or "entries") before having to resize. It is assumed that the keys will be
   * comparable using the {@link Comparable#compareTo(K)} method.
   */
  public OrderedMap() {
    this(INIT_CAPACITY, null);
  }

  /**
   * Instantiate an empty OrderedMap instance that has capacity to hold {@code capacity} key-value
   * pairs (or "entries") before having to resize. It is assumed that the keys will be comparable
   * using the {@link Comparable#compareTo(K)} method.
   *
   * @param capacity The desired initial capacity of the map.
   * @throws IllegalArgumentException If {@code capacity} is less than or equal to {@code 0}.
   */
  public OrderedMap(int capacity) {
    this(capacity, null);
  }

  /**
   * Instantiate an empty OrderedMap instance that has capacity to hold {@value INIT_CAPACITY}
   * key-value pairs (or "entries") before having to resize. The keys will be compared using
   * {@code comparator}, disregarding the {@link Comparable} implementation, if any.
   *
   * @param comparator The comparator to use.
   */
  public OrderedMap(Comparator<K> comparator) {
    this(INIT_CAPACITY, comparator);
  }

  /**
   * Instantiate an empty OrderedMap instance that has capacity to hold {@code capacity} key-value
   * pairs (or "entries") before having to resize. The keys will be compared using
   * {@code comparator}, disregarding the {@link Comparable} implementation.
   *
   * @param capacity   The desired initial capacity of the map.
   * @param comparator The comparator to use.
   * @throws IllegalArgumentException if {@code capacity} is less than or equal to {@code 0}.
   */
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
    length = 0;
    keys = (K[]) new Object[INIT_CAPACITY];
    values = (V[]) new Object[INIT_CAPACITY];
  }

  /**
   * {@inheritDoc}
   * Takes logarithmic time.
   */
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

  /**
   * {@inheritDoc}
   * Takes logarithmic time for a successful search.
   */
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

  /**
   * {@inheritDoc}
   * Takes logarithmic time just like {@link #get(K)}.
   */
  @Override
  public V get(K key, V fallback) {
    try {
      return get(key);
    } catch (NoSuchElementException ok) {
      // key not found, no problem
      return fallback;
    }
  }

  /**
   * {@inheritDoc}
   * Takes linear time in the both the average case and the worst case.
   */
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

  /**
   * {@inheritDoc}
   * Takes linear time in both average and worst case.
   */
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

  /**
   * {@inheritDoc}
   * It is a constant time operation.
   */
  @Override
  public K min() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't find minimum, map is empty");
    }

    return keys[0];
  }

  /**
   * {@inheritDoc}
   * It is a constant time operation.
   */
  @Override
  public K max() {
    if (isEmpty()) {
      throw new NoSuchElementException("can't find maximum, map is empty");
    }

    return keys[length - 1];
  }

  /**
   * {@inheritDoc}
   * Takes logarithmic time to find the floor.
   */
  @Override
  public K floor(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = floorIndex(key);
    return (i < 0) ? null : keys[i];
  }

  /**
   * {@inheritDoc}
   * Takes logarithmic time to find the ceiling.
   */
  @Override
  public K ceil(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = ceilIndex(key);
    return (i < 0) ? null : keys[i];
  }

  /**
   * {@inheritDoc}
   * Takes logarithmic time to find the rank.
   */
  @Override
  public int rank(K key) {
    if (key == null) {
      throw new IllegalArgumentException("param 'key' cannot be null");
    }

    int i = search(key);
    // If key exists, index is rank. Else normalize index.
    return (i >= 0) ? i : -(i + 1);
  }

  /**
   * {@inheritDoc}
   * It is a constant time operation.
   */
  @Override
  public K select(int rank) {
    Objects.checkIndex(rank, size());

    return keys[rank];
  }

  /**
   * {@inheritDoc}
   * Always takes linear time.
   */
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

  /**
   * {@inheritDoc}
   * This operation takes constant time in the best and linear time in the worst case.
   */
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

  /**
   * {@inheritDoc}
   *
   * @return A shallow copy of {@code this} OrderedMap.
   */
  @Override
  public OrderedMap<K, V> copy() {
    OrderedMap<K, V> cp = new OrderedMap<>(size(), comp);
    System.arraycopy(this.keys, 0, cp.keys, 0, this.size());
    System.arraycopy(this.values, 0, cp.values, 0, this.size());
    cp.length = this.length;

    return cp;
  }

  /**
   * {@inheritDoc}
   *
   * @return A deepcopy of {@code this} OrderedMap.
   */
  @Override
  public OrderedMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                   Function<? super V, V> valueCopyFn) {
    return super.deepcopyHelper(new OrderedMap<>(size(), comp), keyCopyFn, valueCopyFn);
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
    return () -> new MapIterator<>(keys, 0, length - 1);
  }

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and logarithmic to construct.
   */
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

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and time to construct.
   */
  @Override
  public Iterable<V> values() {
    return () -> new MapIterator<>(values, 0, length - 1);
  }

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and time to construct.
   */
  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new EntryIterator<>(keys, values, 0, length - 1);
  }

  /**
   * {@inheritDoc}
   * The iterable takes constant extra space and logarithmic time to construct.
   */
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

  /**
   * A wrapper function that wraps {@link Arrays#binarySearch(K[], int, int, K, Comparator)} to
   * avoid repetition of code. Takes logarithmic time.
   *
   * @param key The non-{@code null} key to look for.
   * @return The index at which {@code key} is located. If {@code key} doesn't exist, then returns
   *     negative of the index at which the key should be located, plus 1.
   *     For eg, if {@code key} should have been at index {@code 5}, but does not exist, then will
   *     return {@code -6}.
   * @throws IllegalStateException If the keys are cannot be compared (neither by Comparator nor
   *                               by Comparable).
   */
  private int search(K key) {
    try {
      return Arrays.binarySearch(keys, 0, length, key, comp);
    } catch (ClassCastException fatalException) {
      throw new IllegalStateException("The Key type neither implements Comparable interface, nor" //
          + " was a Comparator provided during time of construction");
    }
  }

  /**
   * Resize the internal keys and values arrays to have length {@code newSize}. Takes time
   * proportional to <code>&theta;(n)</code> and extra space linearly proportional to
   * {@code newSize}. {@code n} here is the {@link #size()} of the map.
   *
   * @param newSize The new desired capacity of the map.
   */
  @SuppressWarnings("unchecked")
  private void resize(int newSize) {
    K[] newKeys = (K[]) new Object[newSize];
    System.arraycopy(keys, 0, newKeys, 0, size());
    keys = newKeys;

    V[] newValues = (V[]) new Object[newSize];
    System.arraycopy(values, 0, newValues, 0, size());
    values = newValues;
  }

  /**
   * Shift the elements {@link #LEFT} or {@link #RIGHT} in both key and value arrays. Helper method
   * for other map operations. Note that the method assumes that the internal arrays have sufficient
   * space to be shift elements. Takes time linearly proportional to {@code size() - fromIndex}.
   *
   * @param fromIndex The index to start shifting elements from.
   * @param direction The direction to shift elements towards. Either {@link #LEFT} or
   *                  {@link #RIGHT}.
   */
  private void shift(int fromIndex, int direction) {
    System.arraycopy(keys, fromIndex, keys, fromIndex + direction, size() - fromIndex);
    System.arraycopy(values, fromIndex, values, fromIndex + direction, size() - fromIndex);
  }

  /**
   * Find the index at which the floor of a given key is located. Takes logarithmic time.
   *
   * @param key The non-{@code null} key.
   * @return {@code -1} of floor doesn't exist. Otherwise the index.
   */
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

  /**
   * Find the index at which the ceiling of a given key is located. Takes logarithmic time.
   *
   * @param key The non-{@code null} key.
   * @return {@code -1} of ceiling doesn't exist. Otherwise the index.
   */
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

      // if both the current and stop happen to be negative, it indicates
      // that the iterator is to be empty. Adjust current to be larger that stop
      // to achieve the same.
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

      // if both the current and stop happen to be negative, it indicates
      // that the iterator is to be empty. Adjust current to be larger that stop
      // to achieve the same.
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
