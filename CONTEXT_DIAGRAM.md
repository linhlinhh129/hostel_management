# HƯỚNG DẪN VẼ CONTEXT DIAGRAM - HOSTEL MANAGEMENT SYSTEM

## 📌 CONTEXT DIAGRAM LÀ GÌ?

**Context Diagram** (Level 0 DFD) là biểu đồ mức cao nhất trong Data Flow Diagram, cho thấy:
- Hệ thống chính (ở giữa)
- Các actor bên ngoài (xung quanh)
- Luồng dữ liệu giữa actors và hệ thống

**MỤC ĐÍCH:** Hiểu được hệ thống tương tác với ai, trao đổi dữ liệu gì.

---

## 🎯 THÀNH PHẦN CỦA DỰ ÁN BẠN

### 1. HỆ THỐNG CHÍNH (Ở GIỮA)
```
┌─────────────────────────────────┐
│   HOSTEL MANAGEMENT SYSTEM      │
│   (Hệ thống Quản lý Nhà trọ)    │
└─────────────────────────────────┘
```

### 2. INTERNAL ACTORS (Người dùng)
- **ADMIN** (Quản trị viên)
- **MANAGER** (Ban quản lý)
- **OPERATOR** (Nhân viên vận hành)
- **TENANT** (Người thuê trọ)

### 3. EXTERNAL SYSTEMS (Hệ thống bên ngoài)
- **VNPay Payment Gateway** (Cổng thanh toán)
- **Email System** (Hệ thống email)
- **Database System** (SQL Server)

---

## 📐 CÁCH VẼ CONTEXT DIAGRAM

### Phương án 1: Vẽ bằng DRAW.IO (Khuyến nghị)

#### Bước 1: Mở draw.io
- Truy cập: https://app.diagrams.net/
- Chọn: **Blank Diagram**

#### Bước 2: Vẽ hệ thống chính
1. Chọn hình **Circle** (hình tròn)
2. Đặt ở giữa màn hình
3. Ghi text: **Hostel Management System**
4. Tô màu: Xanh dương nhạt

#### Bước 3: Vẽ các Actor
**Quy ước ký hiệu:**
- **Internal Actor** (người dùng): Rectangle (hình chữ nhật)
- **External System**: Rectangle bo góc (rounded)

**Vị trí đề xuất:**
```
        ADMIN
         ↕
MANAGER ← SYSTEM → OPERATOR
         ↕
       TENANT
    
   VNPay (góc dưới trái)
   Email System (góc dưới phải)
```


#### Bước 4: Vẽ Data Flow (Luồng dữ liệu)

**Quy tắc:**
- Sử dụng **Arrow** (mũi tên) để chỉ hướng dữ liệu
- Ghi tên luồng dữ liệu trên mũi tên
- Mũi tên 2 chiều nếu có trao đổi qua lại

**Cách vẽ arrow trong draw.io:**
1. Click vào Actor
2. Kéo dot màu xanh → tới System
3. Double click vào arrow → ghi tên luồng dữ liệu

---

## 📊 LUỒNG DỮ LIỆU CHI TIẾT

### A. ADMIN ↔ SYSTEM

**ADMIN → SYSTEM:**
- Thông tin cơ sở mới
- Thông tin nhân sự (Manager/Operator)
- Gán quyền truy cập
- Cấu hình hệ thống

**SYSTEM → ADMIN:**
- Báo cáo doanh thu toàn hệ thống
- Audit logs (nhật ký hệ thống)
- Thống kê KPI
- Danh sách cơ sở/nhân sự

---

### B. MANAGER ↔ SYSTEM

**MANAGER → SYSTEM:**
- Thông tin phòng trọ
- Thông tin khách thuê
- Hợp đồng thuê
- Tạo hóa đơn điện nước
- Duyệt/Từ chối thanh toán
- Gửi thông báo
- Xử lý yêu cầu từ tenant

**SYSTEM → MANAGER:**
- Danh sách phòng/khách thuê
- Danh sách hóa đơn
- Báo cáo công nợ
- Danh sách thanh toán chờ duyệt
- Yêu cầu hỗ trợ từ tenant
- Thống kê doanh thu cơ sở

---

### C. OPERATOR ↔ SYSTEM

**OPERATOR → SYSTEM:**
- Chỉ số điện nước
- Ảnh công tơ điện nước
- Cập nhật trạng thái xử lý yêu cầu
- Hoàn thành công việc

**SYSTEM → OPERATOR:**
- Danh sách phòng cần cập nhật chỉ số
- Yêu cầu sửa chữa/hỗ trợ
- Lịch sử meter readings

---

### D. TENANT ↔ SYSTEM

**TENANT → SYSTEM:**
- Yêu cầu hỗ trợ/sự cố
- Ảnh minh chứng sự cố
- Thông tin thanh toán hóa đơn
- Thông tin người phụ thuộc
- Đổi mật khẩu

**SYSTEM → TENANT:**
- Hóa đơn điện nước chi tiết
- Thông báo từ BQL
- Trạng thái yêu cầu hỗ trợ
- Lịch sử thanh toán
- Thông tin hợp đồng
- Xác nhận thanh toán

---

### E. SYSTEM ↔ VNPAY

**SYSTEM → VNPAY:**
- Payment Request:
  - Invoice ID
  - Amount (VNĐ)
  - Tenant information
  - Return URL
  - Transaction reference

**VNPAY → SYSTEM:**
- Payment Result:
  - Response code (00 = success)
  - Transaction number
  - Amount paid
  - Payment time
  - Secure hash

---

### F. SYSTEM ↔ EMAIL SYSTEM

**SYSTEM → EMAIL SYSTEM:**
- Mật khẩu tạm thời (cho nhân sự mới)
- Link reset mật khẩu
- Thông báo hóa đơn mới
- Thông báo thanh toán thành công
- Thông báo từ BQL

**EMAIL SYSTEM → RECIPIENTS:**
- Email đã được format sẵn

---

### G. SYSTEM ↔ DATABASE

**SYSTEM → DATABASE:**
- INSERT: Users, Rooms, Invoices, Payments, etc.
- UPDATE: Status, thông tin entities
- DELETE: Soft delete (set deleted_at)
- SELECT: Query dữ liệu

**DATABASE → SYSTEM:**
- Dữ liệu entities
- Kết quả query
- Transaction result


---

## 🎨 MẪU CONTEXT DIAGRAM (ASCII)

```
                    ┌─────────────┐
                    │   ADMIN     │
                    └──────┬──────┘
                           │
        Thông tin cơ sở,   │   Báo cáo, Audit logs,
        nhân sự, cấu hình  │   Thống kê KPI
                           ↓
    ┌────────────┐    ┌─────────────────────────────────┐    ┌─────────────┐
    │  MANAGER   │←───→│    HOSTEL MANAGEMENT SYSTEM     │←───→│  OPERATOR   │
    └────────────┘    │   (Hệ thống Quản lý Nhà trọ)    │    └─────────────┘
                      └─────────────────────────────────┘
    Phòng, khách,            ↑              ↓            Chỉ số điện nước,
    hóa đơn, duyệt TT       │              │            Xử lý yêu cầu
                            │              │
                            │              ↓
                    ┌───────┴──────┐   Hóa đơn, thông báo,
                    │    TENANT    │   Trạng thái yêu cầu
                    └──────────────┘
                           │
        Yêu cầu hỗ trợ,    │   
        Thanh toán         │   
                           ↓
                           
    ┌──────────────┐                      ┌────────────────┐
    │    VNPay     │←─────────────────────→│  Email System  │
    │   Gateway    │   Payment request/    │                │
    └──────────────┘   result              └────────────────┘
           ↕                                       ↕
    Thanh toán online                     Gửi email thông báo
```

---

## 📝 TEMPLATE CHO DRAW.IO

### Các thành phần cần vẽ:

#### 1. HỆ THỐNG CHÍNH (Giữa)
- **Hình:** Circle (đường kính ~200px)
- **Text:** "Hostel Management System"
- **Màu nền:** #E3F2FD (xanh dương nhạt)
- **Màu viền:** #1976D2 (xanh dương)
- **Font:** Arial Bold, 14pt

#### 2. ADMIN (Trên cùng)
- **Hình:** Rectangle
- **Text:** "ADMIN"
- **Màu nền:** #FFEBEE (đỏ nhạt)
- **Icon:** 👤 (có thể thêm)

#### 3. MANAGER (Bên trái)
- **Hình:** Rectangle
- **Text:** "MANAGER"
- **Màu nền:** #FFF3E0 (cam nhạt)

#### 4. OPERATOR (Bên phải)
- **Hình:** Rectangle
- **Text:** "OPERATOR"
- **Màu nền:** #E8F5E9 (xanh lá nhạt)

#### 5. TENANT (Dưới cùng)
- **Hình:** Rectangle
- **Text:** "TENANT"
- **Màu nền:** #F3E5F5 (tím nhạt)

#### 6. VNPAY (Góc dưới trái)
- **Hình:** Rounded Rectangle
- **Text:** "VNPay Gateway"
- **Màu nền:** #FFF9C4 (vàng nhạt)
- **Icon:** 💳

#### 7. EMAIL SYSTEM (Góc dưới phải)
- **Hình:** Rounded Rectangle
- **Text:** "Email System"
- **Màu nền:** #CFD8DC (xám xanh)
- **Icon:** ✉️

#### 8. DATA FLOWS (Mũi tên)
- **Style:** Solid arrow với label
- **Màu:** #424242 (xám đậm)
- **Font label:** Arial, 10pt


---

## 🔍 DANH SÁCH LUỒNG DỮ LIỆU ĐẦY ĐỦ (Để ghi trên mũi tên)

### ADMIN → SYSTEM
1. "Thông tin cơ sở"
2. "Thông tin nhân sự"
3. "Gán quyền"
4. "Cấu hình hệ thống"

### SYSTEM → ADMIN
1. "Báo cáo doanh thu"
2. "Audit logs"
3. "Thống kê KPI"
4. "Danh sách cơ sở"

### MANAGER → SYSTEM
1. "Thông tin phòng"
2. "Thông tin khách thuê"
3. "Hợp đồng"
4. "Tạo hóa đơn"
5. "Duyệt thanh toán"

### SYSTEM → MANAGER
1. "Danh sách phòng"
2. "Danh sách hóa đơn"
3. "Công nợ"
4. "Thống kê doanh thu"

### OPERATOR → SYSTEM
1. "Chỉ số điện nước"
2. "Ảnh công tơ"
3. "Xử lý yêu cầu"

### SYSTEM → OPERATOR
1. "Danh sách phòng cần cập nhật"
2. "Yêu cầu hỗ trợ"

### TENANT → SYSTEM
1. "Yêu cầu hỗ trợ"
2. "Thanh toán hóa đơn"
3. "Thông tin người phụ thuộc"

### SYSTEM → TENANT
1. "Hóa đơn chi tiết"
2. "Thông báo"
3. "Trạng thái yêu cầu"
4. "Lịch sử thanh toán"

### SYSTEM → VNPAY
1. "Payment Request" (Invoice ID, Amount, Tenant info)

### VNPAY → SYSTEM
1. "Payment Result" (Success/Failed, Transaction ID)

### SYSTEM → EMAIL
1. "Mật khẩu tạm thời"
2. "Link reset password"
3. "Thông báo hóa đơn"

### EMAIL → USERS
1. "Email đã format"

---

## 💡 GỢI Ý VẼ CHO CHUYÊN NGHIỆP

### 1. Sử dụng màu sắc phân biệt
- **Internal Actors** (ADMIN, MANAGER, OPERATOR, TENANT): Màu pastel nhẹ
- **External Systems** (VNPay, Email): Màu vàng/xám để phân biệt
- **System chính**: Màu xanh dương nổi bật

### 2. Sắp xếp vị trí hợp lý
- **Hierarchy**: ADMIN ở trên (quyền cao nhất)
- **Business flow**: MANAGER - SYSTEM - TENANT (trục dọc)
- **Support**: OPERATOR ở bên (hỗ trợ)
- **External**: VNPay, Email ở góc dưới (tách biệt)

### 3. Gộp nhóm data flows
- Không vẽ từng luồng riêng lẻ (quá rối)
- Gộp thành nhóm chung: VD "Quản lý phòng, khách, hóa đơn"
- Chỉ ghi chi tiết trong tài liệu

### 4. Thêm Legend (Chú thích)
```
┌─────────────────────────────┐
│  LEGEND                     │
├─────────────────────────────┤
│  □  Internal Actor          │
│  ▢  External System         │
│  ○  Main System             │
│  →  Data Flow               │
└─────────────────────────────┘
```


---

## 🛠️ PHƯƠNG ÁN 2: VẼ BẰNG POWERPOINT

### Bước 1: Tạo slide mới
- Chọn layout: Blank

### Bước 2: Insert shapes
- **Insert → Shapes → Oval**: Hệ thống chính (giữa)
- **Insert → Shapes → Rectangle**: Actors
- **Insert → Shapes → Arrow**: Data flows

### Bước 3: Format
- Right-click shape → Format Shape
- Fill: Chọn màu pastel
- Line: Chọn màu viền
- Text: Bold, căn giữa

### Bước 4: Arrange
- Sử dụng **Align** tools để căn đều
- Sử dụng **Distribute** để khoảng cách đều

---

## 📱 PHƯƠNG ÁN 3: VẼ BẰNG LUCIDCHART

Lucidchart có sẵn template cho Context Diagram:
1. Vào https://lucidchart.com
2. Chọn template: **Data Flow Diagram**
3. Chọn level: **Context Diagram (Level 0)**
4. Customize theo dự án của bạn

---

## ✅ CHECKLIST HOÀN THÀNH

Khi vẽ xong, kiểm tra:

### Về nội dung:
- [ ] Có đủ 4 internal actors (ADMIN, MANAGER, OPERATOR, TENANT)
- [ ] Có đủ 3 external systems (VNPay, Email, Database)
- [ ] Hệ thống chính ở giữa, rõ ràng
- [ ] Mỗi actor có ít nhất 1 luồng vào và 1 luồng ra
- [ ] Tất cả luồng đều có label (ghi tên)

### Về hình thức:
- [ ] Màu sắc phân biệt rõ ràng
- [ ] Font chữ đồng nhất
- [ ] Khoảng cách đều nhau
- [ ] Mũi tên không chéo nhau (nếu có thể)
- [ ] Có chú thích (legend)

### Về technical:
- [ ] VNPay được đánh dấu là external payment gateway
- [ ] Email system được đánh dấu là SMTP service
- [ ] Database có thể bỏ qua (vì là technical detail)

---

## 📚 TÀI LIỆU THAM KHẢO

### Quy tắc vẽ Context Diagram:
1. **1 hệ thống duy nhất** ở giữa
2. **External entities** xung quanh
3. **Không có process con** (chỉ có ở Level 1)
4. **Data flow** phải có tên rõ ràng
5. **Không có data store** (chỉ có ở Level 1+)

### Ví dụ chuẩn:
- Yourdon/DeMarco notation (circle cho process)
- Gane-Sarson notation (rounded rectangle cho process)

**Dự án bạn nên dùng:** Yourdon/DeMarco (circle ở giữa)

---

## 🎓 LƯU Ý QUAN TRỌNG

### 1. Context Diagram ≠ Use Case Diagram
- **Context Diagram**: Focus vào DATA FLOW (dữ liệu)
- **Use Case Diagram**: Focus vào CHỨC NĂNG (actor làm gì)

### 2. Mức độ chi tiết
- **Context**: Chỉ overview, không chi tiết
- Nếu muốn chi tiết → vẽ Level 1 DFD

### 3. Database có cần thiết không?
- **Có thể bỏ** nếu coi như internal implementation
- **Nên thêm** nếu muốn nhấn mạnh data persistence

### 4. Số lượng actors
- Không có giới hạn
- Nhưng nên từ 3-7 actors (dễ nhìn)
- Dự án bạn: 4 internal + 2-3 external = OK ✅

---

## 🚀 BƯỚC TIẾP THEO SAU KHI VẼ XONG

### 1. Validate với stakeholders
- Cho Product Owner/Manager xem
- Xác nhận đã có đủ actors chưa
- Xác nhận luồng dữ liệu đúng chưa

### 2. Vẽ Level 1 DFD (nếu cần)
- Break down hệ thống thành processes con
- VD: Authentication, Invoice Management, Payment Processing, etc.

### 3. Tạo Data Dictionary
- Mô tả chi tiết mỗi data flow
- VD: "Thông tin hóa đơn" gồm: code, room_id, total_amount, due_date, etc.

### 4. Export và lưu trữ
- Export as PNG/PDF (high resolution)
- Lưu source file (để chỉnh sửa sau)
- Đưa vào tài liệu thiết kế hệ thống

---

## 📄 MẪU MÔ TẢ CONTEXT DIAGRAM (Để ghi trong báo cáo)

### Hostel Management System - Context Diagram

**1. Mục đích:**
Context Diagram mô tả các thành phần bên ngoài tương tác với Hệ thống Quản lý Nhà trọ và các luồng dữ liệu giữa chúng.

**2. Các thành phần:**

**2.1. Hệ thống chính:**
- Hostel Management System: Quản lý toàn bộ hoạt động của hệ thống nhà trọ

**2.2. Internal Actors:**
- ADMIN: Quản trị viên hệ thống, quản lý cơ sở và nhân sự
- MANAGER: Ban quản lý cơ sở, quản lý phòng, khách thuê, hóa đơn
- OPERATOR: Nhân viên vận hành, cập nhật điện nước, xử lý yêu cầu
- TENANT: Người thuê trọ, xem hóa đơn, thanh toán, gửi yêu cầu

**2.3. External Systems:**
- VNPay Gateway: Xử lý thanh toán trực tuyến
- Email System: Gửi thông báo qua email

**3. Luồng dữ liệu chính:**
- Quản lý cơ sở và nhân sự (ADMIN ↔ System)
- Quản lý phòng, khách thuê, hóa đơn (MANAGER ↔ System)
- Cập nhật điện nước (OPERATOR ↔ System)
- Xem hóa đơn và thanh toán (TENANT ↔ System)
- Xử lý thanh toán (System ↔ VNPay)
- Gửi thông báo (System ↔ Email)

---

**Chúc bạn vẽ Context Diagram thành công! 🎨**

Nếu cần hỗ trợ thêm về Level 1 DFD hoặc các diagram khác, hãy cho tôi biết.
