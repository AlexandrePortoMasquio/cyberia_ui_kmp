# Cyberia UI KMP — Requirements

This document defines the requirements for the Cyberia web UI built with Kotlin Multiplatform (JS target). It is a living document and should be updated as the backend program, scope, or priorities change.

## 1. Overview
- Purpose: Provide a simple, reliable web interface to interact with the Cyberia escrow program on Solana.
- Scope (UI only): Wallet connection, showing balance, and executing escrow actions (open, confirm, refund) against the on-chain program via Anchor.
- Out of scope (for now): Complex dashboards, advanced analytics, multi-wallet adapters, mobile native apps.

## 2. Goals and Non‑Goals
- Goals:
  - Connect Phantom and display the connected public key.
  - Fetch and display SOL balance (lamports).
  - Execute escrow flows using the Anchor IDL for the Cyberia program:
    - Open escrow (inputs: price in lamports, nonce).
    - Confirm delivery (release funds to seller).
    - Refund on timeout (reclaim funds to buyer after configured timeout).
  - Provide clear status and error messages for each action.
- Non‑Goals:
  - Supporting non-Phantom wallets.
  - Token swaps, bridging, or unrelated on-chain operations.

## 3. Personas & Environments
- Personas:
  - Buyer: opens escrows and may request refund after timeout.
  - Seller: receives funds upon confirmation.
  - Developer/QA: runs the app locally with mock wallet and E2E tests.
- Environments:
  - Local: `solana-test-validator` (preferred for development & tests).
  - Devnet: default public test cluster.
  - Mainnet: future; requires careful program and mint configuration.

## 4. Functional Requirements
4.1 Wallet Connection
- Detect Phantom via `window.solana`.
- If present: allow “Connect wallet”; on success, show address and enable actions.
- If absent: show “Phantom not detected”. Dev-only mock may be enabled via `?mockWallet=1`.

4.2 Balance
- Fetch SOL balance in lamports for the connected address via `@solana/web3.js`.
- Display the numeric lamports value; errors must surface to the user.

4.3 Open Escrow
- Inputs: `price` (lamports, unsigned integer), `nonce` (unsigned integer).
- Build and submit the transaction using Anchor `Program` and Phantom signing.
- On success: show transaction signature and a success message.
- On failure: show error reason and keep inputs intact for retry.

4.4 Confirm Delivery
- Submit confirmation to release funds to the seller.
- Show signature on success; show error details on failure.

4.5 Refund (Timeout)
- Submit refund if the escrow is refundable (program enforces timeout).
- Show signature on success; show error details on failure.

4.6 Input Validation
- Price and nonce must be non-negative integers; empty values disable related actions.
- Buttons enable/disable reflect connection state and input validity.

## 5. State & Accounts (Program Side)
- Anchor Program IDL: stored under `webApp/src/main/resources/idl/cyberia.json`.
- Program ID: set in `webApp/src/main/resources/idl/config.json` (`programId`).
- Token Mint (if used): `mintXcyb` in `config.json`.
- PDAs and account schemas are defined by the real IDL and must be used to construct instructions.

## 6. Configuration
- `SOLANA_RPC`: environment variable to override RPC endpoint (default: Devnet).
- `idl/config.json` fields:
  - `programId`: string (required for real transactions).
  - `mintXcyb`: string (required if the program uses this mint).
  - `timeoutSecs`: number (UI reference for messaging; actual logic enforced on-chain).

## 7. UX / UI Requirements
- Language: English only.
- Page shows:
  - Connect wallet and Get balance buttons.
  - Address and balance readouts.
  - Escrow section with inputs (price, nonce) and action buttons (open, confirm, refund).
- Status area shows last action result (success/tx signature or error message).
- Responsive layout: usable on common mobile viewport widths via browser dev tools.

## 8. Error Handling & Messaging
- Missing Phantom: disable actions and show “Phantom not detected”.
- RPC errors: show a concise message and encourage retry.
- Validation errors: keep user inputs; explain what is invalid.
- Transaction lifecycle: show signature and a short hint to view in explorer (cluster-aware link optional).

## 9. Security Considerations
- All transactions require explicit wallet confirmation (Phantom prompt).
- Do not store secrets locally; no private keys handled in the UI.
- Use recent blockhash and correct fee payer; rely on Anchor where applicable.
- Avoid injecting untrusted content into the DOM; keep status messages sanitized.

## 10. Non‑Functional Requirements
- Compatibility: Latest Chrome, Firefox, and WebKit (via Playwright).
- Performance: First load within typical dev server expectations; actions respond within RPC latency bounds.
- Reliability: Handle offline/timeout gracefully with clear retry guidance.

## 11. Test Strategy
- Manual:
  - Local dev server: `./gradlew :webApp:jsBrowserDevelopmentRun`.
  - Mock wallet: `/?mockWallet=1` (no Phantom required).
- E2E (Playwright):
  - `npm install && npx playwright install && npx playwright test`.
  - Smoke tests cover connect, balance, and escrow stub messaging.
  - Extend to real transactions once IDL and accounts are wired.
- Local validator:
  - `solana-test-validator` + `SOLANA_RPC=http://127.0.0.1:8899` for deterministic testing.

## 12. Open Questions
- Final program ID and IDL for Cyberia escrow.
- Whether escrow uses SOL or SPL tokens (and which mint/accounts).
- Exact instruction parameters and PDA derivations for open/confirm/refund.
- Desired UX around transaction confirmation links and toasts.

## 13. Glossary
- Lamports: Smallest unit of SOL (1 SOL = 1,000,000,000 lamports).
- Anchor: Framework for Solana programs with IDL-driven clients.
- IDL: Interface Definition Language describing program instructions and accounts.

