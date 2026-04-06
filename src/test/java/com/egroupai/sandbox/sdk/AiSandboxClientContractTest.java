package com.egroupai.sandbox.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AiSandboxClientContractTest {
  @Test
  void retriesGetOnTransient5xxThenSucceeds() throws Exception {
    AtomicInteger calls = new AtomicInteger();
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/api/v1/agents", exchange -> {
      int count = calls.incrementAndGet();
      if (count == 1) {
        writeResponse(exchange, 503, "temporary failure", Map.of());
        return;
      }
      writeResponse(exchange, 200, "{\"ok\":true,\"payload\":{\"items\":[]}}", Map.of("Content-Type", "application/json"));
    });
    server.start();

    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      AiSandboxClient client = new AiSandboxClient(baseUrl, "test-key", Duration.ofSeconds(3), 2);

      Map<String, Object> result = client.listAgents("");

      assertEquals(2, calls.get());
      assertTrue(Boolean.TRUE.equals(result.get("ok")));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void doesNotRetryPostOnHttp5xx() throws Exception {
    AtomicInteger calls = new AtomicInteger();
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/api/v1/agents/123/chat", exchange -> {
      calls.incrementAndGet();
      writeResponse(exchange, 503, "write failed", Map.of("x-trace-id", "trace-post-1"));
    });
    server.start();

    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      AiSandboxClient client = new AiSandboxClient(baseUrl, "test-key", Duration.ofSeconds(3), 2);

      ApiException ex = assertThrows(ApiException.class, () ->
          client.sendChat(123, Map.of("channelId", "c-1", "message", "hello")));

      assertEquals(503, ex.getStatus());
      assertEquals("trace-post-1", ex.getTraceId().orElse(null));
      assertEquals(1, calls.get());
    } finally {
      server.stop(0);
    }
  }

  private static void writeResponse(HttpExchange exchange, int status, String body, Map<String, String> headers) throws IOException {
    byte[] raw = body.getBytes(StandardCharsets.UTF_8);
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      exchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
    }
    exchange.sendResponseHeaders(status, raw.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(raw);
    }
  }
}
