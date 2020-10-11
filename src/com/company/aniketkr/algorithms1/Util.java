package com.company.aniketkr.algorithms1;

import java.util.Arrays;
import java.util.Objects;


/**
 * Houses all sorts of utility methods required all over the library.
 */
public class Util {

  /**
   * Safely convert {@code obj} into its string representation using either
   * {@code Arrays.deepToString}, {@code Arrays.toString} or {@code obj.toString()}. Safe for use
   * with primitive arrays (but <b>NOT</b> primitives).
   * @param obj Object to get string representation of.
   * @return A String.
   */
  public static String stringify(Object obj) {
    if (obj instanceof Object[]) {
      return Arrays.deepToString((Object[]) obj);

    } else if (obj instanceof int[]) {
      return Arrays.toString((int[]) obj);

    } else if (obj instanceof long[]) {
      return Arrays.toString((long[]) obj);

    } else if (obj instanceof float[]) {
      return Arrays.toString((float[]) obj);

    } else if (obj instanceof double[]) {
      return Arrays.toString((double[]) obj);

    } else if (obj instanceof byte[]) {
      return Arrays.toString((byte[]) obj);

    } else if (obj instanceof char[]) {
      return Arrays.toString((char[]) obj);

    } else if (obj instanceof short[]) {
      return Arrays.toString((short[]) obj);
    }

    return Objects.toString(obj);
  }
}
