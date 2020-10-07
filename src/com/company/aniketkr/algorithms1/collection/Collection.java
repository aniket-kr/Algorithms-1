package com.company.aniketkr.algorithms1.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Collection<E> implements Iterable<E> {

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  public int hashCode() {
    long hash = 0L;
    int power = 0;
    for (E elmt : this) {
      if (elmt instanceof Object[]) {
        hash += Math.pow(31, power++) * Arrays.deepHashCode((Object[]) elmt);
      } else {
        hash += Math.pow(31, power++) * Objects.hashCode(elmt);
      }
    }

    return (int) (hash % Integer.MAX_VALUE);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Collection)) {
      return false;
    }
    Collection<?> collection = (Collection<?>) obj;
    if (this.size() != collection.size()) {
      return false;
    }

    // compare all elements
    Iterator<E> itor = this.iterator();
    for (Object elmt : collection) {
      if (!Objects.deepEquals(elmt, itor.next())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    if (isEmpty()) return "[0]( )";

    StringBuilder sb = new StringBuilder("[").append(size()).append("]( ");
    this.forEach(elmt -> sb.append(elmt).append(", "));
    sb.setLength(sb.length() - 2);
    return sb.append(" )").toString();
  }

  /* **************************************************************************
   * Section: Collection Operations
   ************************************************************************** */

  public abstract int size();

  public abstract boolean isEmpty();

  public abstract void clear();

  public boolean contains(E elmt) {
    for (E collectionElmt : this) {
      if (Objects.deepEquals(elmt, collectionElmt)) {
        return true;
      }
    }
    return false;
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  public abstract Collection<E> copy();

  public abstract Collection<E> deepcopy(Function<? super E, E> copyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  @Override
  public abstract Iterator<E> iterator();

  /* **************************************************************************
   * Section: Protected Helper Methods
   ************************************************************************** */

  protected <C extends Collection<E>> void deepcopyMaker(Function<? super E, E> copyFn, //
                                                         Consumer<E> addElmt) {
    if (copyFn == null) {
      throw new IllegalArgumentException("param 'copyFn' cannot be null");
    }

    for (E elmt : this) {
      addElmt.accept(copyFn.apply(elmt));
    }
  }
}
