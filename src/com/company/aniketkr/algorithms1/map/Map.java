package com.company.aniketkr.algorithms1.map;

import java.util.function.Function;

/**
 * A Map is a data structure that associates "keys" to given "values". Each
 * key-value pair in the map is called an "entry".
 *
 * @param <K> The type of key in the map.
 * @param <V> The type of value that will be associated with keys in the map.
 * @author Aniket Kumar
 */
public interface Map<K, V> {

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  @Override
  String toString();

  /* **************************************************************************
   * Section: Basic Operations
   ************************************************************************** */

  /**
   * Get the count of number of key-value pairs in the map.
   *
   * @return Number of key-value pairs in the map.
   */
  int size();

  /**
   * Check if the map is empty.
   *
   * @return {@code true} if the map has exactly {@code 0} key-value pairs,
   *     {@code false} otherwise.
   */
  boolean isEmpty();

  /**
   * Clears the map of all its key-value pairs.
   * Optionally, it may set the internal state of the map to the default
   * initialisation state.
   */
  void clear();

  /**
   * Check if {@code key} has some value mapped to it in the map.
   *
   * @param key The key.
   * @return {@code true} if {@code key} has mapping in the map, {@code false}
   *     otherwise.
   */
  boolean contains(K key);

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * Get the value associated with {@code key} from the map, provided
   * {@code key} exists in the map.
   *
   * @param key The best form of user interface,
   * @return The associated value.
   * @throws java.util.NoSuchElementException If {@code key} does not exist
   *                                          in the map.
   */
  V get(K key);

  /**
   * Get the value associated with {@code key} from the map, if {@code key}
   * exists in the map. If not, then returns {@code fallback} value.
   *
   * @param key      The key.
   * @param fallback The "value" to return when {@code key} doesn't exist.
   * @return The value associated with {@code key} or {@code fallback} depending
   *     on existence of {@code key} in the map.
   */
  V get(K key, V fallback);

  /**
   * Puts the given key-value pair in the map.
   * If {@code key} happens to already exist in the map, then the associated
   * value is updated to {@code value}.
   *
   * @param key   The key to put or update in the map.
   * @param value The value to associate with {@code key}.
   * @return {@code true} if {@code key} did not exist in the map and a new item
   *     was inserted, {@code false} if only the associated value was updated.
   *     In other words, if {@link #size()} increased then {@code true} is
   *     returned.
   */
  boolean put(K key, V value);

  /**
   * Delete {@code key} and its associated value from the map.
   * If {@code key} does not exist in the map, then no exception is thrown.
   *
   * @param key The key (and associated value) to delete.
   * @return {@code true} if a deletion took place, {@code false} otherwise.
   *     In other words, if {@link #size()} decreased then {@code true} is
   *     returned.
   */
  boolean delete(K key);

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /** Returns a shallow copy of this map. */
  Map<K, V> copy();

  /**
   * Returns a deepcopy of the map.
   *
   * @param keyCopyFn   A pure function that takes the original key as an
   *                    argument and returns a deepcopy of the key.
   * @param valueCopyFn A pure function that takes the original value as an
   *                    argument and returns a deepcopy of the value.
   * @return A deepcopy of this map.
   * @throws IllegalArgumentException If either {@code keyCopyFn} or
   *                                  {@code valueCopyFn} is {@code null}.
   */
  Map<K, V> deepcopy(Function<? super K, K> keyCopyFn, Function<? super V, V> valueCopyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * Iterate over all the keys of the map in no specific order.
   * Two consecutive iterations may or may not yield the keys in the same order.
   *
   * @return An iterable that iterates over keys.
   */
  Iterable<K> keys();

  /**
   * Iterate over all the associated values of the map in no specific order.
   * Two consecutive iterations may or may not yield the values in the same
   * order.
   *
   * @return An iterable that iterates over values.
   */
  Iterable<V> values();

  /**
   * Iterate over all the "entries" of the map in no specific order.
   * An <em>entry</em> is defined as a single key-value pair in the map.
   *
   * @return An iterable that iterates over entries, i.e. key-value pairs.
   * @see Entry
   */
  Iterable<Entry<? super K, ? super V>> entries();
}
