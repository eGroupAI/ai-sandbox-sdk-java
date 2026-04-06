package com.egroupai.sandbox.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HttpRetryPolicyTest {
  @Test
  void retriesTransientOnlyForGetOrHead() {
    assertTrue(HttpRetryPolicy.shouldRetryTransientHttpStatus("GET", 503));
    assertTrue(HttpRetryPolicy.shouldRetryTransientHttpStatus("HEAD", 429));
    assertFalse(HttpRetryPolicy.shouldRetryTransientHttpStatus("POST", 503));
    assertFalse(HttpRetryPolicy.shouldRetryTransientHttpStatus("GET", 404));
  }

  @Test
  void usesExponentialBackoffWithCap() {
    assertEquals(200L, HttpRetryPolicy.retryDelayMillis(1));
    assertEquals(400L, HttpRetryPolicy.retryDelayMillis(2));
    assertEquals(800L, HttpRetryPolicy.retryDelayMillis(3));
    assertEquals(1600L, HttpRetryPolicy.retryDelayMillis(4));
    assertEquals(2000L, HttpRetryPolicy.retryDelayMillis(5));
    assertEquals(2000L, HttpRetryPolicy.retryDelayMillis(9));
  }
}
