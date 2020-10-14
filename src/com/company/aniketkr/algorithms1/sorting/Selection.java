package com.company.aniketkr.algorithms1.sorting;

import com.company.aniketkr.algorithms1.Util;
import java.util.Comparator;


/**
 * Sort the given arrays using selection sort.
 *
 * @author Aniket Kumar
 */
public final class Selection {

  /**
   * Sort {@code arr} in-place using natural ordering. Takes time proportional to
   * <code>&theta;(n<sup>2</sup>)</code>.
   *
   * @param arr Array to sort.
   * @param <T> Type of element in array that is self-comparable.
   * @throws IllegalArgumentException If {@code arr} is {@code null}.
   */
  public static <T extends Comparable<T>> void sort(T[] arr) {
    Util.requireNonNull(arr, "arr");  // arr.length may throw

    sort(arr, Comparator.naturalOrder(), 0, arr.length);
  }

  /**
   * Sort {@code arr} in-place using natural ordering starting from index {@code fromIncl} upto,
   * but not including index {@code toExcl}. Takes time proportional to
   * <code>&theta;(n<sup>2</sup>)</code>, where {@code n} is number of elements that fall within
   * range.
   *
   * @param arr      Array to sort.
   * @param fromIncl Start sorting from this index (inclusive).
   * @param toExcl   Stop sorting before this index (exclusive).
   * @param <T>      Type of element in array that is self-comparable.
   * @throws IllegalArgumentException  If {@code arr} is {@code null}.
   * @throws IndexOutOfBoundsException If index {@code fromIncl} is not in range
   *                                   {@code [0, arr.length)} or if index {@code toExcl} in not in
   *                                   range {@code [0, arr.length]}.
   */
  public static <T extends Comparable<T>> void sort(T[] arr, int fromIncl, int toExcl) {
    sort(arr, Comparator.naturalOrder(), fromIncl, toExcl);
  }

  /**
   * Sort {@code arr} in-place using given comparator {@code comp}. Takes time proportional to
   * <code>&theta;(n<sup>2</sup>)</code>.
   *
   * @param arr  Array to sort.
   * @param comp Comparator to use for sorting.
   * @param <T>  Type of element in the array that can become input to {@code comp}.
   * @throws IllegalArgumentException If {@code arr} or {@code comp} is {@code null}.
   */
  public static <T> void sort(T[] arr, Comparator<? super T> comp) {
    Util.requireNonNull(arr, "arr");  // arr.length may throw

    sort(arr, comp, 0, arr.length);
  }

  /**
   * Sort {@code arr} in-place using given comparator {@code comp} starting from index
   * {@code fromIncl} upto, but not including index {@code toExcl}. Takes time proportional to
   * <code>&theta;(n<sup>2</sup>)</code>, where {@code n} is number of elements that fall within
   * range.
   *
   * @param arr      Array to sort.
   * @param comp     Comparator to use for sorting.
   * @param fromIncl Start sorting from this index (inclusive).
   * @param toExcl   Stop sorting before this index (exclusive).
   * @param <T>      Type of element in the array that can become input to {@code comp}.
   * @throws IllegalArgumentException  If {@code arr} or {@code comp} is {@code null}.
   * @throws IndexOutOfBoundsException If index {@code fromIncl} is not in range
   *                                   {@code [0, arr.length)} or if index {@code toExcl} in not in
   *                                   range {@code [0, arr.length]}.
   */
  public static <T> void sort(T[] arr, Comparator<? super T> comp, int fromIncl, int toExcl) {
    SortUtil.allChecks(arr, comp, fromIncl, toExcl);

    int min;
    for (int i = fromIncl; i < toExcl; i++) {
      min = i;
      for (int j = i + 1; j < toExcl; j++) {
        if (comp.compare(arr[min], arr[j]) > 0) {
          min = j;
        }
      }
      SortUtil.swap(arr, i, min);
    }
  }
}