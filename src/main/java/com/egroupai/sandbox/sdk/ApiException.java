package com.egroupai.sandbox.sdk;

public class ApiException extends RuntimeException {
  private final int status;
  private final String body;

  public ApiException(int status, String body) {
    super("HTTP " + status + ": " + body);
    this.status = status;
    this.body = body;
  }

  public int getStatus() { return status; }
  public String getBody() { return body; }
}
