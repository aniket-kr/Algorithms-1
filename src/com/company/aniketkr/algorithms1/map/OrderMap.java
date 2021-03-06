package com.company.aniketkr.algorithms1.map;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Extends the {@link Map} abstract class to take advantage of the fact that keys have a defined
 * ordering (natural or otherwise). The concrete subclasses of this abstract class should accept all
 * key types and an optional {@link Comparator} that compares keys.
 *
 * <p>If no comparator is provided during construction, then an attempt to use the natural ordering
 * (defined by {@link Comparable} interface) will be made. If that fails, then <b>some operations on
 * the instance will result in an {@link IllegalStateException} being thrown</b>. The behaviour is
 * undefined.</p>
 *
 * @param <K> The type of key in the map. This should either define a natural ordering via
 *            {@code Comparable} interface or a comparator should be provided at the time of
 *            construction.
 * @param <V> The type of values associated to the keys in the map.
 * @author Aniket Kumar
 */
public abstract class OrderMap<K, V> extends Map<K, V> {

  /* **************************************************************************
   * Section: Basic Operations
   ************************************************************************** */

  /**
   * Get the count of number of key-value pairs in the map.
   *
   * @return Number of key-value pairs in the map.
   */
  @Override
  public abstract int size();

  /**
   * Check if the map is empty.
   *
   * @return {@code true} if the map has exactly {@code 0} key-value pairs, {@code false}
   *     otherwise.
   */
  @Override
  public abstract boolean isEmpty();

  /**
   * Clears the map of all its key-value pairs.
   * Optionally, it may set the internal state of the map to the default initialisation state.
   */
  @Override
  public abstract void clear();

  /**
   * Check if {@code key} has some value mapped to it in the map.
   *
   * @param key The key.
   * @return {@code true} if {@code key} has mapping in the map, {@code false} otherwise.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  @Override
  public abstract boolean contains(K key);

  /**
   * Return the comparator being used for comparing keys, {@code null} if natural order is being
   * used instead.
   */
  public abstract Comparator<K> comparator();

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * Get the value associated with {@code key} from the map, provided {@code key} exists in the map.
   *
   * @param key The key.
   * @return The associated value.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   * @throws NoSuchElementException   If {@code key} does not exist in the map.
   */
  @Override
  public abstract V get(K key);

  /**
   * Get the value associated with {@code key} from the map, if {@code key}
   * exists in the map. If not, then returns {@code fallback} value.
   *
   * @param key      The key.
   * @param fallback The "value" to return when {@code key} doesn't exist.
   * @return The value associated with {@code key} or {@code fallback} depending on existence of
   *     {@code key} in the map.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  @Override
  public abstract V get(K key, V fallback);

  /**
   * Puts the given key-value pair in the map. If {@code key} happens to already exist in the map,
   * then the associated value is updated to {@code value}.
   *
   * @param key   The key to put or update in the map.
   * @param value The value to associate with {@code key}.
   * @return {@code true} if {@code key} did not exist in the map and a new item was inserted,
   *     {@code false} if only the associated value was updated. In other words, if {@link #size()}
   *     of the map increased because of this operation, then {@code true} is returned.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  @Override
  public abstract boolean put(K key, V value);

  /**
   * Delete {@code key} and its associated value from the map. If {@code key} does not exist in
   * the map, then no exception is thrown.
   *
   * @param key The key (and associated value) to delete.
   * @return {@code true} if a deletion took place, {@code false} otherwise. In other words, if
   *     {@link #size()} of the map decreased because of this operation, then {@code true} is
   *     returned.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  @Override
  public abstract boolean delete(K key);

  /* **************************************************************************
   * Section: OrderMap Operations
   ************************************************************************** */

  /**
   * Returns the key that has the least priority, without removing it.
   *
   * @return Key with least priority.
   * @throws NoSuchElementException If called when map is empty.
   */
  public abstract K min();

  /**
   * Returns the key that has the highest priority, without removing it.
   *
   * @return Key with highest priority.
   * @throws NoSuchElementException If called when map is empty.
   */
  public abstract K max();

  /**
   * Mathematically, <em>floor</em> of some {@code x} is defined as the largest value smaller than
   * or equal to {@code x}. For example, {@code floor(5.4) = 5} and {@code floor(12)  = 12}.
   *
   * <p>Similarly, {@code floor(key)} will return the key with the greatest priority that is smaller
   * than or equal to the priority of {@code key}.</p>
   *
   * @param key The key to find the floor of.
   * @return The floor of {@code key} and {@code null} if {@code key} has priority less than the
   *     lowest-priority key in the map.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  public abstract K floor(K key);

  /**
   * Mathematically, <em>ceiling</em> of some {@code x} is defined as the smallest value larger
   * than or equal to {@code x}. For example, {@code ceil(5.4) = 6} and {@code ceil(10)  = 10}.
   *
   * <p>Similarly, {@code ceil(key)} will return the key with the smallest priority that is greater
   * than or equal to the priority of {@code key}.</p>
   *
   * @param key The key to find the ceiling of.
   * @return The ceiling of {@code key} and {@code null} if {@code key} has priority more than the
   *     highest-priority key in the map.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   */
  public abstract K ceil(K key);

  /**
   * Counts the number of keys that have priority strictly less than the priority of {@code key}.
   *
   * @param key The key to find "rank" of.
   * @return The number of keys in the map that have priority less than priority of {@code key}.
   * @throws IllegalArgumentException If {@code key} is {@code null}.
   * @see #select(int rank)
   */
  public abstract int rank(K key);

  /**
   * Selects the key that has rank {@code rank}. The "rank" of a key is the number of keys in the
   * map that have priority strictly less than the priority of said key.
   *
   * @param rank The key with this rank will be returned.
   * @return Key in the map that has rank {@code rank}.
   * @throws IndexOutOfBoundsException If {@code rank} is not in range <code>[0, {@link #size()})
   *                                   </code>.
   * @see #rank(K key)
   */
  public abstract K select(int rank);

  /**
   * Remove the key-value pair that has the minimum most priority in the map.
   *
   * @throws NoSuchElementException If called when map is empty.
   */
  public abstract void deleteMin();

  /**
   * Remove the key-value pair that has the maximum most priority in the map.
   *
   * @throws NoSuchElementException If called when map is empty.
   */
  public abstract void deleteMax();

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   *
   * @return A shallow copy of {@code this} OrderMap.
   */
  @Override
  public OrderMap<K, V> copy() {
    return (OrderMap<K, V>) super.copy();
  }

  /**
   * {@inheritDoc}
   *
   * @return A deepcopy of {@code this} OrderMap.
   * @throws NullPointerException If {@code null} is returned by {@code keyCopyFn}.
   */
  @Override
  public abstract OrderMap<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                          Function<? super V, V> valueCopyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * Iterate over all the keys of the map in increasing order of priority of keys.
   *
   * @return An iterable that iterates over keys.
   */
  @Override
  public abstract Iterable<K> keys();

  /**
   * Iterate over keys in the map that fall in the range defined by the ceiling of {code low} to the
   * floor of {@code high}.
   *
   * @param low  Iteration will begin with {@link #ceil(K key) ceil(low)}.
   * @param high Iteration will go upto (and including) {@link #floor(K key) floor(high)}.
   * @return An iterable that iterates over the keys in the range.
   * @throws IllegalArgumentException If either {@code low} or {@code high} is {@code null}.
   */
  public abstract Iterable<K> keys(K low, K high);

  /**
   * Iterate over all the associated values of the map in no specific order. Two consecutive
   * iterations may or may not yield the values in the same order.
   *
   * @return An iterable that iterates over values.
   */
  @Override
  public abstract Iterable<V> values();

  /**
   * Iterate over all the "entries" in the map in increasing order of priority of keys in the
   * key-value pairs.
   * An <em>entry</em> is defined as a single key-value pair in the map.
   *
   * @return An iterable that iterates over entries, i.e. key-value pairs.
   * @see Entry
   */
  @Override
  public abstract Iterable<Entry<K, V>> entries();

  /**
   * Iterate over "entries" in the map whose keys have priorities that fall in the range defined by
   * the ceiling of {code low} to the floor of {@code high}.
   * An <em>entry</em> is defined as a single key-value pair in the map.
   *
   * @param low  Iteration will begin with {@link #ceil(K key) ceil(low)}.
   * @param high Iteration will go upto (and including) {@link #floor(K key) floor(high)}.
   * @return An iterable that iterates over the entries in the range.
   * @throws IllegalArgumentException If either {@code low} or {@code high} is {@code null}.
   * @see Entry
   */
  public abstract Iterable<Entry<K, V>> entries(K low, K high);

  /* **************************************************************************
   * Section: Protected Helper Methods
   ************************************************************************** */

  /**
   * {@inheritDoc}
   *
   * @throws NullPointerException If {@code keyCopyFn} returns {@code null}.
   */
  @Override
  protected <T extends Map<K, V>> T deepcopyMaker(T mapInst, //
                                                  Function<? super K, K> keyCopyFn, //
                                                  Function<? super V, V> valueCopyFn) {
    keyCopyFn = keyCopyFn.andThen(key -> //
        Objects.requireNonNull(key, "'keyCopyFn' returned null key"));

    return super.deepcopyMaker(mapInst, keyCopyFn, valueCopyFn);
  }
}
