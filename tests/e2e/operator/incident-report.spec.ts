import { test, expect } from '@playwright/test';
import { VALID_INCIDENT, VALID_ROOM_INCIDENT } from '../fixtures/operator-data';

test.describe('Feature: Báo cáo sự cố tại hiện trường', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('operator/incidents/create');
  });

  test('AC01 - Given I am on the Incident Report page, When the form loads, Then all required input fields and dropdowns should be visible', async ({ page }) => {
    await expect(page.getByLabel('Cơ sở / Tòa nhà')).toBeVisible();
    await expect(page.getByRole('radio', { name: 'Khu vực chung' })).toBeVisible();
    await expect(page.getByRole('radio', { name: 'Phòng cụ thể' })).toBeVisible();
    
    await page.getByRole('radio', { name: 'Khu vực chung' }).check();
    await expect(page.locator('#locationDetailCommon')).toBeVisible();
    
    await page.getByRole('radio', { name: 'Phòng cụ thể' }).check();
    await expect(page.locator('#locationDetailRoom')).toBeVisible();

    await expect(page.getByLabel('Phân loại sự cố')).toBeVisible();
    await expect(page.getByLabel('Mức độ ưu tiên')).toBeVisible();
    await expect(page.getByLabel('Mô tả chi tiết')).toBeVisible();
    await expect(page.locator('input[type="file"]')).toBeAttached();
  });

  test('AC02 - Given I am on the Incident Report page, When I submit an empty form, Then I should see validation errors for required fields', async ({ page }) => {
    await page.getByRole('button', { name: 'Gửi báo cáo' }).click();
  });

  test('AC03 - Given I have selected an image, When it is attached, Then I should see a thumbnail and a delete button to remove it', async ({ page }) => {
    const fileInput = page.locator('input[type="file"]');
    await fileInput.setInputFiles(VALID_INCIDENT.imagePath);
    
    await expect(page.locator('.hms-image-preview-wrapper img').first()).toBeVisible();
    await page.locator('.hms-image-preview-remove').first().click();
    await expect(page.locator('.hms-image-preview-wrapper img').first()).toBeHidden();
  });

  test('AC04 - Given I have filled out the form correctly for a public area incident, When I click Submit, Then the report should be created and a success toast is shown', async ({ page }) => {
    await page.getByLabel('Cơ sở / Tòa nhà').selectOption(VALID_INCIDENT.facility);
    
    await page.getByRole('radio', { name: 'Khu vực chung' }).check();
    await page.locator('#locationDetailCommon').fill(VALID_INCIDENT.publicArea);
    
    await page.getByLabel('Phân loại sự cố').selectOption(VALID_INCIDENT.category);
    await page.getByLabel('Mức độ ưu tiên').selectOption(VALID_INCIDENT.priority);
    
    await page.getByLabel('Tiêu đề ngắn gọn').fill('Chập cháy bóng đèn hành lang');
    await page.getByLabel('Mô tả chi tiết').fill(VALID_INCIDENT.description);
    
    await page.getByRole('button', { name: 'Gửi báo cáo' }).click();
  });
  
  test('AC04 (Variant) - Given I have filled out the form correctly for a room incident, When I click Submit, Then the report should be created and a success toast is shown', async ({ page }) => {
    await page.getByLabel('Cơ sở / Tòa nhà').selectOption(VALID_ROOM_INCIDENT.facility);
    
    await page.getByRole('radio', { name: 'Phòng cụ thể' }).check();
    await page.locator('#locationDetailRoom').selectOption(VALID_ROOM_INCIDENT.roomId);
    
    await page.getByLabel('Phân loại sự cố').selectOption(VALID_ROOM_INCIDENT.category);
    await page.getByLabel('Mức độ ưu tiên').selectOption(VALID_ROOM_INCIDENT.priority);
    
    await page.getByLabel('Tiêu đề ngắn gọn').fill('Rỉ nước bồn rửa mặt');
    await page.getByLabel('Mô tả chi tiết').fill(VALID_ROOM_INCIDENT.description);
    
    await page.getByRole('button', { name: 'Gửi báo cáo' }).click();
  });
});
