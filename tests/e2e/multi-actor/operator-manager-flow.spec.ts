import { test, expect } from '@playwright/test';

const SAMPLE_IMAGE_PATH = 'tests/e2e/fixtures/sample-image.jpg';

test.describe('Multi-Actor Flow: Quản lý báo sai điện nước & Nhân viên đi sửa', () => {
  // Bỏ qua trạng thái đăng nhập chung, ép 2 trình duyệt phải đăng nhập tay độc lập
  test.use({ storageState: { cookies: [], origins: [] } });

  test('Manager báo sai -> Operator nhận thông báo và đi cập nhật', async ({ browser }) => {
    // Tăng thời gian tối đa cho Test này lên 2 phút (120 giây) vì chúng ta cố tình chạy chậm!
    test.setTimeout(120000);

    // 1. Tạo 2 phiên trình duyệt (Browser Context) hoàn toàn cách ly nhau
    const managerContext = await browser.newContext();
    const operatorContext = await browser.newContext();

    const managerPage = await managerContext.newPage();
    const operatorPage = await operatorContext.newPage();

    // =============== GIAI ĐOẠN 1: MANAGER (QUẢN LÝ) BÁO CÁO SAI SỐ ===============
    await managerPage.bringToFront(); // Mở tab Manager lên trước
    
    // Manager Đăng nhập
    await managerPage.goto('login');
    await managerPage.fill('input[name="username"]', 'mn03112005@gmail.com');
    await managerPage.fill('input[name="password"]', 'Admin@123');
    await managerPage.getByRole('button', { name: 'Đăng nhập' }).click();
    
    // Đảm bảo đã vào được trang quản lý
    await managerPage.waitForURL(/.*manager\/.*/);
    await managerPage.waitForTimeout(2000); // Dừng 2 giây cho bạn đọc Dashboard

    // Vào danh sách hóa đơn
    await managerPage.goto('manager/invoices');
    await managerPage.waitForTimeout(2000); 

    // Bấm xem chi tiết hóa đơn đầu tiên trong danh sách
    await managerPage.locator('text=Xem').first().click();
    await managerPage.waitForTimeout(2000); 

    // Bấm nút Báo cáo sai số
    // Chấp nhận hộp thoại Confirm của trình duyệt (Nhấn OK)
    managerPage.once('dialog', dialog => dialog.accept());
    await managerPage.getByRole('button', { name: 'Báo cáo sai số' }).click();
    await managerPage.waitForTimeout(2000); 

    // Kiểm tra hệ thống báo cáo thành công và gửi thông báo đi
    await expect(managerPage.locator('.hms-toast--success').first()).toBeVisible();
    await managerPage.waitForTimeout(2000); 
    // =============== GIAI ĐOẠN 2: OPERATOR (NHÂN VIÊN) NHẬN THÔNG BÁO VÀ ĐI SỬA ===============
    await operatorPage.bringToFront(); // Đổi sang tab Operator
    await operatorPage.waitForTimeout(2000); // Dừng 2 giây để bạn nhận ra đã chuyển Tab

    // Operator Đăng nhập
    await operatorPage.goto('login');
    await operatorPage.fill('input[name="username"]', 'atu02378@gmail.com');
    await operatorPage.fill('input[name="password"]', 'Admin@123');
    await operatorPage.getByRole('button', { name: 'Đăng nhập' }).click();
    
    await operatorPage.waitForURL(/.*operator\/.*/);
    await operatorPage.waitForTimeout(2000); 

    // Operator vào trang Thông báo hệ thống
    await operatorPage.goto('operator/notifications');
    await operatorPage.waitForTimeout(2000); 
    
    // Xác minh thông báo từ Manager đã nhảy sang màn hình của Operator
    await expect(operatorPage.locator('text=Báo cáo sai số hóa đơn').first()).toBeVisible();

    // Operator ngoan ngoãn đi cập nhật lại điện nước
    await operatorPage.goto('operator/meter-readings/update');
    await operatorPage.waitForTimeout(2000); 
    
    // Điền lại form 
    await operatorPage.fill('input[name="roomCode"]', 'CG0102'); // Sửa lại mã phòng có thật để không bị lỗi
    await operatorPage.fill('input[name="newElectric"]', '150');
    await operatorPage.fill('input[name="newWater"]', '50');
    await operatorPage.locator('input[name="electricMeterImage"]').setInputFiles(SAMPLE_IMAGE_PATH);
    await operatorPage.locator('input[name="waterMeterImage"]').setInputFiles(SAMPLE_IMAGE_PATH);
    
    await operatorPage.waitForTimeout(2000); 
    
    // Bấm lưu số liệu
    await operatorPage.getByRole('button', { name: 'Lưu' }).click();

    // Kiểm tra Operator cập nhật thành công 
    await expect(operatorPage.locator('.hms-toast--success').first()).toBeVisible();
    await operatorPage.waitForTimeout(3000); 
    
  });
});
