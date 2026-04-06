# AI Sandbox SDK for Java

![Motion headline](https://readme-typing-svg.demolab.com?font=Inter&weight=700&size=24&duration=2800&pause=800&color=F89820&background=FFFFFF00&width=900&lines=Enterprise-Grade+AI+Integration+for+Java;11+APIs+%7C+SSE+Streaming+%7C+GA+v1)

![GA](https://img.shields.io/badge/GA-v1-0A84FF?style=for-the-badge)
![APIs](https://img.shields.io/badge/APIs-11-00A86B?style=for-the-badge)
![Streaming](https://img.shields.io/badge/SSE-Ready-7C3AED?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-SDK-F89820?style=for-the-badge)

## UX-First Value Cards

| Quick Integration | Real-Time Experience | Reliability by Default |
| --- | --- | --- |
| Clean Java API surface with low onboarding overhead | `sendChatStream(...)` for streaming chunks | Timeout + retry controls tuned for production |

## Visual Integration Flow

```mermaid
flowchart LR
  A[Create Agent] --> B[Create Chat Channel]
  B --> C[Send Message]
  C --> D[SSE Stream Chunks]
  D --> E[Attach Knowledge Base]
  E --> F[Customer-Ready Experience]
```

## 60-Second Quick Start

```java
import com.egroupai.sandbox.sdk.AiSandboxClient;
import java.util.List;
import java.util.Map;

AiSandboxClient client = new AiSandboxClient(
  System.getenv().getOrDefault("AI_SANDBOX_BASE_URL", "https://www.egroupai.com"),
  System.getenv().getOrDefault("AI_SANDBOX_API_KEY", "")
);

Map<String, Object> agent = client.createAgent(Map.of(
  "agentDisplayName", "Support Agent",
  "agentDescription", "Handles customer inquiries"
));
int agentId = Integer.parseInt(String.valueOf(((Map<String, Object>) agent.get("payload")).get("agentId")));

Map<String, Object> channel = client.createChatChannel(agentId, Map.of(
  "title", "Web Chat",
  "visitorId", "visitor-001"
));
String channelId = String.valueOf(((Map<String, Object>) channel.get("payload")).get("channelId"));

List<String> chunks = client.sendChatStream(agentId, Map.of(
  "channelId", channelId,
  "message", "What is the return policy?",
  "stream", true
));
chunks.forEach(System.out::println);
```

## Maven

```xml
<dependency>
  <groupId>com.egroupai</groupId>
  <artifactId>ai-sandbox-sdk-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Snapshot

| Metric | Value |
| --- | --- |
| API Coverage | 11 operations (Agent / Chat / Knowledge Base) |
| Stream Mode | `text/event-stream` with `[DONE]` handling |
| Error Surface | `ApiException` with status/body |
| Validation | Production-host integration verified |

## Links

- [Official System Integration Docs](https://www.egroupai.com/ai-sandbox/system-integration)
- [30-Day Optimization Plan](docs/30D_OPTIMIZATION_PLAN.md)
- [Integration Guide](docs/INTEGRATION.md)
- [Quickstart Example](src/main/java/com/egroupai/sandbox/sdk/examples/Quickstart.java)
- [Repository](https://github.com/eGroupAI/ai-sandbox-sdk-java)
