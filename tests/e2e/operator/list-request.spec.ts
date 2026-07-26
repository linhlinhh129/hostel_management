import { test, expect } from '@playwright/test';

test.describe('Feature: Danh sách Yêu cầu', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/requests');
  });

  test('Hiển thị danh sách các yêu cầu', async ({ page }) => {
    const requestTable = page.locator('table');
    await expect(requestTable).toBeVisible();
  });

  test('Lọc danh sách theo trạng thái Pending', async ({ page }) => {
    // Tests for filtering
  });

});
