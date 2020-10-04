package com.company.aniketkr.algorithms1.map;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents an entry in a map, like {@link Map}. An "entry" is defined as a single key-value
 * pair.
 *
 * @param <K> The type of key.
 * @param <V> The type of value to associate to the key.
 * @author Aniket Kumar
 */
public final class Entry<K, V> {
  private final K key;
  private final V value;

  /**
   * Creates a new immutable key-value pair Entry object. Assumes nothing about the key and the
   * value types - they can also be {@code null}.
   *
   * @param key   The key in the key-value pair.
   * @param value The value to associate with given key.
   */
  public Entry(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /* **************************************************************************
   * Section: Object Methods
   ************************************************************************** */

  @Override
  public int hashCode() {
    int hash = 0;

    // hash of key
    if (key instanceof Object[]) {
      hash += 31 * Arrays.deepHashCode((Object[]) key);
    } else {
      hash += (key != null) ? 31 * key.hashCode() : 0;
    }

    // hash of value
    if (value instanceof Object[]) {
      hash += 31 * Arrays.deepHashCode((Object[]) value);
    } else {
      hash += (value != null) ? 31 * value.hashCode() : 0;
    }

    return hash;
  }

  /**
   * Check if the given Object {@code obj} is equal to this {@code Entry}.
   *
   * <p>The following condition must be satisfied for equality:
   * <ul>
   *   <li>{@code obj} must be an instance of {@code Entry}.</li>
   *   <li>{@code obj}'s key and value must be equal to the this entry's key and value.</li>
   *   <li>In case any of {@code obj}'s key or value is an array, they must be "deeply" equal
   *   to their counterparts in this Entry.</li>
   * </ul></p>
   *
   * @param obj The other Object to compare with.
   * @return {@code true} if {@code obj} satisfies all conditions, {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Entry)) {
      return false;
    }

    Entry<?, ?> entry = (Entry<?, ?>) obj;

    if (!Objects.deepEquals(this.key, entry.key)) {
      return false;
    }
    return Objects.deepEquals(this.value, entry.value);
  }

  /**
   * Returns a string representation of the entry. Primarily for debugging client code.
   *
   * @return A String.
   */
  @Override
  public String toString() {
    String keyStr;
    if (key instanceof Object[]) {
      keyStr = Arrays.deepToString((Object[]) key);
    } else {
      keyStr = Objects.toString(key);
    }

    String valueStr;
    if (value instanceof Object[]) {
      valueStr = Arrays.deepToString((Object[]) value);
    } else {
      valueStr = Objects.toString(value);
    }

    return String.format("%s: %s", keyStr, valueStr);
  }

  /* **************************************************************************
   * Section: Entry Methods
   ************************************************************************** */

  public K key() {
    return key;
  }

  public V value() {
    return value;
  }
}
