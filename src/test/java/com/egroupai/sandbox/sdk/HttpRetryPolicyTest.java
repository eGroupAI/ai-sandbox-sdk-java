package com.egroupai.sandbox.sdk;

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
}
