package com.egroupai.sandbox.sdk;

import java.util.Locale;

/** Transient HTTP retries (429 / 5xx) are limited to GET/HEAD to avoid duplicate side effects on writes. */
public final class HttpRetryPolicy {
  private HttpRetryPolicy() {}

  public static boolean shouldRetryTransientHttpStatus(String method, int status) {
    if (status != 429 && (status < 500 || status > 599)) {
      return false;
    }
    String m = method == null ? "" : method.trim().toUpperCase(Locale.ROOT);
    return "GET".equals(m) || "HEAD".equals(m);
  }
}
