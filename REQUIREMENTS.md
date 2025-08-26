# Cyberia (XCYB) — Website Requirements

This document defines the product and technical requirements for the Cyberia website, built with Kotlin Multiplatform (JS target). It is a living document and should be updated as the scope evolves.

## 1. Product Overview
- Purpose: Public website for the Cyberia crypto project (token: XCYB), combining:
  - Content: Whitepaper, philosophy/vision, token details (economics, mint, supply), roadmap, and legal disclaimers.
  - App: A web interface to interact with decentralized chatbots (ChatGPT/DeepSeek-like UX) gated by crypto payments via escrow in XCYB (and SOL for fees).
- Audience: Visitors researching Cyberia, and users paying to access decentralized chatbots.

## 2. Goals and Non‑Goals
- Goals:
  - Content site in English: clear navigation for Whitepaper, Philosophy, Token, Roadmap.
  - Chat interface with near real-time streaming responses (typewriter effect) and conversation history in-session.
  - Wallet connection (Phantom) and account display; SOL balance check.
  - Payment gating via escrow using XCYB (SPL token) and Anchor IDL:
    - Purchase chat access (credits/time/session) using escrowed XCYB.
    - Release/settle payment after successful session; refund on timeout/invalid service.
  - Clear error/status messaging throughout.
- Non‑Goals (initial phases):
  - Multi-wallet adapters beyond Phantom.
  - Native mobile apps; complex user accounts with email/password.
  - On-chain moderation, long-term cloud storage of chat logs.

## 3. Information Architecture (IA)
- Top-level navigation:
  - Home (hero + quick links to Whitepaper/Philosophy/Chat)
  - Whitepaper (rendered markdown/pdf)
  - Philosophy (static content page)
  - Token (XCYB): mint address, supply, distribution, utilities, contract links
  - Chat (app): chat UI + payment gating
  - Roadmap
  - Legal (disclaimer, privacy)
- Footer: socials, GitHub, program IDs, contact.

## 4. Chat App — Functional Requirements
4.1 Chat UX
- Input box with send; Shift+Enter for newline.
- Streaming assistant responses; display tokens/latency indicators.
- Message roles: user, assistant, system (system optional/preprompt).
- Model/agent selector (list of decentralized chatbot nodes) — basic dropdown.
- New chat button (resets context); session-local history only for MVP.

4.2 Access Control and Payments
- Access modes (configurable):
  - Free tier (rate-limited, e.g., N messages/day) OR
  - Paid: escrow XCYB to unlock a session/credit package.
- Show current access state (e.g., “Free tier: 3/10 today” or “Credits: 120 left”).

4.3 Escrow (XCYB) for Access
- Inputs: package selection (e.g., N messages or minutes), price in XCYB, nonce.
- Create escrow using Anchor Program + Phantom signing.
- On session completion/threshold reached: settle payment (confirm delivery).
- On failure/timeout: allow refund according to `timeoutSecs`.
- Display transaction signatures; link to explorer (cluster-aware).

4.4 Backend Integration (Agents)
- Configure an API base URL (e.g., gateway to decentralized nodes).
- Endpoints:
  - List models/agents (GET).
  - Chat completion/stream (POST, server-sent events or chunked).
  - Auth: signed message or wallet signature for session identification (optional; see 6.3).
- Provide dev mocks for E2E tests to simulate agent responses.

## 5. Content — Functional Requirements
- Whitepaper: render from markdown/pdf stored in the repo or linked externally.
- Philosophy, Token, Roadmap, Legal: static pages (markdown → HTML or static HTML).
- Token page must show: mint address, supply, distribution/allocation, explorer links, and basic utility description.

## 6. Wallet, Auth, and Config
6.1 Wallet
- Detect Phantom via `window.solana`; connect/disconnect; show public key.
- Show SOL balance (lamports) and optionally XCYB balance (future enhancement).
- Dev-only mock wallet via `?mockWallet=1` for tests and demos.

6.2 Program/IDL
- Anchor Program IDL: `webApp/src/main/resources/idl/cyberia.json` (must be populated).
- Config: `webApp/src/main/resources/idl/config.json` with `programId`, `mintXcyb`, `timeoutSecs`.

6.3 Session/Auth (Optional)
- Option A: Stateless — include wallet public key and ephemeral signed nonce on chat calls.
- Option B: Minimal session — server issues nonce; client signs; token used for subsequent chat requests during the session.

## 7. Input Validation & UX States
- Disable actions until wallet is connected and inputs are valid.
- Validate escrow inputs: price/nonce as non-negative integers; package selection required.
- Show inline validation messages; preserve user input on errors.

## 8. Error Handling & Messaging
- Wallet not detected: disable gated features and show guidance.
- RPC/transaction failures: surface concise messages with retry hints.
- Agent/API errors: show readable error text (e.g., “agent unavailable”) and suggest retry or alternate model.

## 9. Security & Privacy
- All on-chain actions require wallet confirmation; never handle private keys.
- Do not log chat content to third parties by default; provide clear privacy disclaimer if stored.
- Sanitize user-rendered content; prevent XSS.
- Use recent blockhash and correct fee payer; rely on Anchor best practices.

## 10. Non‑Functional Requirements
- Language: English only.
- Performance: responsive UI and smooth streaming; acceptable TTI on dev server.
- Compatibility: Latest Chrome/Firefox/WebKit; responsive layout for mobile.
- Accessibility: basic keyboard navigation and ARIA roles for chat and buttons.
- SEO: metadata for Home/Whitepaper/Token; clean URLs if routing is added.

## 11. Configuration
- `SOLANA_RPC`: override RPC endpoint (Devnet by default during development).
- `AGENTS_API_BASE`: base URL to decentralized chatbot gateway (env or config file).
- Feature flags: FREE_TIER_ENABLED, DEFAULT_MODEL, PRICING_PACKAGES.

## 12. Test Strategy
- Unit: utility functions (formatting, config parsing) when added.
- E2E (Playwright):
  - Content pages render (Home/Whitepaper/Philosophy/Token/Roadmap).
  - Wallet connect mock flow; balance display.
  - Chat flow with mocked agent streaming.
  - Escrow flow with mocked on-chain client (until real IDL is wired).
- Local validator: `solana-test-validator` + `SOLANA_RPC=http://127.0.0.1:8899` for on-chain tests.

## 13. Open Questions
- Final program ID and full Anchor IDL for escrow.
- Exact pricing model (per message, per minute, or credit bundles) and amounts in XCYB.
- Whether XCYB or SOL covers fees beyond program interactions.
- Session persistence (local-only vs. optional server-side storage/login).
- Exact list and discovery of decentralized chatbot nodes.

## 14. Glossary
- XCYB: Cyberia SPL token used for payments.
- Escrow: On-chain mechanism to hold funds during service delivery.
- Lamports: Smallest unit of SOL (1 SOL = 1,000,000,000 lamports).
- Anchor/IDL: Solana framework and program schema for clients.
