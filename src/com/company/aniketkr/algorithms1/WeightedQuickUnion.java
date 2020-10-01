package com.company.aniketkr.algorithms1;

import java.util.Arrays;
import java.util.Objects;

/**
 * Creates a pool with {@code n} independent clusters when instantiated.
 * A cluster can be made up of at least {@code 1} slot and at most
 * {@link #maxClusters()} slots. Supports <em>union</em> and <em>find</em>
 * operations in <code>log<sub>2</sub>(n)</code> time.
 */
public final class WeightedQuickUnion {
  private final int[] arr;  // holds the quick-union data structure
  private int clusters;

  /**
   * Instantiate a new instance that can support unions between at most
   * {@code slots} independent clusters. To the methods the clusters MUST
   * be referred to by index in range <code>[0, slots)</code>. Takes time
   * proportional to <code>O(n)</code> to initialize.
   *
   * @param slots Will support at most {@code slots} number of independent
   *              clusters.
   * @throws IllegalArgumentException If {@code slots} is less than or equal
   *                                  to {@code 0}.
   */
  public WeightedQuickUnion(int slots) {
    if (slots <= 0) {
      throw new IllegalArgumentException("parameter 'slots' must be a natural number");
    }

    clusters = slots;
    arr = new int[slots];
    for (int i = 0; i < slots; i++) {
      arr[i] = i;
    }
  }

  /* **************************************************************************
   * Section: Object Operations
   ************************************************************************** */

  @Override
  public String toString() {
    // TODO: improve toString()
    return "WeightedQuickUnion" + Arrays.toString(arr);
  }

  /* **************************************************************************
   * Section: QuickUnion Operations
   ************************************************************************** */

  /**
   * Returns the maximum number of independent clusters that this quick-union
   * can support. Decided by the {@code slots} argument to the constructor.
   */
  public int maxClusters() {
    return arr.length;
  }

  public int clusters() {
    return clusters;
  }

  /**
   * Check if slot {@code a} and slot {@code b} belong to the same cluster.
   *
   * @param a A slot.
   * @param b Another slot.
   * @return {@code true} if both slots {@code a} and {@code b} belong to the
   *     same cluster, {@code false} otherwise.
   * @throws IndexOutOfBoundsException If either {@code a} or {@code b} are not
   *                                   in range <code>
   *                                   [0, {@link #maxClusters()})</code>.
   */
  public boolean isConnected(int a, int b) {
    Objects.checkIndex(a, arr.length);
    Objects.checkIndex(b, arr.length);

    return findRoot(a) == findRoot(b);
  }

  /**
   * Merge the cluster to which slot {@code a} belongs with the cluster to which
   * slot {@code b} belongs.
   *
   * @param a A slot.
   * @param b Another slot to merge with cluster of slot {@code a}.
   * @throws IndexOutOfBoundsException If either {@code a} or {@code b} are not
   *                                   in range <code>
   *                                   [0, {@link #maxClusters()})</code>.
   */
  public void union(int a, int b) {
    Objects.checkIndex(a, arr.length);
    Objects.checkIndex(b, arr.length);

    int root;
    if (depth(a) > depth(b)) {
      root = findRoot(a);
      if (root != arr[b]) {
        arr[b] = root;
        clusters--;
      }

    } else {

      root = findRoot(b);
      if (root != arr[a]) {
        arr[a] = root;
        clusters--;
      }
    }
  }

  /* **************************************************************************
   * Section: Helper Methods and Classes
   ************************************************************************** */

  private int findRoot(int i) {
    while (i != arr[i]) {
      i = arr[i];
    }
    return i;
  }

  private int depth(int i) {
    int count = 1;
    while (i != arr[i]) {
      i = arr[i];
      count++;
    }
    return count;
  }
}
