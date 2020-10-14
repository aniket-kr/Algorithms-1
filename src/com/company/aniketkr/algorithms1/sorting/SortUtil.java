package com.company.aniketkr.algorithms1.sorting;

import com.company.aniketkr.algorithms1.Util;
import java.util.Comparator;
import java.util.Objects;

/**
 * Utility static methods, useful for package "sorting".
 *
 * @author Aniket Kumar
 */
class SortUtil {

  /**
   * Checks for the following conditions in order:
   * <ol>
   *   <li>{@code arr} should not be {@code null}.</li>
   *   <li>{@code comp} comparator should not be {@code null}.</li>
   *   <li>Index {@code fromIncl} is in range {@code [0, arr.length)}.</li>
   *   <li>Index {@code toExcl} is in range {@code [0, arr.length]}.</li>
   * </ol>
   * This is a wrapper method around other utility methods.
   *
   * @param arr      Array to sort.
   * @param comp     Comparator to use for comparison.
   * @param fromIncl Index to start sorting from (inclusive).
   * @param toExcl   Index to stop sorting at (exclusive).
   * @param <T>      The type of element in the array.
   * @throws IllegalArgumentException  If {@code arr} or {@code comp} is {@code null}.
   * @throws IndexOutOfBoundsException If index {@code fromIncl} is not in range
   *                                   {@code [0, arr.length)} or if index {@code toExcl} is not
   *                                   in range {@code [0, arr.length]}.
   */
  static <T> void allChecks(T[] arr, Comparator<T> comp, int fromIncl, int toExcl) {
    Util.requireNonNull(arr, "arr");
    Util.requireNonNull(comp, "comp (comparator)");
    Objects.checkIndex(fromIncl, arr.length);
    Objects.checkIndex(toExcl - 1, arr.length);  // -1 implements "excluded" range
  }

  /**
   * Swaps the elements at index {@code i} and {@code j} in {@code arr}. <b>Assumes that {@code i}
   * and {@code j} are valid indices in {@code arr}.</b>
   *
   * @param arr Array holding elements.
   * @param i   Index to swap.
   * @param j   Index to swap.
   */
  static void swap(Object[] arr, int i, int j) {
    Object tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }
}
