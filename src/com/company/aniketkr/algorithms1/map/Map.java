package com.company.aniketkr.algorithms1.map;

import java.util.function.Function;

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

  int size();

  boolean isEmpty();

  void clear();

  boolean contains(K key);

  /* **************************************************************************
   * Section: Map Operations
   ************************************************************************** */

  V get(K key);

  V get(K key, V fallback);

  boolean put(K key, V value);

  boolean delete(K key);

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  Map<K, V> copy();

  Map<K, V> deepcopy(Function<? super K, K> keyCopyFn, Function<? super V, V> valueCopyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  Iterable<K> keys();

  Iterable<V> values();

  Iterable<Item<? super K, ? super V>> items();
}
