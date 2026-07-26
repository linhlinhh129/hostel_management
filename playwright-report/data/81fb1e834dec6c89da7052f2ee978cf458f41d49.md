# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: multi-actor\operator-manager-flow.spec.ts >> Multi-Actor Flow: Quản lý báo sai điện nước & Nhân viên đi sửa >> Manager báo sai -> Operator nhận thông báo và đi cập nhật
- Location: tests\e2e\multi-actor\operator-manager-flow.spec.ts:9:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: locator('text=Báo cáo sai số hóa đơn').first()
Expected: visible
Timeout: 5000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 5000ms
  - waiting for locator('text=Báo cáo sai số hóa đơn').first()

```

```yaml
- complementary:
  - text: IH Innolvia Home Kỹ thuật
  - navigation:
    - link "Dashboard":
      - /url: /hostel-management/operator/dashboard
      - img
      - text: Dashboard
    - text: Tác vụ
    - link "Danh sách điện nước":
      - /url: /hostel-management/operator/meter-readings
      - img
      - text: Danh sách điện nước
    - link "Lịch sử điện nước":
      - /url: /hostel-management/operator/meter-readings/history
      - img
      - text: Lịch sử điện nước
    - link "Danh sách yêu cầu":
      - /url: /hostel-management/operator/requests
      - img
      - text: Danh sách yêu cầu
    - link "Báo cáo sự cố":
      - /url: /hostel-management/operator/incidents/create
      - img
      - text: Báo cáo sự cố
    - link "Lịch sử báo cáo":
      - /url: /hostel-management/operator/incidents/my-reports
      - img
      - text: Lịch sử báo cáo
    - link "Thông báo hệ thống":
      - /url: /hostel-management/operator/notifications
      - img
      - text: Thông báo hệ thống
  - text: Innolvia Home · v1.0
- banner:
  - navigation "breadcrumb":
    - list:
      - listitem
  - img "Phạm Anh Tú (Atus)"
  - text: Phạm Anh Tú (Atus) Nhân viên vận hành
- main:
  - heading "Thông báo hệ thống" [level=1]
  - paragraph: Thông báo từ Admin quản trị hệ thống
  - table:
    - rowgroup:
      - row "Mã Tiêu đề Người gửi Ngày gửi":
        - columnheader "Mã"
        - columnheader "Tiêu đề"
        - columnheader "Người gửi"
        - columnheader "Ngày gửi"
    - rowgroup:
      - row "NTF-ALL-001 Thông báo bảo trì hệ thống Admin 28/06/2026 09:00:00":
        - cell "NTF-ALL-001"
        - cell "Thông báo bảo trì hệ thống"
        - cell "Admin"
        - cell "28/06/2026 09:00:00"
  - text: Tổng thông báo · Trang 1 / 1
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | const SAMPLE_IMAGE_PATH = 'tests/e2e/fixtures/sample-image.jpg';
  4  | 
  5  | test.describe('Multi-Actor Flow: Quản lý báo sai điện nước & Nhân viên đi sửa', () => {
  6  |   // Bỏ qua trạng thái đăng nhập chung, ép 2 trình duyệt phải đăng nhập tay độc lập
  7  |   test.use({ storageState: { cookies: [], origins: [] } });
  8  | 
  9  |   test('Manager báo sai -> Operator nhận thông báo và đi cập nhật', async ({ browser }) => {
  10 |     // Tăng thời gian tối đa cho Test này lên 2 phút (120 giây) vì chúng ta cố tình chạy chậm!
  11 |     test.setTimeout(120000);
  12 | 
  13 |     // 1. Tạo 2 phiên trình duyệt (Browser Context) hoàn toàn cách ly nhau
  14 |     const managerContext = await browser.newContext();
  15 |     const operatorContext = await browser.newContext();
  16 | 
  17 |     const managerPage = await managerContext.newPage();
  18 |     const operatorPage = await operatorContext.newPage();
  19 | 
  20 |     // =============== GIAI ĐOẠN 1: MANAGER (QUẢN LÝ) BÁO CÁO SAI SỐ ===============
  21 |     await managerPage.bringToFront(); // Mở tab Manager lên trước
  22 |     
  23 |     // Manager Đăng nhập
  24 |     await managerPage.goto('login');
  25 |     await managerPage.fill('input[name="username"]', 'mn03112005@gmail.com');
  26 |     await managerPage.fill('input[name="password"]', 'Admin@123');
  27 |     await managerPage.getByRole('button', { name: 'Đăng nhập' }).click();
  28 |     
  29 |     // Đảm bảo đã vào được trang quản lý
  30 |     await managerPage.waitForURL(/.*manager\/.*/);
  31 |     await managerPage.waitForTimeout(2000); // Dừng 2 giây cho bạn đọc Dashboard
  32 | 
  33 |     // Vào danh sách hóa đơn
  34 |     await managerPage.goto('manager/invoices');
  35 |     await managerPage.waitForTimeout(2000); 
  36 | 
  37 |     // Bấm xem chi tiết hóa đơn đầu tiên trong danh sách
  38 |     await managerPage.locator('text=Xem').first().click();
  39 |     await managerPage.waitForTimeout(2000); 
  40 | 
  41 |     // Bấm nút Báo cáo sai số
  42 |     // Chấp nhận hộp thoại Confirm của trình duyệt (Nhấn OK)
  43 |     managerPage.once('dialog', dialog => dialog.accept());
  44 |     await managerPage.getByRole('button', { name: 'Báo cáo sai số' }).click();
  45 |     await managerPage.waitForTimeout(2000); 
  46 | 
  47 |     // Kiểm tra hệ thống báo cáo thành công và gửi thông báo đi
  48 |     await expect(managerPage.locator('.hms-toast--success').first()).toBeVisible();
  49 |     await managerPage.waitForTimeout(2000); 
  50 |     // =============== GIAI ĐOẠN 2: OPERATOR (NHÂN VIÊN) NHẬN THÔNG BÁO VÀ ĐI SỬA ===============
  51 |     await operatorPage.bringToFront(); // Đổi sang tab Operator
  52 |     await operatorPage.waitForTimeout(2000); // Dừng 2 giây để bạn nhận ra đã chuyển Tab
  53 | 
  54 |     // Operator Đăng nhập
  55 |     await operatorPage.goto('login');
  56 |     await operatorPage.fill('input[name="username"]', 'atu02378@gmail.com');
  57 |     await operatorPage.fill('input[name="password"]', 'Admin@123');
  58 |     await operatorPage.getByRole('button', { name: 'Đăng nhập' }).click();
  59 |     
  60 |     await operatorPage.waitForURL(/.*operator\/.*/);
  61 |     await operatorPage.waitForTimeout(2000); 
  62 | 
  63 |     // Operator vào trang Thông báo hệ thống
  64 |     await operatorPage.goto('operator/notifications');
  65 |     await operatorPage.waitForTimeout(2000); 
  66 |     
  67 |     // Xác minh thông báo từ Manager đã nhảy sang màn hình của Operator
> 68 |     await expect(operatorPage.locator('text=Báo cáo sai số hóa đơn').first()).toBeVisible();
     |                                                                               ^ Error: expect(locator).toBeVisible() failed
  69 | 
  70 |     // Operator ngoan ngoãn đi cập nhật lại điện nước
  71 |     await operatorPage.goto('operator/meter-readings/update');
  72 |     await operatorPage.waitForTimeout(2000); 
  73 |     
  74 |     // Điền lại form 
  75 |     await operatorPage.fill('input[name="roomCode"]', 'CG0102'); // Sửa lại mã phòng có thật để không bị lỗi
  76 |     await operatorPage.fill('input[name="newElectric"]', '150');
  77 |     await operatorPage.fill('input[name="newWater"]', '50');
  78 |     await operatorPage.locator('input[name="electricMeterImage"]').setInputFiles(SAMPLE_IMAGE_PATH);
  79 |     await operatorPage.locator('input[name="waterMeterImage"]').setInputFiles(SAMPLE_IMAGE_PATH);
  80 |     
  81 |     await operatorPage.waitForTimeout(2000); 
  82 |     
  83 |     // Bấm lưu số liệu
  84 |     await operatorPage.getByRole('button', { name: 'Lưu' }).click();
  85 | 
  86 |     // Kiểm tra Operator cập nhật thành công 
  87 |     await expect(operatorPage.locator('.hms-toast--success').first()).toBeVisible();
  88 |     await operatorPage.waitForTimeout(3000); 
  89 |     
  90 |   });
  91 | });
  92 | 
```