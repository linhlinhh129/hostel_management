# Implementation Plan: WaterElectricUpdate

**Branch**: `WaterElectricUpdate` | **Date**: 2026-07-31 | **Spec**: [spec.md](file:///F:/SU26/New%20folder/hostel_management/my-project/sdd/specs/operator/WaterElectricUpdate/spec.md)

**Input**: Feature specification from `/specs/WaterElectricUpdate/spec.md`

## Summary

Thay đổi tính năng "Cập nhật Điện Nước" thành "Chỉnh sửa Bản ghi Điện Nước Đã Tồn Tại". Hỗ trợ xử lý nghiệp vụ khi chỉ số điện nước mới nhỏ hơn kỳ trước bằng cách cung cấp giao diện khai báo **Thay công tơ mới** hoặc **Công tơ quay vòng (Rollover)**. Từ đó, phần mềm có thể tính toán chính xác số lượng tiêu thụ trong tháng mà không cần tạo 2 hóa đơn hay yêu cầu chủ trọ tự trừ tay.

## Technical Context

**Language/Version**: Java 17, Jakarta EE

**Primary Dependencies**: Servlets, JSP, JDBC, JSTL

**Storage**: SQL Server

**Testing**: N/A

**Target Platform**: Web Browser

**Project Type**: Web Application

**Performance Goals**: < 500ms response time

**Constraints**: SQL transaction for updating meter_readings and calculating consumption

**Scale/Scope**: Operator Module

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Layered Architecture (MVC)**: Yes. Logic tính toán `usage` nằm ở Service.
- **Consistent UI Design**: Yes. Sử dụng UI form chuẩn của template.
- **Safe Database Operations**: Yes. Update dữ liệu dùng Transaction và PreparedStatement.

## Project Structure

### Documentation (this feature)

```text
my-project/sdd/specs/operator/WaterElectricUpdate/
├── plan.md              
├── research.md          
├── data-model.md        
├── quickstart.md        
└── tasks.md             
```

### Source Code (repository root)

```text
src/main/
├── java/com/quanlyphongtro/
│   ├── dao/MeterReadingDAO.java
│   ├── dto/MeterStatusDTO.java
│   ├── service/MeterReadingService.java
│   └── controller/operator/UpdateMeterReadingServlet.java
└── webapp/WEB-INF/views/operator/
    └── meter_readings/
        ├── list.jsp
        └── update.jsp
```

**Structure Decision**: Web application structure.

## Technical Design (Backend)

- **DTO (`MeterStatusDTO.java`):**
  - Chứa thông tin của bản ghi hiện tại và bản ghi kỳ trước. Thêm thuộc tính `electricUsage`, `waterUsage`, `electricStatus`, `electricOldFinal`, `electricNewStart`, `electricMaxLimit` (tương tự cho water).

- **DAO (`MeterReadingDAO.java`):**
  - Cập nhật hàm lưu thay đổi để hỗ trợ lưu thêm `electric_usage`, `electric_status` (tùy chọn schema: có thể lưu trực tiếp vào db hoặc lưu vào 1 file metadata json, hoặc đơn giản nhất là chỉ tính ra `usage` rồi lưu `electric_usage`, không cần lưu history rườm rà nếu không có requirement tra cứu sau này. Để tuân thủ spec, tạm lưu vào DB).
  - Khuyến nghị: Bảng `meter_readings` cần alter table thêm các cột `electric_status`, `electric_old_final`...

- **Service (`MeterReadingService.java`):**
  - Triển khai logic tính toán lượng tiêu thụ dựa trên công thức của Rollover và Replace.
  - Đối với Rollover, ép cứng (hardcode) `maxLimit = 10000` để đảm bảo an toàn từ Backend, bỏ qua mọi thay đổi trái phép từ Frontend.

- **Servlet (`UpdateMeterReadingServlet.java`):**
  - Xử lý **POST**: Nhận tham số status, số chốt cũ, số bắt đầu mới. Giới hạn max limit sẽ bị bỏ qua ở backend.

## Technical Design (Frontend)

- **Form Sửa (`update.jsp`):**
  - Luôn luôn hiển thị khối chọn "Lý do chỉ số bất thường" (Tuân thủ thuần JSP, loại bỏ xử lý ẩn/hiện bằng JS oninput).
  - Sử dụng JS đơn giản để toggle (hiển thị/ẩn) các ô phụ trợ như "Số chốt cũ", "Số bắt đầu mới" chỉ khi người dùng chủ động chọn trạng thái `REPLACED`.
  - Ô Giới hạn cho `ROLLOVER` được gán cứng `10000` và để dạng `readonly`.
