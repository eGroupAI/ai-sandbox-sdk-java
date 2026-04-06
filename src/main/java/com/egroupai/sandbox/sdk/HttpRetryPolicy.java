package com.egroupai.sandbox.sdk;

import java.util.Locale;

/** Transient HTTP retries (429 / 5xx) are limited to GET/HEAD to avoid duplicate side effects on writes. */
public final class HttpRetryPolicy {
  private static final long RETRY_BASE_DELAY_MS = 200L;
  private static final long RETRY_MAX_DELAY_MS = 2_000L;

  private HttpRetryPolicy() {}

  public static boolean shouldRetryTransientHttpStatus(String method, int status) {
    if (status != 429 && (status < 500 || status > 599)) {
      return false;
    }
    String m = method == null ? "" : method.trim().toUpperCase(Locale.ROOT);
    return "GET".equals(m) || "HEAD".equals(m);
  }

  public static long retryDelayMillis(int attempt) {
    int safeAttempt = Math.max(1, attempt);
    long delay = RETRY_BASE_DELAY_MS * (1L << (safeAttempt - 1));
    return Math.min(RETRY_MAX_DELAY_MS, delay);
  }
}
