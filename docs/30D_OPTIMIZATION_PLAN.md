# 30-Day Optimization Plan (Java SDK)

## Outcome Target

- Raise Java SDK to enterprise rollout readiness with robust contracts, predictable retries, and stronger quality controls.
- Keep first API success under 10 minutes and first SSE integration under 30 minutes.

## P0 (Day 1-14): Reliability and Contract Hardening

| Workstream | Task | Files | Acceptance |
| --- | --- | --- | --- |
| API Contract Alignment | Reconcile all path/method definitions with backend and docs | `src/main/java/com/egroupai/sandbox/sdk/AiSandboxClient.java`, `openapi/ai-sandbox-v1.yaml`, `docs/INTEGRATION.md` | 11 API operations validated with no mismatch |
| Safe Retry Policy | Limit default retries to idempotent operations and expose write retry strategy explicitly | `AiSandboxClient.java`, `README.md` | No duplicate write operations during retry failure simulation |
| Error Observability | Extend `ApiException` to carry trace information where available | `ApiException.java`, `AiSandboxClient.java`, `docs/INTEGRATION.md` | Error handling docs include trace workflow |
| QA Baseline | Add unit tests for request/response mapping and SSE parsing behavior | `src/main/java/...`, `src/test/java/...` (new), `pom.xml` | Automated tests pass in CI |
| CI/CD Guardrails | Add Maven workflow for lint/test/package checks | `.github/workflows/ci.yml` (new), `pom.xml` | PR checks required before merge |

## P1 (Day 15-30): Developer Experience and Growth

| Workstream | Task | Files | Acceptance |
| --- | --- | --- | --- |
| Example Expansion | Build full-path quickstart (agent -> channel -> SSE -> KB) | `src/main/java/.../examples/Quickstart.java`, `README.md` | Example executes with env vars only |
| Visual Docs Upgrade | Add troubleshooting matrix and production tuning notes | `README.md`, `docs/INTEGRATION.md` | Faster onboarding in partner trial |
| Release Quality | Enforce release checklist and compatibility declaration | `CHANGELOG.md`, `CONTRIBUTING.md` | Every release includes change impact note |
| Security Posture | Add SCA and secret scan checks in workflow | `.github/workflows/ci.yml`, `SECURITY.md` | No unresolved high-severity issue at release gate |

## Language File Checklist

- `README.md`
- `docs/INTEGRATION.md`
- `docs/30D_OPTIMIZATION_PLAN.md`
- `src/main/java/com/egroupai/sandbox/sdk/AiSandboxClient.java`
- `src/main/java/com/egroupai/sandbox/sdk/ApiException.java`
- `src/main/java/com/egroupai/sandbox/sdk/examples/Quickstart.java`
- `openapi/ai-sandbox-v1.yaml`
- `pom.xml`
- `CHANGELOG.md`
- `CONTRIBUTING.md`
- `SECURITY.md`

## Definition of Done (DoD)

- [ ] 11/11 API operations pass production integration validation.
- [ ] SSE flow reads chunk stream and terminates correctly on `[DONE]`.
- [ ] Retry default policy avoids duplicate non-idempotent operations.
- [ ] CI pipeline enforces compile + test + quality checks on every PR.
- [ ] Quickstart runs from clean environment using only required env vars.
