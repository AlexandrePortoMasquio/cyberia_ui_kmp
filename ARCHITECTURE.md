# Cyberia (XCYB) — Architecture

This document describes how the project is structured and implemented: frameworks, runtime architecture, build tooling, Solana integrations, and testing.

## 1. High‑Level Overview
- App type: Single‑Page Application (SPA) written in Kotlin Multiplatform with a JS target.
- Purpose: Public website for XCYB (content pages) and a Chat interface for decentralized agents, with crypto payments via on‑chain escrow.
- Routing: Lightweight hash‑based router implemented in `Main.kt`.
- Pages: Home, Whitepaper, Philosophy, Token (XCYB), Chat, Roadmap, Legal.

## 2. Modules and Structure
- `webApp` (active): Kotlin/JS app that renders the SPA and integrates with Solana.
- `shared` (scaffold): Multiplatform module reserved for future shared logic.
- Resources:
  - `webApp/src/main/resources/index.html`: HTML shell with header/nav and a `<main id="app">` content root.
  - `webApp/src/main/resources/idl/`: `config.json` and `cyberia.json` (Anchor IDL placeholder).

Project layout (key paths):
- `webApp/src/main/kotlin/Main.kt` — SPA entrypoint, router, and Chat view wiring
- `webApp/src/main/kotlin/js/Phantom.kt` — externs for Phantom provider
- `webApp/src/main/kotlin/solana/Web3.kt` — externs for `@solana/web3.js`
- `webApp/src/main/kotlin/solana/Anchor.kt` — externs for `@coral-xyz/anchor`
- `webApp/src/main/kotlin/solana/Ed25519.kt` — extern for Ed25519 program

## 3. Frameworks and Libraries
- Kotlin 2.0.0 (Multiplatform, JS IR target)
- Gradle Wrapper 8.7 (build and task orchestration)
- Webpack Dev Server (via Kotlin/JS Gradle plugin)
- NPM dependencies:
  - `@solana/web3.js` — RPC access, transactions
  - `@coral-xyz/anchor` — Anchor Program provider/client (via externs)
- Playwright — end‑to‑end browser tests across Chromium/Firefox/WebKit
- Phantom — wallet provider (browser extension) or dev‑only mock

## 4. Runtime Architecture
- Index shell: `index.html` renders header + nav and includes `/webApp.js` compiled by Kotlin/JS.
- Router: In `Main.kt`, a hash‑based router reads `window.location.hash` and renders the matching page into `<main id="app">`.
- Chat page: Renders wallet controls (connect/balance) and escrow buttons (currently stubs). Real escrow instructions will be wired when the Anchor IDL is provided.
- Mock wallet: `index.html` injects a Phantom‑like `window.solana` when `?mockWallet=1` is present, enabling tests and manual trials without Phantom.

## 5. Solana Integration
- Phantom externs (`js/Phantom.kt`): typed access to `window.solana` connect/sign flows.
- Web3 externs (`solana/Web3.kt`): minimal types for `Connection`, `PublicKey`, `Transaction`, and `SystemProgram`.
- Anchor externs (`solana/Anchor.kt`): `anchor.AnchorProvider` and `anchor.Program` placeholders for loading IDL and program.
- Ed25519 extern (`solana/Ed25519.kt`): for composing verification instructions when needed.
- Configuration (`idl/config.json`):
  - `programId`: Cyberia escrow program ID (to be set).
  - `mintXcyb`: SPL mint address for XCYB (to be set).
  - `timeoutSecs`: UI reference for refund messaging (on‑chain rules prevail).
- RPC endpoint: defaults to Devnet; can be overridden with `SOLANA_RPC` env var.

## 6. Build, Run, and Tasks
- Gradle wrapper tasks (module `webApp`):
  - `:webApp:jsBrowserDevelopmentRun` — dev server with live reload
  - `:webApp:compileKotlinJs` — compile only
  - `:webApp:jsBrowserProductionWebpack` — production bundle (for static hosting)
- Node/Yarn tooling is managed by the Kotlin/JS plugin; `kotlin-js-store/yarn.lock` is checked in by the plugin.

## 7. Testing Strategy
- Playwright config (`playwright.config.ts`):
  - Starts the dev server via Gradle (`jsBrowserDevelopmentRun`).
  - Base URL `http://localhost:8080`.
  - Headless by default, traces on first retry.
- Smoke test (`tests/smoke.spec.ts`):
  - Loads `/?mockWallet=1` and verifies connect/balance buttons, address display, basic escrow stub messaging.
- Dev‑only `data-testid` attributes are present in `index.html` for stable selectors.

## 8. Security and Privacy
- Wallet keys never handled by the app; Phantom prompts for signatures.
- Input validation and DOM sanitization where user text is rendered.
- Future chat integration should clearly document any data retention.

## 9. Roadmap (Technical)
- Content pipeline: Markdown rendering for Whitepaper/Philosophy/Roadmap/Legal.
- Chat integration: agent API (streaming), model selector, rate limiting.
- Escrow wiring: load real Anchor IDL, program IDs, build and submit instructions for open/confirm/refund in XCYB.
- XCYB balance and price display helpers.
- Expanded E2E suite (content, chat streaming, escrow gating, error states).

