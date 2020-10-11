package com.company.aniketkr.algorithms1.map.hash;

import com.company.aniketkr.algorithms1.Util;
import com.company.aniketkr.algorithms1.map.Entry;
import com.company.aniketkr.algorithms1.map.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;


/**
 * Extends the {@link Map} abstract class using an open-addressing based hash table. Keys and values
 * can be {@code null}. The Keys should be immutable and implement {@code hashCode()} and
 * {@code equals()} methods at the very least.
 *
 * @param <K> The type of key in the hash table. Can be {@code null}.
 * @param <V> The type of value to be associated with the keys.
 */
public final class ProbingHashMap<K, V> extends Map<K, V> {
  private static final byte INIT_CAPACITY = 4;  // byte saves space
  private static final byte LOAD_PERCENT = 65;  // similar to 0.65 load factor, byte saves space

  private final float loadFactor;  // ratio of number of entries in the map to the "size" of map.
  private int length = 0;  // actual number of entries in the map.
  private Node<K, V>[] table;  // array holding the hash map.

  /**
   * Instantiate an empty ProbingHashMap with capacity {@value INIT_CAPACITY} and fill it a maximum
   * of {@value LOAD_PERCENT}% capacity.
   */
  public ProbingHashMap() {
    this(INIT_CAPACITY, LOAD_PERCENT);
  }

  /**
   * Instantiate an empty ProbingHashMap with capacity {@code capacity} and fill it a maximum of
   * {@value LOAD_PERCENT}% of capacity.
   *
   * @param capacity The number of key-value pairs (or "entries") that map should be able to hold
   *                 without needing to resize when at 100% load.
   * @throws IllegalArgumentException If {@code capacity} is less that or equal to {@code 0}.
   */
  public ProbingHashMap(int capacity) {
    this(capacity, LOAD_PERCENT);
  }

  /**
   * Instantiate an empty ProbingHashMap with capacity {@value INIT_CAPACITY} and fill it a maximum
   * of {@code loadPercent} percent of capacity.
   *
   * @param loadPercent The percentage of capacity to actually utilize to store entries.
   * @throws IllegalArgumentException If {@code loadPercent} falls out of range of
   *                                  {@code (25, 100]}.
   */
  public ProbingHashMap(double loadPercent) {
    this(INIT_CAPACITY, loadPercent);
  }

  /**
   * Instantiate an empty ProbingHashMap with capacity {@code capacity} and fill it a maximum
   * of {@code loadPercent} percent of capacity.
   *
   * @param capacity    The number of key-value pairs (or "entries") that map should be able to hold
   *                    without needing to resize when at 100% load.
   * @param loadPercent The percentage of capacity to actually utilize to store entries.
   * @throws IllegalArgumentException If {@code capacity} is less that or equal to {@code 0}. Also,
   *                                  if {@code loadPercent} falls out of range of
   *                                  {@code (25, 100]}.
   */
  @SuppressWarnings("unchecked")
  public ProbingHashMap(int capacity, double loadPercent) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("invalid capacity: " + capacity);
    } else if (loadPercent <= 25 || loadPercent > 100) {
      throw new IllegalArgumentException("'loadPercent' is not in range (25, 100]");
    }

    this.loadFactor = (float) (loadPercent / 100D);
    table = (Node<K, V>[]) new Node[capacity];
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
    length = 0;
    table = (Node<K, V>[]) new Node[INIT_CAPACITY];
  }

  /**
   * {@inheritDoc}
   * Takes time proportional to <code>&theta;(c)</code> in the worst case, where {@code c} is the
   * capacity of map.
   */
  @Override
  public boolean contains(K key) {
    try {
      get(key);
      return true;
    } catch (NoSuchElementException ok) {
      // key doesn't exist, no problem
      return false;
    }
  }

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant time, subject to vary inversely with load percentage. Higher load factor equals
   * higher time (linear in worst case).
   */
  @Override
  public V get(K key) {
    int i = probeToFind(hash(key), key);
    if (i >= 0) {
      return table[i].value;
    }

    // key doesn't exist, throw exception
    String keyStr = Util.stringify(key);
    throw new NoSuchElementException(String.format("key '%s' does not exist in map", keyStr));
  }

  /**
   * {@inheritDoc}
   * Takes constant time, subject to vary inversely with load percentage. Higher load factor equals
   * higher time (linear in worst case).
   */
  @Override
  public V get(K key, V fallback) {
    int i = probeToFind(hash(key), key);
    return (i >= 0) ? table[i].value : fallback;
  }

  /**
   * {@inheritDoc}
   * Takes constant amortized time, subject to vary inversely with load percentage. Higher load
   * factor equals higher time. Takes linear time in worst case - resizing and rehashing.
   */
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

  /**
   * {@inheritDoc}
   * Takes constant amortized time. Linear time in worst case - downsizing and rehashing.
   */
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
    return deepcopyMaker(new ProbingHashMap<>(size(), loadFactor * 100), keyCopyFn, valueCopyFn);
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant extra space and time to construct.
   */
  @Override
  public Iterable<K> keys() {
    return () -> new ProbingMapIterator<>(node -> node.key);
  }

  /**
   * {@inheritDoc}
   * Takes constant extra space and time to construct.
   */
  @Override
  public Iterable<V> values() {
    return () -> new ProbingMapIterator<>(node -> node.value);
  }

  /**
   * {@inheritDoc}
   * Takes constant extra space and time to construct.
   */
  @Override
  public Iterable<Entry<K, V>> entries() {
    return () -> new ProbingMapIterator<>(node -> new Entry<>(node.key, node.value));
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ************************************************************************** */

  /**
   * Returns the hash of {@code key}.
   *
   * @param key The key to hash. If {@code key} is an array, then
   *            {@link Arrays#deepHashCode(Object[])} is used.
   * @return The hash code representing the index of placement.
   */
  private int hash(K key) {
    int h = (key instanceof Object[]) ? Arrays.deepHashCode((Object[]) key) : Objects.hashCode(key);
    return (h & 0x7f_fff_fff) % table.length;
  }

  /**
   * Rehash all the entries of "this" map into a new map that has capacity to hold {@code newSize}
   * number of entries. The load percentage is carried forward.
   *
   * @param newSize The new desired capacity of the map.
   */
  private void rehash(int newSize) {
    ProbingHashMap<K, V> cp = new ProbingHashMap<>(newSize, loadFactor * 100);
    for (Entry<K, V> entry : this.entries()) {
      cp.put(entry.key(), entry.value());
    }
    this.table = cp.table;
  }

  /**
   * Returns the next index in the internal hash table such that the index "wraps" to {@code 0}
   * after the last valid index hash been reached.
   *
   * @param index Index next to this index will be returned.
   * @return The next index, wrapped if hit an end.
   */
  private int nextIndex(int index) {
    return (index == table.length - 1) ? 0 : index + 1;
  }

  /**
   * Probes the hash table for {@code key}, starting at index {@code fromIndex}. Returns at the
   * first occurrence of {@code null} in table.
   *
   * @param fromIndex The index to start looking from.
   * @param key       The key to look for.
   * @return The index of node with {@code key}, else {@code -1} if not found.
   */
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

  /**
   * Probes the hash table looking for a place to insert {@code key}, starting from the index
   * {@code fromIndex}. Returns at first {@code null} in table or a "dead" {@link Node}, whichever
   * comes first.
   *
   * @param fromIndex The index to start looking from.
   * @param key       The key to look for.
   * @return Index where {@code key} can be inserted.
   */
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
    throw new InternalError("DEBUG: load is way too high, map is full, rehash wasn't called");
  }

  private static class Node<K, V> {
    K key;
    V value;
    boolean isDead = false;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
    }

    void revive(K key, V value) {
      // assert isDead;

      this.key = key;
      this.value = value;
      this.isDead = false;
    }

    void kill() {
      // assert !isDead;

      this.key = null;
      this.value = null;
      this.isDead = true;
    }
  }

  private class ProbingMapIterator<R> implements Iterator<R> {
    private final Function<? super Node<K, V>, R> resultFn; // accept node & produce return "entity"
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