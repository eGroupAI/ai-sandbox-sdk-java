package com.egroupai.sandbox.sdk.examples;

import com.egroupai.sandbox.sdk.AiSandboxClient;
import java.util.Map;

public class Quickstart {
  public static void main(String[] args) throws Exception {
    AiSandboxClient client = new AiSandboxClient(
      System.getenv().getOrDefault("AI_SANDBOX_BASE_URL", "https://www.egroupai.com"),
      System.getenv().getOrDefault("AI_SANDBOX_API_KEY", "")
    );
    Map<String, Object> result = client.createAgent(Map.of(
      "agentDisplayName", "Java SDK Quickstart",
      "agentDescription", "Created by Java SDK"
    ));
    System.out.println(result);
  }
}
