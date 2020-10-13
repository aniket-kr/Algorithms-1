package com.company.aniketkr.algorithms1.collection.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;


/**
 * Extends the {@link Queue} abstract class using an internal resizing array. The size of the array
 * increases (or decreases) by a factor of {@code 2}.
 *
 * @param <E> The type of element in the queue. Can also be {@code null}.
 */
public final class ArrayQueue<E> extends Queue<E> {
  private static final byte INIT_CAPACITY = 8;

  private int head = 0;  // head/front of queue
  private int tail = 0;  // tail/back of queue
  private E[] queue;  // array that holds the "queue"

  /**
   * Initialise an empty ArrayQueue with default capacity {@value INIT_CAPACITY}.
   */
  public ArrayQueue() {
    this(INIT_CAPACITY);
  }

  /**
   * Initialise an empty ArrayQueue with space for {@code capacity} number of elements.
   *
   * @param capacity The desired number of elements the queue should be able to hold before having
   *                 to resize.
   * @throws IllegalArgumentException If {@code capacity} is less than or equal to {@code 0}.
   */
  @SuppressWarnings("unchecked")
  public ArrayQueue(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("param 'capacity' must be positive");
    }

    queue = (E[]) new Object[capacity];
  }

  /* **************************************************************************
   * Section: Collection Operations
   ************************************************************************** */

  @Override
  public int size() {
    return wrapped() ? (queue.length - head + tail) : (tail - head);
  }

  /**
   * {@inheritDoc}
   * This operation takes constant time.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    super.clear();

    head = 0;
    tail = 0;
    queue = (E[]) new Object[INIT_CAPACITY];
  }

  /* **************************************************************************
   * Section: Queue Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant amortized time on average and linear time in worst case.
   */
  @Override
  public void enqueue(E elmt) {
    super.enqueue(elmt);

    if (size() + 1 == queue.length) {  // +1 to size() maintains a null, which is CRUCIAL
      resize(queue.length * 2);
    }

    queue[tail] = elmt;
    tail = nextIndex(tail);
  }

  /**
   * {@inheritDoc}
   * Takes constant amortized time on average and linear time in worst case.
   */
  @Override
  public E dequeue() {
    super.dequeue();

    final E elmt = queue[head];
    queue[head] = null;
    head = nextIndex(head);

    // resize, if necessary
    if (size() == queue.length / 4) {
      resize(queue.length / 2);
    }

    return elmt;
  }

  /**
   * {@inheritDoc}
   * It is a constant time operation.
   */
  @Override
  public E peek() {
    super.peek();

    return queue[head];
  }

  /* **************************************************************************
   * Section: Duplication Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes linear time, but is significantly faster than {@link #deepcopy(Function)}.
   *
   * @return A shallow copy of this {@code ArrayQueue}.
   */
  @Override
  public ArrayQueue<E> copy() {
    int size = (size() > 2) ? (size() * 2) : INIT_CAPACITY;
    ArrayQueue<E> cp = new ArrayQueue<>(size);
    _copy(cp);
    copyOverElements(cp.queue);
    return cp;
  }

  /**
   * {@inheritDoc}
   * If '{@code copyFn}' takes time '{@code c}' to deepcopy a single element, then the time taken
   * by this operation is proportional to <code>&theta;(cn)</code>. '{@code n}' is the number of
   * elements in the queue.
   *
   * @return A deepcopy of this {@code ArrayQueue}.
   */
  @Override
  public ArrayQueue<E> deepcopy(Function<? super E, E> copyFn) {
    int size = (size() > 2) ? (size() * 2) : INIT_CAPACITY;
    ArrayQueue<E> cp = new ArrayQueue<>(size);
    _deepcopy(cp, copyFn, cp::enqueue);
    return cp;
  }

  /* **************************************************************************
   * Section: Iteration Operations
   ************************************************************************** */

  /**
   * {@inheritDoc}
   * Takes constant time and space to construct.
   */
  @Override
  public Iterator<E> iterator() {
    return new WrappedIterator<>(queue, size(), head);
  }

  /* **************************************************************************
   * Section: Helper Classes and Methods
   ************************************************************************** */

  /**
   * Return the next "wrapped" index. The index next to the last index is the {@code 0}-th index.
   *
   * @param index The index to find next index of.
   * @return The index next to {@code index}.
   */
  private int nextIndex(int index) {
    return (index == queue.length - 1) ? 0 : index + 1;
  }

  /**
   * Check if the index is wrapped.
   *
   * @return {@code true} if queue is wrapped around to the front of the array, {@code false}
   *     otherwise.
   */
  private boolean wrapped() {
    return head > tail;
  }

  /**
   * Resize the {@linkplain #queue internal array} to have capacity to hold {@code newSize} number
   * of elements.
   *
   * @param newSize The new desired capacity of the queue.
   */
  @SuppressWarnings("unchecked")
  private void resize(int newSize) {
    E[] newQueue = (E[]) new Object[newSize];
    copyOverElements(newQueue);
    tail = size();  // `tail` MUST BE computed before `head`
    head = 0;
    queue = newQueue;
  }

  /**
   * Copy over the elements of the {@linkplain #queue internal array} to the given array. It is
   * assumed that {@code array} has capacity at least equal to the {@link #size()} of the queue.
   * Note that the process of copying over <em>unwraps</em> and <em>resets</em> the array.
   *
   * <p>The queue in the copied over element will start from index {@code 0} upto index
   * {@link #size()}.</p>
   *
   * @param array The array to copy elements over to.
   */
  private void copyOverElements(E[] array) {
    if (wrapped()) {
      // unwrap, reset to 0 and copy
      System.arraycopy(queue, head, array, 0, queue.length - head);
      System.arraycopy(queue, 0, array, queue.length - head, tail);
    } else {
      // reset to 0 and copy
      System.arraycopy(queue, head, array, 0, size());
    }
  }

  /**
   * Static nested class that iterates over the elements of a wrapped array queue.
   *
   * @param <E> The type of element in the queue (and to return).
   */
  private static class WrappedIterator<E> implements Iterator<E> {
    private final E[] array;  // the queue array
    private int remaining;  // number of elements remaining to be iterated over
    private int current;  // the index of element to iterate over next

    private WrappedIterator(E[] array, int numOfElements, int startIndex) {
      this.remaining = numOfElements;
      this.array = array;
      this.current = startIndex;
    }

    @Override
    public boolean hasNext() {
      return remaining > 0;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      E elmt = array[current];
      current = (current == array.length - 1) ? 0 : current + 1;  // next "wrapped" index
      remaining--;
      return elmt;
    }
  }
}
