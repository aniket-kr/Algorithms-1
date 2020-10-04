package com.company.aniketkr.algorithms1.map.hash;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.symbol.UnorderedMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


public final class ChainingHashMap<K, V> extends Map<K, V> {
  private static final int INIT_CAPACITY = 4;

  private Map<K, V>[] buckets;
  private int length = 0;

  public ChainingHashMap() {
    this(INIT_CAPACITY);
  }

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

  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    buckets = (Map<K, V>[]) new Map<?, ?>[INIT_CAPACITY];
    length = 0;
  }

  @Override
  public boolean contains(K key) {
    int h = hash(key);
    return buckets[h] != null && buckets[h].contains(key);
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  @Override
  public V get(K key) {
    int h = hash(key);

    if (buckets[h] != null) {
      return buckets[h].get(key);
    }

    // no "bucket" available, key not present
    String keyStr;
    if (key instanceof Object[]) {
      keyStr = Arrays.deepToString((Object[]) key);
    } else {
      keyStr = Objects.toString(key);
    }
    throw new NoSuchElementException(String.format("key '%s' doesn't exist in the map", keyStr));
  }

  @Override
  public V get(K key, V fallback) {
    int h = hash(key);

    return (buckets[h] != null) ? buckets[h].get(key, fallback) : fallback;
  }

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
    return deepcopyHelper(new ChainingHashMap<>(size()), keyCopyFn, valueCopyFn);
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return () -> new BucketMapIterator<>(bucket -> bucket.keys().iterator());
  }

  @Override
  public Iterable<V> values() {
    return () -> new BucketMapIterator<>(bucket -> bucket.values().iterator());
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new BucketMapIterator<>(bucket -> bucket.entries().iterator());
  }

  /* **************************************************************************
   * Section: Helper Classes and Methods
   ************************************************************************** */

  private int hash(K key) {
    if (key == null) {
      return 0;
    }

    int hash = key instanceof Object[] ? Arrays.deepHashCode((Object[]) key) : key.hashCode();
    return (hash & 0x7f_fff_fff) % buckets.length;
  }

  private void rehash(int newSize) {
    ChainingHashMap<K, V> hashMap = new ChainingHashMap<>(newSize);
    for (Entry<K, V> entry : this.entries()) {
      hashMap.put(entry.key(), entry.value());
    }

    this.buckets = hashMap.buckets;
  }

  private class BucketMapIterator<R> implements Iterator<R> {
    private final Function<? super Map<K, V>, Iterator<R>> itorFn;
    private int iterated = 0;
    private int bucketIndex = 0;
    private Iterator<R> curItor = null;

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
