package com.egroupai.sandbox.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiSandboxClient {
  private final String baseUrl;
  private final String apiKey;
  private final Duration timeout;
  private final int maxRetries;
  private final HttpClient client;
  private final ObjectMapper mapper;

  public AiSandboxClient(String baseUrl, String apiKey) {
    this(baseUrl, apiKey, Duration.ofSeconds(30), 2);
  }

  public AiSandboxClient(String baseUrl, String apiKey, Duration timeout, int maxRetries) {
    this.baseUrl = baseUrl.replaceAll("/+$", "");
    this.apiKey = apiKey;
    this.timeout = timeout;
    this.maxRetries = maxRetries;
    this.client = HttpClient.newBuilder().connectTimeout(timeout).build();
    this.mapper = new ObjectMapper();
  }

  private HttpResponse<String> sendJson(String method, String path, Map<String, Object> body) throws Exception {
    int attempt = 0;
    while (true) {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/api/v1" + path))
        .timeout(timeout)
        .header("Authorization", "Bearer " + apiKey)
        .header("Accept", "application/json");
      if (body != null) {
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
      } else {
        requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
      }
      HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
      int status = response.statusCode();
      if ((status == 429 || status >= 500) && attempt < maxRetries) {
        attempt += 1;
        Thread.sleep(200L * attempt);
        continue;
      }
      if (status >= 400) throw new ApiException(status, response.body());
      return response;
    }
  }

  public Map<String, Object> createAgent(Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("POST", "/agents", payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> updateAgent(int agentId, Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("PUT", "/agents/" + agentId, payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> listAgents(String query) throws Exception {
    String qs = query == null || query.isBlank() ? "" : "?" + query;
    return mapper.readValue(sendJson("GET", "/agents" + qs, null).body(), new TypeReference<>() {});
  }
  public Map<String, Object> getAgentDetail(int agentId) throws Exception {
    return mapper.readValue(sendJson("GET", "/agents/" + agentId, null).body(), new TypeReference<>() {});
  }
  public Map<String, Object> createChatChannel(int agentId, Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("POST", "/agents/" + agentId + "/channels", payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> sendChat(int agentId, Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("POST", "/agents/" + agentId + "/chat", payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> getChatHistory(int agentId, String channelId, String query) throws Exception {
    String qs = query == null || query.isBlank() ? "limit=50&page=0" : query;
    return mapper.readValue(sendJson("GET", "/agents/" + agentId + "/channels/" + channelId + "/messages?" + qs, null).body(), new TypeReference<>() {});
  }
  public Map<String, Object> getKnowledgeBaseArticles(int agentId, int collectionId, String query) throws Exception {
    String qs = query == null || query.isBlank() ? "startIndex=0" : query;
    return mapper.readValue(sendJson("GET", "/agents/" + agentId + "/collections/" + collectionId + "/articles?" + qs, null).body(), new TypeReference<>() {});
  }
  public Map<String, Object> createKnowledgeBase(int agentId, Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("POST", "/agents/" + agentId + "/collections", payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> updateKnowledgeBaseStatus(int agentCollectionId, Map<String, Object> payload) throws Exception {
    return mapper.readValue(sendJson("PATCH", "/agent-collections/" + agentCollectionId + "/status", payload).body(), new TypeReference<>() {});
  }
  public Map<String, Object> listKnowledgeBases(int agentId, String query) throws Exception {
    String qs = query == null || query.isBlank() ? "activeOnly=false" : query;
    return mapper.readValue(sendJson("GET", "/agents/" + agentId + "/collections?" + qs, null).body(), new TypeReference<>() {});
  }

  public List<String> sendChatStream(int agentId, Map<String, Object> payload) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(baseUrl + "/api/v1/agents/" + agentId + "/chat"))
      .timeout(timeout)
      .header("Authorization", "Bearer " + apiKey)
      .header("Accept", "text/event-stream")
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
      .build();

    HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    if (response.statusCode() >= 400) throw new ApiException(response.statusCode(), "stream request failed");

    List<String> chunks = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.startsWith("data: ")) continue;
        String data = line.substring(6).trim();
        if ("[DONE]".equals(data)) break;
        chunks.add(data);
      }
    }
    return chunks;
  }
}
