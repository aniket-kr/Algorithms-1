package com.company.aniketkr.algorithms1.collection.queue;

import com.company.aniketkr.algorithms1.collection.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;


/**
 * A queue is a collection of elements that model a "queue" in real life. The element that goes in
 * first, comes out first; <em>First-In-First-Out</em> (FIFO) order.
 *
 * @param <E> The type of element in the queue.
 */
public abstract class Queue<E> extends Collection<E> {

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  public boolean equals(Object obj) {
    return equalsHelper(obj, Queue.class);
  }

  /* **************************************************************************
   * Section: Collection Operations
   ************************************************************************** */

  /**
   * Gets the number of elements in the queue.
   *
   * @return The count of elements.
   */
  @Override
  public abstract int size();

  /**
   * Checks if the queue is empty. A queue is empty when there are no elements in it.
   *
   * @return {@code true} if queue has no elements, {@code false} otherwise.
   */
  @Override
  public boolean isEmpty() {
    return super.isEmpty();
  }

  /**
   * Clears the queue of all its elements and set the state of the queue to its
   * <em>default</em> initialisation state.
   */
  @Override
  public void clear() {
    super.clear();
  }

  /**
   * Checks if queue has {@code elmt} as one of its elements.
   *
   * @param elmt The element to check existence of.
   * @return {@code true} if {@code elmt} appears at least once in the queue.
   */
  @Override
  public boolean contains(E elmt) {
    return super.contains(elmt);
  }

  /* **************************************************************************
   * Section: Queue Operations
   ************************************************************************** */

  /**
   * Adds {@code elmt} to the end of the queue.
   *
   * @param elmt The element to add.
   */
  public void enqueue(E elmt) {
    hashModified = true;
  }

  /**
   * Removes the first element in the queue.
   *
   * @return The removed element from the front.
   * @throws java.util.NoSuchElementException If queue is empty.
   */
  public E dequeue() {
    if (isEmpty()) {
      throw new NoSuchElementException("underflow: can't dequeue from empty queue");
    }

    hashModified = true;
    return null;  // placeholder null
  }

  /**
   * Returns the first element of the queue, without removing it.
   *
   * @return The element at the front of the queue.
   * @throws java.util.NoSuchElementException If queue is empty.
   */
  public E peek() {
    if (isEmpty()) {
      throw new NoSuchElementException("underflow: can't peek at empty queue");
    }
    return null;  // placeholder null
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /**
   * Gets a shallow copy of this queue. A shallow copy copies the queue object but uses
   * references for the actual elements in the queue, implying that if an element is mutated in
   * one queue, the element in the copy it also mutated.
   *
   * @return A copy of the queue.
   */
  @Override
  public abstract Queue<E> copy();

  /**
   * Gets a deepcopy of this queue. A deepcopy copies the queue object as well as its
   * elements over to a new queue. Any change made to one is completed isolated of the other.
   *
   * @param copyFn A pure-function that accepts an element as an argument and returns its deepcopy.
   * @return A deepcopy of the queue.
   * @throws IllegalArgumentException If {@code copyFn} is {@code null}.
   */
  @Override
  public abstract Queue<E> deepcopy(Function<? super E, E> copyFn);

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * Iterates over the elements of the queue in natural order. The natural order for a queue is
   * <em>First-In-First-Out</em> (FIFO).
   *
   * @return An iterator that iterates over elements.
   */
  @Override
  public abstract Iterator<E> iterator();
}
