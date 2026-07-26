import { test, expect } from '@playwright/test';

test.describe('Feature: Dashboard Nhân viên vận hành', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/dashboard');
  });

  test('AC01 - Hiển thị Thẻ thống kê (Metric Cards)', async ({ page }) => {
    await page.waitForLoadState('networkidle');
    await expect(page.locator('text=Yêu cầu đang xử lý')).toBeVisible();
    await expect(page.locator('text=Yêu cầu & Sự cố')).toBeVisible();
    await expect(page.locator('text=Tiến độ chốt Điện Nước')).toBeVisible();
  });

  test('AC02 - Hiển thị Lối tắt thao tác (Quick Actions)', async ({ page }) => {
    const btnReportIncident = page.locator('a.quick-action-btn:has-text("Yêu cầu sửa chữa")');
    const btnUpdateMeter = page.locator('a.quick-action-btn:has-text("Chỉ số điện nước")');

    await expect(btnReportIncident).toBeVisible();
    await expect(btnUpdateMeter).toBeVisible();

    await btnReportIncident.click();
    await expect(page).toHaveURL(/.*operator\/requests/);
  });

  test('AC03 - Hiển thị Lịch hẹn sắp tới', async ({ page }) => {
    await expect(page.locator('text=Lịch hẹn sắp tới')).toBeVisible();
  });

  test('AC04 - Xử lý luồng Click vào Thẻ thống kê', async ({ page }) => {
    await page.locator('a:has-text("Chi tiết")').first().click();
    await expect(page).toHaveURL(/.*operator\/requests/);
  });

});
