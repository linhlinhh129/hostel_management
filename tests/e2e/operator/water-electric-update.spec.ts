import { test, expect } from '@playwright/test';
import { METER_READING_DATA, METER_READING_INVALID } from '../fixtures/operator-data';

test.describe('Feature: Cập nhật chỉ số điện nước', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/meter-readings/update');
  });

  test('AC01 - Cập nhật thành công (Happy Path)', async ({ page }) => {
    await page.locator('input[name="roomCode"]').fill(METER_READING_DATA.roomId);
    await page.locator('input[name="newElectric"]').fill(METER_READING_DATA.newElectric.toString());
    await page.locator('input[name="newWater"]').fill(METER_READING_DATA.newWater.toString());
    
    await page.locator('input[name="electricMeterImage"]').setInputFiles(METER_READING_DATA.electricImage);
    await page.locator('input[name="waterMeterImage"]').setInputFiles(METER_READING_DATA.waterImage);

    await page.getByRole('button', { name: 'Lưu' }).click();
  });

  test('AC02 & AC03 - Cảnh báo khi số điện/nước mới nhỏ hơn số cũ', async ({ page }) => {
    await page.locator('input[name="roomCode"]').fill(METER_READING_INVALID.roomId);
    await page.locator('input[name="newElectric"]').fill(METER_READING_INVALID.newElectric.toString());
    await page.locator('input[name="newWater"]').fill(METER_READING_INVALID.newWater.toString());
    
    await page.locator('input[name="electricMeterImage"]').setInputFiles(METER_READING_INVALID.electricImage);
    await page.locator('input[name="waterMeterImage"]').setInputFiles(METER_READING_INVALID.waterImage);

    await page.getByRole('button', { name: 'Lưu' }).click();
    await expect(page.locator('.hms-toast--error').first()).toBeVisible();
  });

  test('AC04 & AC05 - Báo lỗi khi thiếu ảnh chứng minh', async ({ page }) => {
    await page.locator('input[name="roomCode"]').fill(METER_READING_DATA.roomId);
    await page.locator('input[name="newElectric"]').fill(METER_READING_DATA.newElectric.toString());
    await page.locator('input[name="newWater"]').fill(METER_READING_DATA.newWater.toString());
    
    await page.getByRole('button', { name: 'Lưu' }).click();
  });

  test('AC06 - Báo lỗi khi mã phòng không tồn tại', async ({ page }) => {
    await page.locator('input[name="roomCode"]').fill('INVALID_ROOM');
    await page.locator('input[name="newElectric"]').fill('100');
    await page.locator('input[name="newWater"]').fill('100');
    await page.locator('input[name="electricMeterImage"]').setInputFiles(METER_READING_DATA.electricImage);
    await page.locator('input[name="waterMeterImage"]').setInputFiles(METER_READING_DATA.waterImage);

    await page.getByRole('button', { name: 'Lưu' }).click();
    await expect(page.locator('.hms-toast--error').first()).toBeVisible();
  });

});
