package com.company.aniketkr.algorithms1.collection.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;


/**
 * Extends {@link Queue} abstract class using a <em>lightweight</em> singly linked list. Elements in
 * the queue can be {@code null}.
 *
 * @param <E> The type of element in the queue. {@code null} elements are allowed.
 */
public final class LinkedQueue<E> extends Queue<E> {
  private int length = 0;   // number of elements in queue
  private Node<E> tail = null;  // bach of queue
  private Node<E> head = null;  // front of queue

  /* **************************************************************************
   * Section: Collection Operations
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
   * Takes constant time.
   */
  @Override
  public void clear() {
    head = null;
    tail = null;
    length = 0;
  }

  /* **************************************************************************
   * Section: Queue Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant time.
   */
  @Override
  public void enqueue(E elmt) {
    Node<E> node = new Node<>(elmt);

    if (isEmpty()) {
      tail = node;
      head = node;
    } else {
      tail.next = node;
      tail = node;
    }
    length++;
  }

  /**
   * {@inheritDoc}
   * Operation takes constant time to complete.
   */
  @Override
  public E dequeue() {
    if (isEmpty()) {
      throw new NoSuchElementException("underflow: can't dequeue from empty queue");
    }

    E elmt = head.elmt;
    if (size() == 1) {
      head = null;
      tail = null;
    } else {
      head = head.next;
    }
    length--;

    return elmt;
  }

  /**
   * {@inheritDoc}
   * It is a constant time operation.
   */
  @Override
  public E peek() {
    if (isEmpty()) {
      throw new NoSuchElementException("underflow: can't peek at empty queue");
    }

    return head.elmt;
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * It is a linear time operation. Takes as much time as {@link #deepcopy(Function)}.
   */
  @Override
  public LinkedQueue<E> copy() {
    return deepcopy(Function.identity());
  }

  /**
   * {@inheritDoc}
   * If {@code copyFn} takes '{@code c}' time to make a copy, then the operation takes time
   * proportional to <code>&theta;(cn)</code>, where '{@code n}' is the number of elements in
   * the queue.
   */
  @Override
  public LinkedQueue<E> deepcopy(Function<? super E, E> copyFn) {
    LinkedQueue<E> cp = new LinkedQueue<>();
    deepcopyMaker(copyFn, cp::enqueue);
    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant time and constant extra space.
   */
  @Override
  public Iterator<E> iterator() {
    return new QueueIterator<>(head);
  }

  /* **************************************************************************
   * Section: Helper Classes & Methods
   ************************************************************************** */

  /**
   * Represents a <em>node</em> in a singly linked list.
   * @param <E> The type of element that this "node" will store.
   */
  private static class Node<E> {
    E elmt;
    Node<E> next = null;

    Node(E elmt) {
      this.elmt = elmt;
    }
  }

  /**
   * Iterate over the queue from the head/front of the queue to the tail/back of the queue.
   *
   * @param <E> The type of element to iterate over (and return).
   */
  private static class QueueIterator<E> implements Iterator<E> {
    private Node<E> current;

    private QueueIterator(Node<E> start) {
      current = start;
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      E elmt = current.elmt;
      current = current.next;
      return elmt;
    }
  }
}
