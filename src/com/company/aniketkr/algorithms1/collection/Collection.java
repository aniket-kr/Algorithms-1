package com.company.aniketkr.algorithms1.collection;

import com.company.aniketkr.algorithms1.Util;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Abstract base class for all data structures that are containers for similar type of elements.
 *
 * @param <E> Type of element in the collection. Unless ordering is explicitly required,
 *            {@code null} elements are allowed.
 */
public abstract class Collection<E> implements Iterable<E> {

  protected int hash;
  protected boolean hashModified = true;

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  public int hashCode() {
    if (!hashModified) {
      return hash;
    }

    long hashCode = 0L;
    int power = 0;
    for (E elmt : this) {
      if (elmt instanceof Object[]) {
        hashCode += Math.pow(31, power++) * Arrays.deepHashCode((Object[]) elmt);
      } else {
        hashCode += Math.pow(31, power++) * Objects.hashCode(elmt);
      }
    }

    hash = (int) (hashCode % Integer.MAX_VALUE);
    hashModified = false;

    return hash;
  }

  /**
   * Compare the given Object {@code obj} with "this" collection. {@code obj} will only be
   * considered equal to this collection is all of the following conditions are satisfied:
   * <ul>
   *   <li>{@code obj} extends the {@link Collection} abstract class.</li>
   *   <li>{@code obj} is the same "type" of collection as "this". (Stack goes with Stack,
   *       Queue with Queue, etc.)</li>
   *   <li>
   *     All the elements in the iteration sequence are equal and in the same place in both the
   *       collections.
   *   </li>
   * </ul>
   *
   * @param obj The object to compare with for equality.
   * @return {@code true} if all above conditions hold, {@code false} otherwise.
   */
  @Override
  public abstract boolean equals(Object obj);

  /**
   * Checks all of the required conditions for {@link #equals(Object)} one-by-one.
   *
   * @param obj      The object to compare with.
   * @param reqClass The class that the obj should be {@code instanceof} to pass.
   * @return {@code true} if all {@link #equals(Object) conditions} hold, false otherwise.
   */
  protected boolean _equals(Object obj, Class<?> reqClass) {
    if (this == obj) {
      return true;
    }

    // check for correct collection type
    if (!reqClass.isInstance(obj)) {
      return false;
    }
    Collection<?> collection = (Collection<?>) obj;

    // compare size()
    if (this.size() != collection.size()) {
      return false;
    }

    // compare (cached) hashes
    if (!this.hashModified && !collection.hashModified) {
      if (this.hash != collection.hash) {
        return false;  // equal collections can't have unequal hashCode() values
      }
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
    if (isEmpty()) {
      return "[0][ ]";
    }

    StringBuilder sb = new StringBuilder("[").append(size()).append("][ ");
    this.forEach(elmt -> sb.append(Util.stringify(elmt)).append(", "));
    sb.setLength(sb.length() - 2);
    return sb.append(" ]").toString();
  }

  /* **************************************************************************
   * Section: Collection Operations
   ************************************************************************** */

  /**
   * Gets the number of elements in the collection.
   *
   * @return The count of elements.
   */
  public abstract int size();

  /**
   * Checks if the collection is empty. A collection is empty when there are no elements in it.
   *
   * @return {@code true} if collection has no elements, {@code false} otherwise.
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Clears the collection of all its elements and set the state of the collection to its
   * <em>default</em> initialisation state.
   */
  public void clear() {
    hashModified = true;
  }

  /**
   * Checks if collection has {@code elmt} as one of its elements.
   *
   * @param elmt The element to check existence of.
   * @return {@code true} if {@code elmt} appears at least once in the collection.
   */
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

  /**
   * Gets a shallow copy of this collection. A shallow copy copies the collection object but uses
   * references for the actual elements in the collection, implying that if an element is mutated in
   * one collection, the element in the copy it also mutated.
   *
   * @return A copy of the collection.
   */
  public abstract Collection<E> copy();

  /**
   * Copy of internal variables from "this" collection to given {@code collection} object.
   *
   * @param collection The other collection to copy over field values to.
   */
  protected void _copy(Collection<E> collection) {
    collection.hash = this.hash;
    collection.hashModified = this.hashModified;
  }

  /**
   * Gets a deepcopy of this collection. A deepcopy copies the collection object as well as its
   * elements over to a new collection. Any change made to one is completed isolated of the other.
   *
   * @param copyFn A pure-function that accepts an element as an argument and returns its deepcopy.
   * @return A deepcopy of the collection.
   * @throws IllegalArgumentException If {@code copyFn} is {@code null}.
   */
  public abstract Collection<E> deepcopy(Function<? super E, E> copyFn);

  protected void _deepcopy(Collection<E> collection, Function<? super E, E> copyFn,//
                           Consumer<E> addElmt) {
    if (copyFn == null) {
      throw new IllegalArgumentException("param 'copyFn' cannot be null");
    }

    for (E elmt : this) {
      addElmt.accept(copyFn.apply(elmt));
    }

    // set hash cache fields
    collection.hash = this.hash;
    collection.hashModified = this.hashModified;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * Iterates over the elements of the collection in natural order. The natural order is best
   * defined by the extending subclasses.
   *
   * @return An iterator that iterates over elements.
   */
  @Override
  public abstract Iterator<E> iterator();
}
