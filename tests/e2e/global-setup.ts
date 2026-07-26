import { chromium, FullConfig } from '@playwright/test';

async function globalSetup(config: FullConfig) {
  // Thêm tuỳ chọn hiển thị trình duyệt ở bước này để quan sát xem có bị khoá thật không
  const browser = await chromium.launch({ headless: false, slowMo: 500 });
  const page = await browser.newPage();
  
  // Login exactly once
  await page.goto('http://localhost:8080/hostel-management/login');
  await page.locator('input[name="username"]').fill('atu02378@gmail.com');
  await page.locator('input[name="password"]').fill('Admin@123');
  await page.getByRole('button', { name: 'Đăng nhập' }).click();
  
  // Wait for login to complete (adjust the selector based on your actual successful login page)
  await page.waitForURL(/.*operator\/dashboard|.*dashboard|.*home/);
  
  // Save storage state to auth.json
  await page.context().storageState({ path: 'tests/e2e/auth.json' });
  await browser.close();
}

export default globalSetup;
