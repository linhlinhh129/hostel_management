# Feature: Danh sách chỉ số điện nước các phòng

**Status:** Draft
**Author:** Tú Anh
**Date:** 2026-06-11

---

## User Story

**As a** nhân viên vận hành,

**I want to** xem danh sách chỉ số điện nước của tất cả các phòng,

**so that** tôi có thể theo dõi tình trạng cập nhật chỉ số điện nước trong kỳ hiện tại và thực hiện công tác quản lý, đối soát dữ liệu.

---

## Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách thành công

**WHEN** người dùng truy cập màn hình danh sách chỉ số điện nước

**THE SYSTEM SHALL** hiển thị danh sách các phòng gồm:

* Mã phòng
* Số điện kỳ trước
* Số nước kỳ trước
* Thời gian cập nhật gần nhất
* Trạng thái cập nhật

### AC02 – Trạng thái chưa cập nhật

**WHEN** phòng chưa có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
CHUA_CAP_NHAT
```

### AC03 – Trạng thái đã cập nhật

**WHEN** phòng đã có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
DA_CAP_NHAT
```

### AC04 – Không có dữ liệu

**WHEN** hệ thống không tìm thấy dữ liệu phòng

**THE SYSTEM SHALL** hiển thị danh sách rỗng.

---

## 4. Giao tiếp Hệ thống (System Flow)

### Đường dẫn (Endpoint)
* **Endpoint:** `GET /operator/meter-readings`
* **Loại dữ liệu (Content-Type):** Trả về HTML (JSP)

### Phản hồi Hệ thống (System Response)
* **Thành công (OK):** Forward đến trang giao diện `/WEB-INF/views/operator/meter_readings/list.jsp` chứa danh sách trạng thái điện nước của tất cả các phòng do người dùng vận hành.
* Các dữ liệu render trên JSP bao gồm: `roomCode`, `previousElectricReading`, `previousWaterReading`, `updatedAt`, `status`.
* Áp dụng Filter cơ sở dữ liệu để phân loại phòng "Chưa cập nhật" và "Đã cập nhật".

---

## Out of Scope

* Chỉnh sửa chỉ số điện nước.
* Xóa bản ghi chỉ số điện nước.
* Upload ảnh công tơ.
* Xuất Excel/PDF.
* Tìm kiếm và lọc dữ liệu.
* Chức năng chốt sổ điện nước.
