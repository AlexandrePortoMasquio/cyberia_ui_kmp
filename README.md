# Cyberia (XCYB) — Web UI (Kotlin Multiplatform)

Public website and app for Cyberia’s token XCYB. It provides content pages (Whitepaper, Philosophy, Token, Roadmap, Legal) and a Chat interface for decentralized agents, with crypto payments via escrow (to be wired with the real Anchor IDL).

## Features

- Single‑Page Application with hash‑based routing (Home, Whitepaper, Philosophy, Token, Chat, Roadmap, Legal).
- Phantom wallet integration (connect, show address, get SOL balance). Dev‑only mock via `?mockWallet=1`.
- Escrow UI stubs (open/confirm/refund) ready to wire to the Anchor program.
- Playwright end‑to‑end tests and Gradle wrapper for reproducible builds.

## Tech Stack

- Kotlin 2.0.0 (Multiplatform, JS IR target)
- Gradle Wrapper 8.7
- Kotlin/JS + Webpack Dev Server
- NPM: `@solana/web3.js`, `@coral-xyz/anchor`
- Playwright (Chromium/Firefox/WebKit)
- Phantom (wallet) or mock wallet for tests

See ARCHITECTURE.md for detailed internals, REQUIREMENTS.md for product scope, and CONTRIBUTING.md for conventions (English-only policy, workflow).

## Project Structure

- `webApp/`
  - `src/main/resources/index.html` — HTML shell, header/nav, mock wallet injection
  - `src/main/kotlin/Main.kt` — SPA entrypoint, router, Chat wiring
  - `src/main/kotlin/js/Phantom.kt` — Phantom externs
  - `src/main/kotlin/solana/{Web3,Anchor,Ed25519}.kt` — JS externs for Solana/Anchor
  - `src/main/resources/idl/{config.json,cyberia.json}` — program config + IDL placeholder
- `shared/` — multiplatform scaffold (currently empty)
- `playwright.config.ts`, `tests/` — E2E configuration and smoke test
- `REQUIREMENTS.md`, `ARCHITECTURE.md` — docs
 - `DEVELOPMENT_STATUS.md` — what works, tests, and next steps

## Getting Started

Prerequisites: JDK 17+, Node 18+. Phantom optional (for real wallet testing).

Run dev server:

```bash
./gradlew :webApp:jsBrowserDevelopmentRun
```

Open `http://localhost:8080`. Navigate using the header links, e.g. `#/chat`.

Mock wallet: append `?mockWallet=1` if Phantom isn’t installed.

RPC endpoint: defaults to Devnet. Override with an env var before running:

```bash
SOLANA_RPC=http://127.0.0.1:8899 ./gradlew :webApp:jsBrowserDevelopmentRun
```

## Configuration

- `webApp/src/main/resources/idl/config.json`
  - `programId`: Cyberia escrow program ID (required for real transactions)
  - `mintXcyb`: SPL mint address for XCYB
  - `timeoutSecs`: UI reference for refunds
- `SOLANA_RPC`: overrides default Devnet RPC for local validator or custom endpoints

## Testing (E2E)

```bash
npm install
npx playwright install
npx playwright test
```

Playwright auto‑starts the dev server on port 8080 and runs tests headlessly. Use `PWDEBUG=1 npx playwright test` for headed debugging.

## Production Build

```bash
./gradlew :webApp:jsBrowserProductionWebpack
```

Artifacts are emitted under `webApp/build/distributions/` (static files suitable for hosting).
