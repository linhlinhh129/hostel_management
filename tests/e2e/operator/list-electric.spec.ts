import { test, expect } from '@playwright/test';

test.describe('Feature: Xem danh sách phòng cần ghi điện nước', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/meter-readings');
  });

  test('Hiển thị danh sách phòng cần ghi điện nước', async ({ page }) => {
    const meterTable = page.locator('table');
    await expect(meterTable).toBeVisible();
    
    await expect(page.locator('text=Mã phòng').first()).toBeVisible();
    await expect(page.locator('text=Số điện').first()).toBeVisible();
    await expect(page.locator('text=Số nước').first()).toBeVisible();
  });

});
