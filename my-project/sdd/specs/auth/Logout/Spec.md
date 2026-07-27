# Đặc tả Tính năng: Đăng xuất

## Thông tin chung

* **Trạng thái:** Bản nháp
* **Tác giả:** AI Assistant
* **Người duyệt:** Tech Lead
* **Ngày cập nhật:** 2026-07-27
* **Độ ưu tiên:** Cao

---

## 1. Bối cảnh Nghiệp vụ

Chức năng đăng xuất đóng vai trò kết thúc vòng đời của một phiên làm việc, đảm bảo an toàn thông tin cho người dùng khi họ hoàn thành công việc, đặc biệt khi sử dụng máy tính công cộng. Một chức năng đăng xuất hoàn thiện phải đảm bảo hai yếu tố:
1. Xóa sạch mọi dữ liệu phiên (Session) trên máy chủ.
2. Ngăn chặn triệt để việc khôi phục trang đã truy cập bằng nút "Back" của trình duyệt.

---

## 2. Câu chuyện Người dùng (User Stories)

* **Kịch bản chuẩn (Happy Path):** Là một người dùng đang đăng nhập, tôi muốn nhấp vào nút "Đăng xuất" để kết thúc phiên làm việc của mình và được đưa về trang đăng nhập chính.
* **Trường hợp ngoại lệ (Edge Case) 1 - Chống truy cập bằng lịch sử (Nút Back):** Là một người dùng đã đăng xuất, nếu tôi cố tình (hoặc người khác vô tình) nhấn nút "Back" trên trình duyệt, tôi muốn hệ thống không hiển thị lại các trang chứa thông tin nhạy cảm trước đó, mà phải yêu cầu tôi đăng nhập lại.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)

* **KHI** người dùng nhấp vào nút "Đăng xuất", **HỆ THỐNG PHẢI** hủy (invalidate) ngay lập tức phiên làm việc hiện tại (`HttpSession`) trên máy chủ **VÀ** xóa bỏ cookie liên kết nếu có.
* **KHI** quá trình hủy phiên hoàn tất, **HỆ THỐNG PHẢI** điều hướng (Redirect) người dùng về màn hình đăng nhập chính (`/login`).
* **KHI** người dùng truy cập các trang nội bộ yêu cầu xác thực, **HỆ THỐNG PHẢI** gửi kèm các HTTP Headers chống cache (`Cache-Control: no-cache, no-store, must-revalidate`, `Pragma: no-cache`, `Expires: 0`) để đảm bảo trình duyệt không lưu lại bản sao của trang.
* **KHI** người dùng nhấn nút "Back" trên trình duyệt sau khi đăng xuất, **HỆ THỐNG PHẢI** chặn quyền truy cập do không còn phiên làm việc hợp lệ và yêu cầu đăng nhập lại.

---

## 4. Giao tiếp Hệ thống (System Flow)

* **Đường dẫn (Endpoint):** `GET /logout` (hoặc `POST /logout` tùy theo thiết kế, khuyến nghị dùng `GET` để dễ dàng tích hợp vào link menu).
* **Loại dữ liệu:** Không yêu cầu Payload.

### Phản hồi Hệ thống (System Response)

* Server gọi `request.getSession().invalidate()`.
* Server thực hiện Redirect (HTTP 302) đến `/login`.

---

## 5. Ràng buộc & Chính sách

### Ràng buộc Kỹ thuật (Technical Constraints)

* **Hủy phiên:** Sử dụng API chuẩn của Servlet `session.invalidate()`.
* **Chống Cache (Anti-Caching):** Cấu hình tự động tại `Filter` (ví dụ: `AuthFilter` hoặc `SecurityFilter` hiện có). Tất cả các trang an toàn phải được gán các Header sau:
  ```http
  Cache-Control: no-cache, no-store, must-revalidate
  Pragma: no-cache
  Expires: 0
  ```
* **Bảo vệ luồng:** Mọi nỗ lực truy cập vào các URL như `/tenant/*`, `/manager/*`, `/admin/*` sau khi đăng xuất phải bị Filter từ chối và đẩy về `/login`.

### Chính sách Nghiệp vụ (Business Constraints)

* Không lưu lại bất kỳ lịch sử duyệt trang nhạy cảm nào tại phía Client sau khi đăng xuất.

---

## 6. Giả định & Câu hỏi mở

* **Giả định:** Đã tồn tại một `Filter` chặn các request chưa đăng nhập. Ta sẽ bổ sung logic gán HTTP Headers vào `Filter` này.
* **Câu hỏi mở:** Có nên bổ sung cơ chế theo dõi lịch sử hoạt động (Audit log) để ghi nhận hành vi "Logout thành công" của user không?

---

## 7. Nằm ngoài phạm vi (Out of Scope)

* Tính năng tự động đăng xuất khi không hoạt động (Auto Logout on Idle Time) - có thể phát triển ở giai đoạn sau.
* Tính năng "Đăng xuất khỏi tất cả các thiết bị".
