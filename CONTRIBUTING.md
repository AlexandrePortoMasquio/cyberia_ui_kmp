# Contributing to Cyberia (XCYB)

Thank you for your interest in contributing! This document outlines the conventions and workflow for this repository.

## Language Policy
- Use English only across code, UI strings, commit messages, PRs, and docs.
- HTML `lang` is set to `en`; keep it consistent.

## Branching & Commits
- Branches: `feature/<short-scope>` → PR into `release/v1.0` (or main when adopted).
- Commit style: Conventional Commits (e.g., `feat:`, `fix:`, `docs:`, `chore:`, `test:`).

## Development
- Prerequisites: JDK 17+, Node 18+, Phantom (optional).
- Dev server: `./gradlew :webApp:jsBrowserDevelopmentRun` → `http://localhost:8080`.
- Mock wallet: append `?mockWallet=1` to URLs if Phantom is not installed.
- RPC: default Devnet; override `SOLANA_RPC` env var.

## Tests
- Playwright E2E: `npm install && npx playwright install && npx playwright test`.
- Traces on failure; use `PWDEBUG=1` for headed debugging.

## Code Style & Structure
- Kotlin MPP (JS IR). Keep UI logic in `Main.kt` organized by views (router + page renderers).
- Prefer small, pure helpers for DOM updates. Avoid mixing responsibilities.
- Security: sanitize any user-provided or external HTML before insertion into the DOM.

## Security & Privacy
- Never handle private keys; rely on Phantom for all signatures.
- Be cautious with markdown/HTML rendering; consider sanitization (e.g., DOMPurify).
- Document any data retention or logging for future chat integrations.

## Issues & PRs
- Open issues with clear reproduction steps and acceptance criteria.
- For PRs, link to the relevant requirement in `REQUIREMENTS.md` or task in `DEVELOPMENT_STATUS.md`.

