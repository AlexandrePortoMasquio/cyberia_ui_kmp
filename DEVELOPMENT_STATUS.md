# Cyberia (XCYB) — Development Status

This file tracks what works today, what’s tested, and what’s next. It acts as a living guide for contributors.

## Working Today
- SPA shell with header navigation: Home, Whitepaper, Philosophy, Token, Chat, Roadmap, Legal.
- Markdown content renderer (marked) for Whitepaper/Philosophy/Roadmap/Legal.
- Phantom wallet detection + connect (and dev-only mock via `?mockWallet=1`).
- SOL balance fetch via `@solana/web3.js`.
- Escrow UI controls (open/confirm/refund) as stubs pending real IDL.
- Gradle Wrapper (8.7) + Kotlin/JS dev server task.
- Playwright setup + smoke test scaffold.

## Verified / Tested
- Kotlin/JS compilation succeeds.
- Dev server runs locally (`./gradlew :webApp:jsBrowserDevelopmentRun`) and serves the SPA on `http://localhost:8080`.
- Manual navigation verified for all pages; Chat page wires wallet/balance actions.
- E2E: Playwright config present; smoke test covers connect/balance and escrow status with mock wallet. Run locally:
  - `npm install && npx playwright install && npx playwright test`

## In Progress / Next Up
- Chat UI (mock streaming): model selector, session reset, rate-limit hinting.
- Token (XCYB) page content: mint, supply, allocation, utilities, explorer links; optional XCYB balance display.
- Escrow gating MVP: open/confirm/refund flows with real Anchor IDL + Phantom; signatures + explorer links.
- Security hardening: sanitize rendered markdown (e.g., DOMPurify) before inserting HTML.
- Config consolidation: centralize `SOLANA_RPC`, `AGENTS_API_BASE`, pricing packages; document in README.
- Accessibility/SEO: roles/labels, keyboard nav, meta tags, sitemap/robots (for static deploy).
- E2E expansion: content rendering, chat streaming assertions, paywall logic, error states; device emulation.
- CI pipeline (GitHub Actions): Gradle + Playwright across Chromium/Firefox/WebKit with caching and trace artifacts.

## Inputs Needed
- Anchor program ID (`programId`), XCYB mint (`mintXcyb`), and `timeoutSecs` in `idl/config.json`.
- Anchor IDL populated at `webApp/src/main/resources/idl/cyberia.json`.
- Agents API base URL (`AGENTS_API_BASE`).
- Pricing packages and amounts (XCYB).
- Final whitepaper and token details (markdown or external links).

## Milestones & Acceptance
- M1 — Foundations & Content (DONE):
  - SPA routing, markdown pages, wallet/balance wiring, smoke E2E.
- M2 — Chat (Mock) (NEXT):
  - Streaming UI, model selector, basic rate-limit messaging.
- M3 — Escrow Gating (MVP):
  - Real open/confirm/refund flows via Anchor + Phantom; signatures shown.
- M4 — Production Readiness:
  - Expanded E2E, CI pipeline, SEO/accessibility polish, deploy.

## Conventions
- Language: English only (code, UI, docs).
- Commit messages: Conventional Commits (e.g., `feat:`, `fix:`, `docs:`, `chore:`).
- Branching: feature branches `feature/...` → PRs into `release/v1.0` (or mainline branch per workflow).
