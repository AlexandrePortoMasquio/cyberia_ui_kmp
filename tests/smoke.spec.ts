import { test, expect } from '@playwright/test';

test('renders and connects with mock wallet', async ({ page }) => {
  await page.goto('/?mockWallet=1');

  const connect = page.getByTestId('connect');
  await expect(connect).toBeVisible();
  await connect.click();

  await expect(page.getByTestId('address')).not.toHaveText('—');

  const balanceBtn = page.getByTestId('balance');
  await expect(balanceBtn).toBeEnabled();
  await balanceBtn.click();

  // With mock wallet, balance call hits RPC. We only assert UI updated eventually.
  await expect(page.getByTestId('lamports')).not.toHaveText('—');

  // Escrow stubs should enable and show messages
  await page.getByTestId('price').fill('123');
  await page.getByTestId('nonce').fill('1');
  await page.getByTestId('open').click();
  await expect(page.getByTestId('status')).toContainText('Opening escrow (stub)');
});

