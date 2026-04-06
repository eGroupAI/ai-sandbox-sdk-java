package com.egroupai.sandbox.sdk;

import java.util.Optional;

public class ApiException extends RuntimeException {
  private final int status;
  private final String body;
  private final String traceId;

  public ApiException(int status, String body) {
    this(status, body, null);
  }

  public ApiException(int status, String body, String traceId) {
    super(traceId == null || traceId.isBlank()
        ? "HTTP " + status + ": " + body
        : "HTTP " + status + ": " + body + " (trace_id=" + traceId + ")");
    this.status = status;
    this.body = body;
    this.traceId = traceId;
  }

  public int getStatus() { return status; }
  public String getBody() { return body; }
  public Optional<String> getTraceId() { return Optional.ofNullable(traceId).filter(s -> !s.isBlank()); }
}
