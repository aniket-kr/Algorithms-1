package com.company.aniketkr.algorithms1.map.hash;

import com.company.aniketkr.algorithms1.Util;
import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.symbol.UnorderedMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


/**
 * Extends {@link Map} abstract class using an internal hash table with buckets. The default number
 * of entries that it holds is {@value INIT_CAPACITY}, which can be changed at the time of
 * construction. All the time complexities in method documentations are proposed under the
 * <em>uniform hashing assumption</em>.
 *
 * @param <K> The type of key in the map. MUST be immutable. Can be {@code null}. If the keys are
 *            not mutable, and their hashcode changes, then the behaviour is undefined.
 * @param <V> The type of value to be associated with keys in the map. Can be {@code null}.
 * @author Aniket Kumar
 */
public final class ChainingHashMap<K, V> extends Map<K, V> {
  private static final int INIT_CAPACITY = 4;  // default number of buckets

  private Map<K, V>[] buckets;  // array holding buckets
  private int length = 0;  // number of key-value pairs in the map

  /**
   * Initialise an empty ChainingHashMap instance which has the capacity to hold
   * {@value INIT_CAPACITY} key-value pairs (or "entries").
   */
  public ChainingHashMap() {
    this(INIT_CAPACITY);
  }

  /**
   * Initialise an empty ChainingHashMap instance which has the capacity to hold {@code capacity}
   * key-value pairs (or "entries").
   *
   * @throws IllegalArgumentException If {@code capacity} is less than or equal to {@code 0}.
   */
  @SuppressWarnings("unchecked")
  public ChainingHashMap(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("invalid capacity: " + capacity);
    }

    buckets = (Map<K, V>[]) new Map<?, ?>[capacity];
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
   * It is a constant time operation.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    buckets = (Map<K, V>[]) new Map<?, ?>[INIT_CAPACITY];
    length = 0;
  }

  /**
   * {@inheritDoc}
   * Takes constant time under uniform hashing assumption.
   */
  @Override
  public boolean contains(K key) {
    int h = hash(key);
    return buckets[h] != null && buckets[h].contains(key);
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant amortized time in average case. Takes linear time in worst case.
   */
  @Override
  public V get(K key) {
    int h = hash(key);

    if (buckets[h] != null) {
      return buckets[h].get(key);
    }

    // no "bucket" available, key not present
    String keyStr = Util.stringify(key);
    throw new NoSuchElementException(String.format("key '%s' doesn't exist in the map", keyStr));
  }

  /**
   * {@inheritDoc}
   * Assuming a uniform hash, it should be constant time operation.
   */
  @Override
  public V get(K key, V fallback) {
    int h = hash(key);

    return (buckets[h] != null) ? buckets[h].get(key, fallback) : fallback;
  }

  /**
   * {@inheritDoc}
   * Takes constant amortized time in average case, linear time in worst case.
   */
  @Override
  public boolean put(K key, V value) {
    if (size() == buckets.length) {
      rehash(buckets.length * 2);
    }

    int h = hash(key);

    if (buckets[h] == null) {
      buckets[h] = new UnorderedMap<>(2);
    }

    boolean keyWasPut = buckets[h].put(key, value);
    if (keyWasPut) {
      length++;
    }
    return keyWasPut;
  }

  /**
   * {@inheritDoc}
   * Takes constant amortized time in average case, but linear time in worst case.
   */
  @Override
  public boolean delete(K key) {
    if (size() == buckets.length / 4) {
      rehash(buckets.length / 2);
    }

    int h = hash(key);
    if (buckets[h] == null) {
      // no bucket, no key
      return false;
    }

    boolean keyWasDeleted = buckets[h].delete(key);
    if (keyWasDeleted) {
      // is bucket is empty, delete the bucket
      if (buckets[h].isEmpty()) {
        buckets[h] = null;
      }
      length--;
    }

    return keyWasDeleted;
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  @Override
  public ChainingHashMap<K, V> copy() {
    return deepcopy(Function.identity(), Function.identity());
  }

  @Override
  public ChainingHashMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                        Function<? super V, V> valueCopyFn) {
    return deepcopyMaker(new ChainingHashMap<>(size()), keyCopyFn, valueCopyFn);
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant time and space to construct the iterable.
   */
  @Override
  public Iterable<K> keys() {
    return () -> new BucketMapIterator<>(bucket -> bucket.keys().iterator());
  }

  /**
   * {@inheritDoc}
   * Takes constant time and space to construct the iterable.
   */
  @Override
  public Iterable<V> values() {
    return () -> new BucketMapIterator<>(bucket -> bucket.values().iterator());
  }

  /**
   * {@inheritDoc}
   * Takes constant time and space to construct the iterable.
   */
  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new BucketMapIterator<>(bucket -> bucket.entries().iterator());
  }

  /* **************************************************************************
   * Section: Helper Classes and Methods
   ************************************************************************** */

  /**
   * Gets the index after applying internal hash function. Note that this function is highly
   * dependent on the {@code hashCode()} implementation of the key.
   *
   * @param key The key to hash. Can be {@code null}.
   * @return The index at which this key should be at.
   */
  private int hash(K key) {
    if (key == null) {
      return 0;
    }

    int hash = key instanceof Object[] ? Arrays.deepHashCode((Object[]) key) : key.hashCode();
    return (hash & 0x7f_fff_fff) % buckets.length;
  }

  /**
   * Rehashes all the keys in the map when the size of the buckets array changes. Takes linear time
   * and linear extra space.
   *
   * @param newSize The new desired capacity of the internal array.
   */
  private void rehash(int newSize) {
    ChainingHashMap<K, V> hashMap = new ChainingHashMap<>(newSize);
    for (Entry<K, V> entry : this.entries()) {
      hashMap.put(entry.key(), entry.value());
    }

    this.buckets = hashMap.buckets;
  }

  private class BucketMapIterator<R> implements Iterator<R> {

    /** Takes bucket (map) and returns its iterator. */
    private final Function<? super Map<K, V>, Iterator<R>> itorFn;

    private int iterated = 0;  // count of entities iterated over
    private int bucketIndex = 0;  // index of outer buckets array
    private Iterator<R> curItor = null;  // current iterator to produce return entity from

    private BucketMapIterator(Function<? super Map<K, V>, Iterator<R>> itorFn) {
      this.itorFn = itorFn;

      if (!isEmpty()) {
        renewItor();
      }
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
      if (!curItor.hasNext()) {
        renewItor();
      }

      iterated++;
      return curItor.next();
    }

    private void renewItor() {
      while (buckets[bucketIndex] == null) {
        bucketIndex++;
      }

      curItor = itorFn.apply(buckets[bucketIndex++]);
    }
  }
}
