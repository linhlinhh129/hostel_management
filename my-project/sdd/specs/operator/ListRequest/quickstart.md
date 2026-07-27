# Quickstart: Danh sách yêu cầu sửa chữa (Operator)

## Overview
Hướng dẫn kiểm tra chức năng xem danh sách yêu cầu sửa chữa dành cho nhân viên vận hành (Operator).

## Prerequisites
- Server đã chạy tại cổng 8080 (Tomcat).
- Đã đăng nhập bằng tài khoản có vai trò `OPERATOR`.
- Hệ thống đã có ít nhất một vài request trong cơ sở dữ liệu để kiểm thử.

## Validation Scenarios

### 1. Xem danh sách mặc định
- **Action**: Truy cập URL `/operator/requests`.
- **Expected**:
  - Giao diện hiển thị danh sách các yêu cầu được giao cho bạn.
  - Có cột **Thể loại** và **Trạng thái** được hiển thị bằng **Tiếng Việt** (vd: Sự cố điện, Chờ xử lý).
  - Có cột **Số phòng** hiển thị rõ tên/số phòng.

### 2. Lọc danh sách theo Thể loại
- **Action**: Trong thanh công cụ, chọn Thể loại là `Sự cố điện` và ấn Tìm kiếm / Lọc.
- **Expected**:
  - URL thay đổi thành `/operator/requests?category=ELECTRICITY` (hoặc tương tự tuỳ form submit).
  - Danh sách trả về chỉ chứa các yêu cầu có thể loại là Sự cố điện.

### 3. Phân trang
- **Action**: Nếu số lượng yêu cầu > 20, cuộn xuống dưới bảng và ấn trang 2.
- **Expected**: URL thay đổi thành `/operator/requests?page=2` và bảng hiển thị danh sách các yêu cầu tiếp theo.
