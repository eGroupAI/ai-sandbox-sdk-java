# Integration Guide (Java)

This SDK is designed for low-change, low-touch customer integration.

## Goals
- Stable API surface for v1.
- Explicit timeout and retry controls.
- Streaming chat support (`text/event-stream`).

## Retry safety
- **429 / 5xx** automatic retries apply only to **GET** and **HEAD**. **POST / PUT / PATCH** are not retried on those status codes to avoid duplicate side effects.
- **Network** I/O failures may still be retried for all methods, up to `maxRetries`.
- Retry delay uses **exponential backoff** with a capped wait time.

## Install
`Maven: <dependency><groupId>com.egroupai</groupId><artifactId>ai-sandbox-sdk-java</artifactId><version>1.0.0</version></dependency>`

## First Steps
1. Construct `AiSandboxClient` with `baseUrl` and `apiKey`.
2. Call `createAgent(...)`.
3. Create a chat channel with `createChatChannel(...)` and send the first message with `sendChat(...)` or `sendChatStream(...)`.

## Errors
- On HTTP errors, `ApiException` includes optional `getTraceId()` when the server sends `x-trace-id`.
