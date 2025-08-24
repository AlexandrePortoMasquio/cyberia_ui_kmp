import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: 'tests',
  timeout: 30_000,
  retries: process.env.CI ? 2 : 0,
  use: {
    baseURL: 'http://localhost:8080',
    trace: 'on-first-retry',
    headless: true,
  },
  webServer: {
    command: process.platform === 'win32'
      ? '.\\gradlew.bat :webApp:browserDevelopmentRun'
      : './gradlew :webApp:browserDevelopmentRun',
    port: 8080,
    reuseExistingServer: !process.env.CI,
    env: {
      // Avoid auto-opening browser in CI; Gradle script reads CI var.
      CI: 'true',
      // Point to local validator if running: uncomment if needed
      // SOLANA_RPC: 'http://127.0.0.1:8899',
    },
  },
});

