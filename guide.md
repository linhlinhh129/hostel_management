# Quy Trình Kiểm Thử Theo "Playbook: Spec-Driven & Agent-Driven Development"

Dựa sát vào cuốn sách "Playbook: Spec-Driven & Agent-Driven Development", quy trình và tư duy kiểm thử (test) được quy định cực kỳ chặt chẽ để kiểm soát AI. Thay vì chỉ "chạy thử xem có lỗi không", sách hướng dẫn bạn test theo các nguyên tắc và công cụ sau:

## 1. Chiến lược viết Test 4 khía cạnh (Test Strategy)

Theo "Template Test Writing" trong sách, chiến lược test bắt buộc phải bao phủ 4 khía cạnh:
* *Happy path:* Các kịch bản thành công chính.
* *Error cases:* Các kịch bản lỗi, bắt buộc phải lấy trực tiếp từ các mẫu câu Unwanted (WHERE...) trong bản Spec. Mỗi error case = 1 test.
* *Boundary values:* Các giá trị biên (nhỏ nhất, lớn nhất, rỗng, độ dài tối đa/tối thiểu).
* *Concurrent scenarios:* Các kịch bản truy cập đồng thời (nếu có).

**Nguyên tắc tối thượng của sách:** "Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)". Nếu cách AI viết code bên trong thay đổi nhưng hành vi vẫn đúng, test vẫn phải pass.


## 2. Ứng dụng vòng lặp TDD (Test-Driven Development) kết hợp AI

Sách nhấn mạnh việc dùng test làm "hàng rào kiểm soát" AI thông qua vòng lặp TDD:
* *Bước 1 (Red):* Bạn viết test (hoặc định nghĩa Acceptance Criteria) để định nghĩa hành vi mong muốn.
* *Bước 2 (Green):* Agent viết code để vượt qua các bài test đó.
* *Bước 3 (Refactor):* Bạn review logic và yêu cầu AI dọn dẹp code.
* *Bước 4:* Lặp lại và thêm tests cho edge cases.

Sách khẳng định: "Tests là hàng rào: nếu AI tạo code sai, tests fail ngay lập tức. Bạn không cần đọc toàn bộ code chỉ cần xem tests có pass và tests có đúng".

## 3. Validation Gate (Cổng kiểm chứng 4 lớp) ở Pha 5

Sách quy định một task chỉ được coi là "Done" khi vượt qua 4 lớp kiểm tra của Validation Gate:
* *Lớp 1 (Automated):* Chạy unit test, linting, type check. Bắt buộc 100% test phải pass và độ phủ code (coverage) đạt tối thiểu 80%.
* *Lớp 2 (Spec Compliance):* Kiểm tra xem mọi yêu cầu SHALL trong Spec đã được code chưa thông qua các thẻ # EARS[]. Phải lập một *Ma trận truy vết (Traceability Matrix)* ánh xạ trực tiếp từ: Yêu cầu Spec -> Dòng code -> Tên Test -> Trạng thái Pass/Fail. Đồng thời kiểm tra để đảm bảo AI không code thừa những tính năng nằm trong phần "Out of Scope".
* *Lớp 3 (Constitution):* Hệ thống CI kiểm tra tự động xem code có vi phạm các quy tắc bảo mật và kiến trúc trong CONSTITUTION.md không.
* *Lớp 4 (Acceptance):* Chạy thử (demo) thủ công để đảm bảo toàn bộ luồng người dùng (user story) hoạt động trơn tru theo các tiêu chí chấp nhận.

## 4. Dùng chính AI để "săn" Edge Cases (Trường hợp ngoại lệ)

Sách có một kỹ thuật rất hay là dùng AI để tìm lỗi trước khi test. Bạn có thể dùng prompt: 
*"Đây là spec cho tính năng [X]. Hãy liệt kê 10 edge cases mà developer thường bỏ qua. Với mỗi edge case, giải thích tại sao nó nguy hiểm và đề xuất test case cụ thể"*


Kết quả thường bao gồm các lỗi do race conditions, timezone, null handling...

## 5. Nguyên tắc xử lý khi Test Fail: "Fix the Spec, not the Code"

Sách cảnh báo một lỗi cực kỳ phổ biến (Anti-pattern): Khi test fail, con người có xu hướng nhảy vào sửa code trực tiếp.

Theo sách, quy trình đúng là:
1. Xác định test nào fail.
2. Truy vết (Trace) về đoạn Spec xem yêu cầu EARS nào bị vi phạm.
3. Nếu lỗi do Spec thiếu hoặc mơ hồ -> Bạn phải cập nhật file SPEC.md cho rõ ràng.
4. Yêu cầu AI sinh (re-generate) lại code dựa trên bản Spec đã cập nhật. 

Bằng cách tuân thủ đúng quy trình này, toàn bộ tài liệu và test cases của bạn luôn đồng bộ với mã nguồn thực tế.