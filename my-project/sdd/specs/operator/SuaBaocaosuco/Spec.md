# Feature: Sửa Báo cáo sự cố (Lịch sử)

**Status:** Draft
**Author:** AI Agent
**Reviewer:** [Tên Reviewer]
**Date:** 2026-07-27
**Priority:** High

---

## 1. Business Context

Trong quá trình quản lý và vận hành, nhân viên có thể đã tạo một báo cáo sự cố nhưng điền sai thông tin (ví dụ: sai tiêu đề, mô tả chưa chính xác, hoặc muốn cập nhật lại hình ảnh). 
Tính năng "Sửa Báo cáo sự cố" cho phép nhân viên có thể chỉnh sửa lại thông tin của báo cáo sự cố mà họ đã tạo, giúp cho dữ liệu báo cáo luôn chính xác và đầy đủ trước khi quản lý tiến hành xử lý.

---

## 2. User Stories

### Story 1 (Chỉnh sửa thông tin báo cáo)

**As a** nhân viên vận hành,

**I want to** chỉnh sửa lại tiêu đề, mô tả chi tiết, và vị trí của một báo cáo sự cố tôi đã gửi,

**so that** tôi có thể cập nhật thông tin chính xác nhất cho người quản lý xử lý.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị Form Sửa báo cáo

**WHEN** user truy cập vào trang sửa báo cáo sự cố từ danh sách lịch sử

**THE SYSTEM SHALL** hiển thị form với các dữ liệu đã điền từ trước:
- **Tiêu đề:** (Input text)
- **Cơ sở/Tòa nhà:** (Dropdown)
- **Vị trí:** (Khu vực chung / Phòng)
- **Chi tiết vị trí:** (Input text)
- **Phân loại:** (Dropdown)
- **Mức độ ưu tiên:** (Dropdown)
- **Mô tả chi tiết:** (Textarea)
- **Ảnh đính kèm:** (Hiển thị ảnh cũ, cho phép xóa/thêm ảnh mới tối đa 3 ảnh)

### AC02 – Validate dữ liệu bắt buộc (Độ dài giới hạn)

**WHEN** user nhấn nút cập nhật (Lưu báo cáo)

**AND** các trường thông tin vượt quá độ dài quy định.

**THE SYSTEM SHALL** chặn hành động lưu và hiển thị thông báo lỗi màu đỏ (tương tự như màn hình tạo mới):
- **Tiêu đề**: Tối đa 50 ký tự.
- **Chi tiết vị trí**: Tối đa 50 ký tự.
- **Mô tả chi tiết**: Tối đa 1000 ký tự.

### AC03 – Cập nhật thành công (Happy Path)

**WHEN** user điền đầy đủ dữ liệu hợp lệ và nhấn lưu

**THE SYSTEM SHALL**:
- Cập nhật thông tin vào cơ sở dữ liệu.
- Hiển thị Toast thông báo: "Cập nhật báo cáo sự cố thành công".
- Điều hướng user về màn hình "Lịch sử báo cáo sự cố".

---

## 4. Technical Guidelines

- Quá trình cập nhật (`UPDATE`) dữ liệu phải sử dụng **PreparedStatements**.
- Validation thực hiện ở cả Frontend (HTML5 `maxlength`, JS validation) và Backend (Java Servlet).
- Chỉ cho phép user (nhân viên) sửa các báo cáo do chính họ tạo ra (kiểm tra `sender_id` khớp với `currentUser`).
- Có thể giới hạn chỉ được sửa khi trạng thái của báo cáo đang là `PENDING` (chưa được quản lý tiếp nhận xử lý).
