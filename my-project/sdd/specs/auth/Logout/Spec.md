# **Tính năng: Đăng xuất người dùng (Logout)**

**Trạng thái:** Nháp (Draft) **Người viết:** [Tên của bạn] | **Người duyệt:** [Tên Tech Lead/PM] | **Ngày:** 2026-06-10 **Mức độ ưu tiên:** Cao (High)

## **1. Bối cảnh Nghiệp vụ (Business Context)**

Việc đăng xuất an toàn là yêu cầu bắt buộc để bảo vệ tài khoản người dùng, đặc biệt là khi họ sử dụng ứng dụng trên các thiết bị công cộng hoặc thiết bị dùng chung.

Chức năng này liên kết trực tiếp với mục tiêu bảo mật dữ liệu và tuân thủ các tiêu chuẩn an toàn thông tin cơ bản của dự án.a

## **2. Câu chuyện Người dùng (User Stories)**

**Story 1 (Luồng chính/Happy Path):** Là một **người dùng đã đăng nhập**, tôi muốn **đăng xuất khỏi hệ thống** để **không ai khác có thể truy cập vào tài khoản của tôi trên thiết bị này**.

**Story 2 (Trường hợp ngoại lệ/Edge Case):** Là một **người dùng đã đăng nhập**, khi **phiên đăng nhập (session) của tôi trên server đã hết hạn**, tôi muốn **vẫn có thể nhấn đăng xuất và được chuyển hướng về trang đăng nhập một cách mượt mà** mà không bị hiện các thông báo lỗi kỹ thuật.

## **3. Tiêu chí Nghiệm thu (Acceptance Criteria - EARS)**

**KHI** người dùng nhấn nút "Đăng xuất", **HỆ THỐNG SẼ** hiển thị biểu tượng tải (loading) VÀ gọi API đăng xuất.

**KHI** API trả về kết quả thành công (200 OK), **HỆ THỐNG SẼ** xóa toàn bộ trạng thái đăng nhập ở máy khách (localStorage/sessionStorage/cookies) VÀ chuyển hướng người dùng về màn hình Đăng nhập.

**KHI** API trả về lỗi (ví dụ: 401 Unauthorized - token đã hết hạn), **HỆ THỐNG SẼ** vẫn dọn dẹp trạng thái đăng nhập ở máy khách VÀ bắt buộc chuyển hướng về màn hình Đăng nhập.

**TRONG KHI** yêu cầu đăng xuất đang được xử lý, **HỆ THỐNG SẼ** vô hiệu hóa nút đăng xuất để ngăn người dùng bấm nhiều lần.

## **4. Đặc tả API (API Contract)**

**Endpoint:** POST /api/v1/auth/logout

**Headers:** Authorization: Bearer <access\_token> (Hoặc hệ thống tự động đính kèm HttpOnly Cookie nếu dùng session).

**Request Body:** { "refreshToken": "string" } *(Chỉ cần nếu hệ thống dùng JWT và cần lưu blacklist/xóa refresh token trong database).*

**Response 200 (Thành công):** { "success": true, "message": "Đăng xuất thành công" } *(Lưu ý: Backend cần trả về header Set-Cookie: token=; Max-Age=0; HttpOnly nếu dùng cookie để có thể xóa cookie ở client).*

**Response 401 (Không có quyền/Hết hạn):** { "success": false, "error": { "code": "TOKEN\_INVALID", "message": "Phiên đăng nhập đã hết hạn" } }

## **5. Ràng buộc Kỹ thuật (Technical Constraints)**

**Bảo mật:** Backend bắt buộc phải thu hồi (revoke) hoặc đưa Refresh Token vào danh sách đen (blacklist) để chặn việc tạo Access Token mới.

**Trạng thái Client:** Frontend phải dọn dẹp sạch sẽ các dữ liệu nhạy cảm của người dùng đang lưu trong trạng thái toàn cục (Redux, Zustand...) hoặc Local Storage.

**Thời gian phản hồi tối đa:** 300ms (p95) để đảm bảo giao diện (UI) không bị treo khi người dùng muốn thoát.

## **6. Nằm ngoài phạm vi (Out of Scope)**

Tính năng "Đăng xuất khỏi tất cả các thiết bị khác" (Force logout all devices) sẽ không nằm trong phạm vi của tài liệu này mà thuộc về một tính năng riêng trong phần Cài đặt bảo mật (Security Settings).