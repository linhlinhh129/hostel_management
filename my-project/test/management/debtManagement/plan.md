# Implementation Plan: Debt Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `DebtPageServlet.java`
- **Dependencies**: `DebtService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Tập trung vào xử lý logic ngày nợ, tiền nợ (không cho phép số tiền bị âm), và kiểm tra IDOR khi truy cập chéo cơ sở.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/DebtPageServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadDebtList_Success`: Gọi `GET /manager/debts`, Mock trả về danh sách công nợ. Servlet setAttributes và forward về JSP.
- `testDoGet_LoadDebtDetail_Success`: Gọi `GET /manager/debts?action=detail&id=1`, Mock trả về `DebtDetailDTO`. Trả về UI.

### 3.2 Error Cases
- `testDoGet_Filter_InvalidStatus`: Gọi GET với tham số `status=INVALID`. Servlet trả về lỗi HTTP 400 Bad Request.
- `testDoGet_Detail_IDOR_AccessDenied`: Manager A gọi xem detail hóa đơn của cơ sở B. Mock trả về null. Servlet ném HTTP 404 (Not Found) để bảo mật.
- `testDoGet_UnauthorizedAccess`: User có role TENANT truy cập. Servlet chặn 403.

### 3.3 Boundary Values
- `testDoGet_Boundary_NoLateFee_Before3Days`: Hóa đơn trễ 3 ngày -> Kiểm tra không phát sinh phí chậm nộp.
- `testDoGet_Boundary_LateFee_After3Days`: Hóa đơn trễ 4 ngày -> Kiểm tra có phí chậm nộp 1%.
- `testDoGet_Boundary_NegativeDebt_LockedToZero`: Khách đóng dư tiền -> Số tiền còn nợ trả ra màn hình khóa ở mức 0.

### 3.4 Concurrent Scenarios
- `testConcurrency_DebtList_ThreadSafety`: Ép tải 10 thread truy cập danh sách công nợ cùng lúc với các keyword tìm kiếm khác nhau, đảm bảo Request của Thread nào nhận được Keyword của Thread nấy (Stateless).

## 4. Các bước thực hiện
1. Thiết lập `DebtPageServletTest.java` với `@ExtendWith(MockitoExtension.class)`.
2. Tiến hành code Unit Tests cho từng case.
3. Map các thẻ EARS vào file code.
