import { test, expect } from '@playwright/test';
import { REQUEST_DETAIL_DATA } from '../fixtures/operator-data';

test.describe('Feature: Cập nhật Trạng thái Yêu cầu (Update Status)', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto(`operator/requests/detail?id=${REQUEST_DETAIL_DATA.requestId}`);
  });

  test('Thay đổi trạng thái sang Tiếp nhận', async ({ page }) => {
    const btnAccept = page.getByRole('button', { name: /Xác nhận tiếp nhận/i });
    if (await btnAccept.isVisible()) {
      page.on('dialog', dialog => dialog.accept()); // handle confirm dialog
      await btnAccept.click();
    }
  });

});
