package com.company.aniketkr.algorithms1.map;

import java.util.Objects;

/**
 * Represents an entry in a map, like {@link Map}. An "entry" is defined as a
 * single key-value pair.
 *
 * @param <K> The type of key.
 * @param <V> The type of value to associate to the key.
 * @author Aniket Kumar
 */
public final class Entry<K, V> {
  private final K key;
  private final V value;

  /**
   * Creates a new immutable key-value pair Entry object.
   * Assumes nothing about the key and the value types - they can also be
   * {@code null}.
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
    int hash = (key != null) ? key.hashCode() : 0;
    hash = 31 * hash + ((value != null) ? value.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Entry)) {
      return false;
    }

    Entry<?, ?> entry = (Entry<?, ?>) obj;

    if (!Objects.equals(this.key, entry.key)) {
      return false;
    }
    return Objects.equals(this.value, entry.value);
  }

  @Override
  public String toString() {
    return String.format("(%s: %s)", key, value);
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
