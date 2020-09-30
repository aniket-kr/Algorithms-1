package com.company.aniketkr.algorithms1.map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EntryTest {

  @ParameterizedTest
  @CsvSource(value = {"12,50.0", "14, 18.0", "24,0.56", "0,-3.0", "Aniket,Kumar", "c,5", "0.5,0.7F"})
  <K, V> void test_HashCodesEqual_when_EntriesEqual(K key, V value) {
    assertEquals(new Entry<>(key, value).hashCode(), new Entry<>(key, value).hashCode());
  }
}
