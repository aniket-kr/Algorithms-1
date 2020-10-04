package com.company.aniketkr.algorithms1.map.hash;

import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import com.company.aniketkr.algorithms1.map.OrderMap;
import com.company.aniketkr.algorithms1.map.symbol.UnorderedMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


public final class ChainingHashMap<K, V> implements Map<K, V> {
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
    if (obj instanceof OrderMap) {
      return obj.equals(this);
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> map = (Map<?, ?>) obj;
    if (this.size() != map.size()) {
      return false;
    }

    return mapEquals(map);
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

  @SuppressWarnings("unchecked")
  private boolean mapEquals(Map<?, ?> map) {
    try {
      for (Entry<?, ?> entry : map.entries()) {
        if (!Objects.deepEquals(entry.value(), this.get((K) entry.key()))) {
          // at least one entry has unequal values for the same key
          return false;
        }
      }
    } catch (ClassCastException | NoSuchElementException ok) {
      // ClassCastException     -> key types in the two maps are not interconvertible
      // NoSuchElementException -> key from `map` does not exist in `this` map
      return false;
    }

    return true;  // all key-value pairs are equal
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
    if (keyCopyFn == null) {
      throw new IllegalArgumentException("param 'keyCopyFn' cannot be null");
    }
    if (valueCopyFn == null) {
      throw new IllegalArgumentException("param 'valueCopyFn' cannot be null");
    }

    ChainingHashMap<K, V> cp = new ChainingHashMap<>((size() >= 2) ? (size() * 2) : INIT_CAPACITY);
    for (Entry<K, V> entry : this.entries()) {
      cp.put(keyCopyFn.apply(entry.key()), valueCopyFn.apply(entry.value()));
    }

    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public Iterable<K> keys() {
    return () -> new BucketMapIterator<>(map -> map.keys().iterator());
  }

  @Override
  public Iterable<V> values() {
    return () -> new BucketMapIterator<>(map -> map.values().iterator());
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new BucketMapIterator<>(map -> map.entries().iterator());
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