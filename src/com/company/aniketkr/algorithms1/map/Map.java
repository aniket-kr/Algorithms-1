package com.company.aniketkr.algorithms1.map;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A Map is a data structure that associates "keys" to given "values". Each key-value pair in the
 * map is called an "entry".
 *
 * @param <K> The type of key in the map.
 * @param <V> The type of value that will be associated with keys in the map.
 * @author Aniket Kumar
 */
public abstract class Map<K, V> {

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
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> map = (Map<?, ?>) obj;
    if (map.size() != this.size()) {
      return false;
    }

    // compare all key-value pairs
    try {
      for (Entry<?, ?> entry : map.entries()) {
        if (!Objects.deepEquals(entry.value(), this.get((K) entry.key()))) {
          // at least 1 entry exists with same keys but different values
          return false;
        }
      }
    } catch (ClassCastException | NoSuchElementException | IllegalArgumentException ok) {
      // ClassCastException       -> key types in two maps are not interconvertible
      // NoSuchElementException   -> key from `map` doesn't exist in "this" map
      // IllegalArgumentException -> key is null from a Map, OrderMap throws exception
      return false;
    }

    return true;  // all key-value pairs
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[0]{  }";
    }

    StringBuilder sb = new StringBuilder("[").append(size()).append("]{ ");
    this.entries().forEach(entry -> sb.append(entry).append(", "));
    sb.setLength(sb.length() - 2);
    return sb.append(" }").toString();
  }

  /* **************************************************************************
   * Section: Basic Operations
   ************************************************************************** */

  /**
   * Get the count of number of key-value pairs in the map.
   *
   * @return Number of key-value pairs in the map.
   */
  public abstract int size();

  /**
   * Check if the map is empty.
   *
   * @return {@code true} if the map has exactly {@code 0} key-value pairs, {@code false} otherwise.
   */
  public abstract boolean isEmpty();

  /**
   * Clears the map of all its key-value pairs. Optionally, it may set the internal state of the
   * map to the default initialisation state.
   */
  public abstract void clear();

  /**
   * Check if {@code key} has some value mapped to it in the map.
   *
   * @param key The key.
   * @return {@code true} if {@code key} has mapping in the map, {@code false} otherwise.
   */
  public abstract boolean contains(K key);

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  /**
   * Get the value associated with {@code key} from the map, provided {@code key} exists in the
   * map.
   *
   * @param key The key.
   * @return The associated value.
   * @throws java.util.NoSuchElementException If {@code key} does not exist in the map.
   */
  public abstract V get(K key);

  /**
   * Get the value associated with {@code key} from the map, if {@code key} exists in the map.
   * If not, then returns {@code fallback} value.
   *
   * @param key      The key.
   * @param fallback The "value" to return when {@code key} doesn't exist.
   * @return The value associated with {@code key} or {@code fallback} depending on existence of
   *     {@code key} in the map.
   */
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
   */
  public abstract boolean put(K key, V value);

  /**
   * Delete {@code key} and its associated value from the map. If {@code key} does not exist in the
   * map, then no exception is thrown.
   *
   * @param key The key (and associated value) to delete.
   * @return {@code true} if a deletion took place, {@code false} otherwise. In other words, if
   *     {@link #size()} of the map decreased because of this operation, then {@code true} is
   *     returned.
   */
  public abstract boolean delete(K key);

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /**
   * Returns a shallow copy of this map.
   *
   * @return A shallow copy of {@code this} Map.
   */
  public Map<K, V> copy() {
    return this.deepcopy(Function.identity(), Function.identity());
  }

  /**
   * Returns a deepcopy of the map.
   *
   * @param keyCopyFn   A pure function that takes the original key as an argument and returns a
   *                    deepcopy of the key.
   * @param valueCopyFn A pure function that takes the original value as an argument and returns a
   *                    deepcopy of the value.
   * @return A deepcopy of {@code this} Map.
   * @throws IllegalArgumentException If either {@code keyCopyFn} or {@code valueCopyFn} is
   *                                  {@code null}.
   */
  public abstract Map<K, V> deepcopy(Function<? super K, K> keyCopyFn, //
                                     Function<? super V, V> valueCopyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * Iterate over all the keys of the map in no specific order. Two consecutive iterations may or
   * may not yield the keys in the same order.
   *
   * @return An iterable that iterates over keys.
   */
  public abstract Iterable<K> keys();

  /**
   * Iterate over all the associated values of the map in no specific order. Two consecutive
   * iterations may or may not yield the values in the same order.
   *
   * @return An iterable that iterates over values.
   */
  public abstract Iterable<V> values();

  /**
   * Iterate over all the "entries" of the map in no specific order.
   * An <em>entry</em> is defined as a single key-value pair in the map.
   *
   * @return An iterable that iterates over entries, i.e. key-value pairs.
   * @see Entry
   */
  public abstract Iterable<Entry<K, V>> entries();

  /* **************************************************************************
   * Section: Protected Helper Methods
   ****************************************************************************/

  /**
   * Perform the deepcopy operation by creating and populating an empty map instance with the
   * key-value pairs (or "entries") of {@code this} map.
   *
   * @param mapInst     An empty map instance.
   * @param keyCopyFn   A pure function that returns deepcopy of keys.
   * @param valueCopyFn A pure function that returns deepcopy of values.
   * @param <T>         The type of empty map instance passed. Decides the return value of the
   *                    method.
   *                    Doing it this way, helps avoid casting.
   * @return A deepcopy of this map instance.
   * @throws IllegalArgumentException If either {@code keyCopyFn} or {@code valeCopyFn} is
   *                                  {@code null}.
   */
  protected <T extends Map<K, V>> T deepcopyHelper(T mapInst, //
                                                   Function<? super K, K> keyCopyFn, //
                                                   Function<? super V, V> valueCopyFn) {
    if (keyCopyFn == null) {
      throw new IllegalArgumentException("param 'keyCopyFn' cannot be null");
    }
    if (valueCopyFn == null) {
      throw new IllegalArgumentException("param 'valueCopyFn' cannot be null");
    }

    // populate the new instance
    for (Entry<K, V> entry : this.entries()) {
      mapInst.put(keyCopyFn.apply(entry.key()), valueCopyFn.apply(entry.value()));
    }
    return mapInst;
  }
}
