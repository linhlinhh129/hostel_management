import { test, expect } from '@playwright/test';

test.describe('Feature: Xem thông báo hệ thống', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/notifications');
  });

  test('Hiển thị danh sách thông báo', async ({ page }) => {
    // Check if the table or empty state is visible
    const tableVisible = await page.locator('.table-mintlify').isVisible();
    const emptyStateVisible = await page.locator('.empty-state').isVisible();
    
    expect(tableVisible || emptyStateVisible).toBeTruthy();
    
    if (tableVisible) {
      await expect(page.locator('th', { hasText: 'Mã' })).toBeVisible();
    }
  });

});
