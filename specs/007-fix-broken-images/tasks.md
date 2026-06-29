## Cập nhật Backend

- [x] **Task 1**: Cập nhật `DetailRequestServlet.java`
  - [x] Bỏ `request.getContextPath()` tại dòng lưu `attachmentUrls2`.
- [x] **Task 2**: Cập nhật `IncidentReportServlet.java`
  - [x] Bỏ `request.getContextPath()` tại dòng lưu `attachmentUrls`.
- [x] **Task 3**: Cập nhật `UpdateMeterReadingServlet.java`
  - [x] Bỏ `request.getContextPath()` tại dòng lưu `electricImgUrl` và `waterImgUrl`.

## Cập nhật Frontend

- [x] **Task 4**: Cập nhật `operator/requests/detail.jsp`
  - [x] Import thư viện JSTL Functions (`<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>`).
  - [x] Thay đổi cách render `<img src="${img}">` sang logic an toàn (`${fn:startsWith(img, pageContext.request.contextPath) ? img : pageContext.request.contextPath += img}`).
- [x] **Task 5**: Cập nhật `tenant/tickets/detail.jsp`
  - [x] Thêm thư viện JSTL Functions.
  - [x] Áp dụng logic an toàn Context Path cho `ticket.attachmentUrls1` và vòng lặp `ticket.attachmentUrls2`.
