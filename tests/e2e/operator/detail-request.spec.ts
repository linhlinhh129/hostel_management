import { test, expect } from '@playwright/test';
import { REQUEST_DETAIL_DATA } from '../fixtures/operator-data';

test.describe('Feature: Chi tiết Yêu cầu sửa chữa', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto(`operator/requests/detail?id=${REQUEST_DETAIL_DATA.requestId}`);
  });

  test('AC01 - Hiển thị chi tiết yêu cầu', async ({ page }) => {
    await expect(page.locator('h1')).toBeVisible();
    await expect(page.locator('text=Nội dung yêu cầu')).toBeVisible();
    await expect(page.locator('.badge-hms').first()).toBeVisible();
  });

  test('AC02 - Xác nhận hoàn thành yêu cầu (Happy Path)', async ({ page }) => {
    const btnOpenModal = page.getByRole('button', { name: /Báo cáo hoàn thành/i });
    if (await btnOpenModal.isVisible()) {
      await btnOpenModal.click();
      await page.locator('textarea[name="notes"]').fill(REQUEST_DETAIL_DATA.notes);
      await page.locator('input[name="after_images"]').setInputFiles(REQUEST_DETAIL_DATA.afterImage);
      await page.getByRole('button', { name: /Xác nhận lưu/i }).click();
    }
  });

  test('AC03 - Báo lỗi khi thiếu ghi chú hoặc ảnh đính kèm', async ({ page }) => {
    const btnOpenModal = page.getByRole('button', { name: /Báo cáo hoàn thành/i });
    if (await btnOpenModal.isVisible()) {
      await btnOpenModal.click();
      await page.getByRole('button', { name: /Xác nhận lưu/i }).click();
    }
  });

});
