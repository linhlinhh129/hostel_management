# Use Case Specification: UC-24 – Manage Doctor Registration

**ID and Name:** UC-24 – Manage Doctor Registration  
**Primary Actor:** Admin  

## Description
Admin manages new doctor registration requests by reviewing submitted details and credentials. Based on verification, the admin can approve or reject the registration, with notifications sent to the doctor.

## Trigger
Admin selects “Doctor Registration Requests” from the dashboard.

## Preconditions
- Doctor has submitted a registration/profile with credentials (BR-01, BR-09).
- Doctor’s profile status = Pending Verification.

## Postconditions
- Doctor profile status is updated to either Verified or Rejected.
- Notification is sent to the doctor about the decision.

## Normal Flow
1. Admin opens the list of pending doctor registrations.
2. The system displays doctor details and uploaded credentials.
3. Admin reviews the submission.
4. Admin chooses:
   - **Approve:** System sets profile → Verified, stores record, and sends notification → MSG012 (Profile approved).
   - **Reject:** Admin enters rejection reason → system sets profile → Rejected and notifies doctor → MSG013 (Profile rejected: {reason}).
5. The system logs the decision for audit purposes.

## Alternative Flows
- **AF1:** Missing or unclear credential → admin requests resubmission instead of reject → system updates profile status to Resubmission Required → notify doctor → MSG065 (Resubmission requested: please upload missing documents).

## Exceptions
- **EX1:** Network error while saving/deleting → MSG041 (Network error).
- **EX2:** System error → MSG003 (System error, please try again later).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Frequent – whenever new doctors register on the platform.
- **Business Rules:** BR-01, BR-09, BR-33

## Other Information
- Approved doctors become visible in UC: Search Doctor & View Profile (BR-15).
- Rejected doctors can resubmit using UC-7: Resubmit Profile.

## Assumptions
- Admin makes decisions based on complete and valid evidence.
- The notification system is reliable and immediate.
- Doctors will correct and resubmit if requested.

---

# Use Case Specification: UC-01 – Login

**ID and Name:** UC-01 – Login  
**Primary Actor:** Any User (Tenant, Admin, Manager)

## Description
The user logs into the system using their registered credentials (email/username and password) to access personalized features and their dashboard.

## Trigger
User navigates to the login page and selects the "Login" option.

## Preconditions
- User must have an active and registered account.
- User is currently logged out.

## Postconditions
- User is successfully authenticated and redirected to their respective dashboard.
- A session is created.

## Normal Flow
1. User enters their email/username and password.
2. User clicks the "Login" button.
3. System validates the credentials against the database.
4. System authenticates the user and generates a session token.
5. System redirects the user to the dashboard corresponding to their role.

## Alternative Flows
- **AF1:** Incorrect credentials → System displays error message MSG001 (Invalid email or password) and prompts user to try again.
- **AF2:** Account locked/inactive → System displays error message MSG002 (Account is locked or inactive, please contact support).

## Exceptions
- **EX1:** Network error → MSG041 (Network error).
- **EX2:** System error → MSG003 (System error, please try again later).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Frequent
- **Business Rules:** Passwords must be encrypted/hashed. Max failed attempts policy applies.

## Other Information
- Links to "Forgot Password" available on the login screen.

## Assumptions
- User has a stable internet connection.
- User remembers their credentials.

---

# Use Case Specification: UC-02 – Forgot Password

**ID and Name:** UC-02 – Forgot Password  
**Primary Actor:** Any User (Tenant, Admin, Manager)

## Description
Allows a user who has forgotten their password to request a password reset link via their registered email address.

## Trigger
User clicks the "Forgot Password" link on the login page.

## Preconditions
- User is not logged in.
- User has a registered email address in the system.

## Postconditions
- A password reset link is sent to the user's email.
- User can successfully reset their password and log in.

## Normal Flow
1. User enters their registered email address.
2. User clicks the "Send Reset Link" button.
3. System verifies if the email exists in the database.
4. System generates a secure, time-limited reset token.
5. System sends an email containing the reset link to the user.
6. System displays a confirmation message MSG004 (Reset link sent if email exists).

## Alternative Flows
- **AF1:** Email not found → System still displays MSG004 to prevent email enumeration attacks.

## Exceptions
- **EX1:** Email service unavailable → MSG005 (Unable to send email, please try again later).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Occasional
- **Business Rules:** Reset link expires in 15 minutes.

## Other Information
- After resetting, user is redirected to the Login page.

## Assumptions
- User has access to their email account.

---

# Use Case Specification: UC-03 – View Profile

**ID and Name:** UC-03 – View Profile  
**Primary Actor:** Any User (Tenant, Admin, Manager)

## Description
User views their personal profile details, including personal information, contact details, and account settings.

## Trigger
User clicks on their profile picture or "Profile" link from the navigation menu.

## Preconditions
- User is logged into the system.

## Postconditions
- User is able to view their current profile information.

## Normal Flow
1. User navigates to the Profile section.
2. System retrieves user's profile data from the database.
3. System displays the profile information (Name, Email, Phone, Role, etc.).

## Alternative Flows
- None.

## Exceptions
- **EX1:** Data retrieval error → MSG006 (Unable to load profile data).

## Additional Details
- **Priority:** Medium
- **Frequency of Use:** Moderate
- **Business Rules:** Profile details are read-only in this view unless "Edit Profile" is selected.

## Other Information
- Serves as the starting point for updating personal information.

## Assumptions
- The database is accessible and data is intact.

---

# Use Case Specification: UC-04 – View Dashboard

**ID and Name:** UC-04 – View Dashboard  
**Primary Actor:** Any User (Tenant, Admin, Manager)

## Description
Displays a role-specific overview of the system. For a tenant, it may show upcoming rent, recent notifications, and active requests. For an admin/manager, it shows overall occupancy, total revenue, pending incidents, etc.

## Trigger
User successfully logs in, or clicks the "Dashboard" link in the navigation menu.

## Preconditions
- User is logged into the system.

## Postconditions
- User sees relevant summary data and quick action links based on their role.

## Normal Flow
1. User requests the Dashboard page.
2. System checks the user's role.
3. System aggregates necessary data (stats, notifications, pending items).
4. System displays the role-specific dashboard interface.

## Alternative Flows
- **AF1:** Dashboard customization → User reorganizes widgets (if supported), system saves layout preferences.

## Exceptions
- **EX1:** Failure to load specific widget data → System displays placeholder "Data unavailable" for that widget without crashing the whole dashboard.

## Additional Details
- **Priority:** High
- **Frequency of Use:** High – typically the landing page.
- **Business Rules:** Data shown must be strictly filtered by user's role and permissions.

## Other Information
- Dashboard is the central hub for navigation.

## Assumptions
- The dashboard metrics can be calculated reasonably fast.

---

# Use Case Specification: UC-05 – Logout

**ID and Name:** UC-05 – Logout  
**Primary Actor:** Any User (Tenant, Admin, Manager)

## Description
User securely ends their current session and exits the authenticated area of the system.

## Trigger
User clicks the "Logout" button/link.

## Preconditions
- User is currently logged into the system.

## Postconditions
- User session is destroyed.
- User is redirected to the login page or public homepage.

## Normal Flow
1. User initiates the logout action.
2. System prompts for confirmation (optional).
3. System invalidates the current session token.
4. System clears user data from local storage/cookies.
5. System redirects user to the Login page.

## Alternative Flows
- None.

## Exceptions
- **EX1:** Session already expired → System treats as successful logout and redirects to login.

## Additional Details
- **Priority:** High
- **Frequency of Use:** Frequent
- **Business Rules:** All session data must be cleared securely from the client side and server side.

## Other Information
- Helps maintain account security on shared devices.

## Assumptions
- Logout requests are processed immediately.

---

# Use Case Specification: UC-06 – View Meter Reading

**ID and Name:** UC-06 – View Meter Reading List  
**Primary Actor:** Operator

## Description
Allows the Operator to view a list of electricity and water meter readings for all rooms. The list shows the room code, previous readings, last updated time, and current update status (e.g., DA_CAP_NHAT, CHUA_CAP_NHAT) to monitor which rooms have updated meter readings for the current billing cycle.

## Trigger
Operator selects "Meter Readings" (Danh sách chỉ số điện nước) from the menu.

## Preconditions
- Operator is logged into the system.

## Postconditions
- System displays the list of rooms and their meter reading statuses.

## Normal Flow
1. Operator navigates to the Meter Readings section.
2. System verifies Operator role and permissions.
3. System fetches the list of all active rooms along with their previous readings and update statuses.
4. System displays the data in a tabular format.

## Alternative Flows
- **AF1:** Room has no readings in current cycle → System displays status "CHUA_CAP_NHAT" (Not Updated).
- **AF2:** Room has readings in current cycle → System displays status "DA_CAP_NHAT" (Updated).

## Exceptions
- **EX1:** No data available → System displays an empty list.

## Additional Details
- **Priority:** High
- **Frequency of Use:** Monthly/Frequent during billing cycle
- **Business Rules:** Only active rooms are displayed.

## Other Information
- Tenant and Manager do not use this standalone list; their meter readings are viewed within their respective Invoices.

## Assumptions
- The database is accessible and data is intact.

---

# Use Case Specification: UC-07 – Update Meter Reading

**ID and Name:** UC-07 – Update Meter Reading  
**Primary Actor:** Operator

## Description
Operator records or modifies the utility meter readings (electricity, water) for a specific room and billing cycle.

## Trigger
Operator selects "Update Reading" or "Add New Reading" from the Meter Readings page.

## Preconditions
- Operator is logged in.
- A valid room and billing cycle must be selected.

## Postconditions
- New reading is saved.
- System sets the status to UPDATED and stores the uploaded meter images.

## Normal Flow
1. Operator selects a room and specifies the month/billing cycle.
2. Operator inputs the new meter reading value and uploads a photo of the meter as proof.
3. Operator clicks "Save".
4. System validates that the new reading is equal to or greater than the previous reading.
5. System stores the reading and images in the database.
6. System displays success message MSG008 (Meter reading updated successfully).

## Alternative Flows
- **AF1:** Edit existing reading → Operator modifies an already inputted reading for the current month. System logs the change and updates billing if not already paid.

## Exceptions
- **EX1:** New reading is lower than previous reading → System displays error MSG009 (New reading cannot be lower than the previous reading).
- **EX2:** Missing meter image → System displays error MSG015 (Meter image is required).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Monthly
- **Business Rules:** Cannot modify a meter reading if the corresponding invoice has already been paid.

## Other Information
- Forms the basis for monthly utility invoices.

## Assumptions
- Physical meters are functioning correctly.

---

# Use Case Specification: UC-08 – View Request

**ID and Name:** UC-08 – View Request  
**Primary Actor:** Operator

## Description
Combines viewing the list of requests and viewing request details. Operator can see a list of assigned service/maintenance requests. From the list, the Operator can select a specific request to view its full details (including attached images, processing history, and status).

## Trigger
Operator selects "Requests" from the navigation menu.

## Preconditions
- Operator is logged in.

## Postconditions
- System displays the assigned request list or detailed request information.

## Normal Flow
1. Operator navigates to the Requests section.
2. System fetches and displays the list of assigned requests.
3. Operator selects filters (e.g., status, category, room) to find specific requests.
4. Operator clicks on a specific request card/row.
5. System redirects to the Detail Request page.
6. System displays full details: category, description, attached images, room info, timeline, and current status.

## Alternative Flows
- **AF1:** No requests match filter → System displays "No requests found matching criteria".

## Exceptions
- **EX1:** Request not found or unauthorized access → System displays MSG010 (Request not found or access denied).

## Additional Details
- **Priority:** High
- **Frequency of Use:** High
- **Business Rules:** Operator can only view requests assigned to them or within their scope.

## Other Information
- Provides the entry point for further actions like accepting or updating a request.

## Assumptions
- Request statuses are kept up to date.

---

# Use Case Specification: UC-09 – Update Request

**ID and Name:** UC-09 – Update Request (Report Completion)  
**Primary Actor:** Operator

## Description
Allows the Operator to report the completion of a maintenance request. The Operator must input resolution notes and upload at least one post-repair image as evidence to change the request status to COMPLETED.

## Trigger
Operator clicks "Report Completion" (Báo cáo hoàn thành) on the Request Detail page.

## Preconditions
- Operator is logged in and viewing an assigned request currently in IN_PROGRESS status.

## Postconditions
- Request status is updated to COMPLETED.
- Resolution notes and post-repair images are saved to the database.

## Normal Flow
1. Operator views the details of an IN_PROGRESS request.
2. Operator clicks the "Report Completion" button.
3. System displays a form to input resolution notes and upload proof images.
4. Operator inputs notes and uploads at least one after-repair image.
5. Operator clicks "Confirm Save".
6. System validates the inputs and updates the request status to COMPLETED, recording the exact completion time.
7. System displays a success message and reloads the detail page.

## Alternative Flows
- None.

## Exceptions
- **EX1:** Missing mandatory resolution notes → System displays error (Notes are required).
- **EX2:** Missing mandatory post-repair image → System displays error (At least one post-repair image is required).
- **EX3:** Attempting to update a request not assigned to the operator → System displays error (Unauthorized to update this request).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Frequent (every time a repair is finished)
- **Business Rules:** Image proof is strictly mandatory to ensure transparency and prevent fraud.

## Other Information
- This serves as the final step in the maintenance workflow.

## Assumptions
- Operator has a mobile device to capture and upload images on-site.

---

# Use Case Specification: UC-10 – Create Incident (On-site Report)

**ID and Name:** UC-10 – Create Incident (On-site Report)  
**Primary Actor:** Operator

## Description
Allows an Operator to report a problem or incident (e.g., broken equipment, leaks, security issues) discovered during facility inspections or daily operations. The Operator records the location (Public Area or Specific Room), category, priority, and attaches photo evidence to quickly notify management.

## Trigger
Operator clicks "Report Incident" from their dashboard/menu.

## Preconditions
- Operator is logged in.

## Postconditions
- A new incident ticket is created with status PENDING.
- The incident is assigned to the reporting Operator by default (or logged with their Staff ID).
- Management is notified.

## Normal Flow
1. Operator navigates to the Incident Reporting form.
2. Operator selects the Facility/Building.
3. Operator selects the location (Public Area or Specific Room) and chooses the exact area/room ID.
4. Operator selects the category (e.g., Electricity, Water, Security) and priority (Normal, Emergency).
5. Operator provides a detailed description and uploads photo(s) of the incident.
6. Operator submits the form.
7. System generates a unique Incident ID and sets status to PENDING.
8. System displays success message MSG014 (Incident reported successfully).

## Alternative Flows
- **AF1:** Missing mandatory fields → System highlights missing fields and prevents submission.
- **AF2:** Image optimization → System automatically compresses uploaded images to save bandwidth before submission.

## Exceptions
- **EX1:** Invalid file upload (e.g., format or too large) → MSG015 (File size exceeds limit or invalid format).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Moderate
- **Business Rules:** Emergency incidents should immediately notify the Manager. Photo evidence is highly recommended/mandatory.

## Other Information
- Digitalizes on-site problem reporting, reducing reliance on manual messaging apps.

## Assumptions
- Operators use mobile devices with cameras to capture and compress photos.

---

# Use Case Specification: UC-11 – Reporting

**ID and Name:** UC-11 – Reporting  
**Primary Actor:** Admin, Manager

## Description
Admin/Manager generates and views analytical reports related to hostel operations, such as financial reports (revenue, unpaid invoices), occupancy rates, and maintenance analytics.

## Trigger
Admin/Manager navigates to the "Reports" or "Analytics" section.

## Preconditions
- Admin/Manager is logged in with appropriate permissions.

## Postconditions
- The requested report is generated and displayed or downloaded.

## Normal Flow
1. Admin/Manager selects the type of report to generate.
2. Admin/Manager specifies parameters (e.g., Date Range, Hostel Block, Report Category).
3. Admin/Manager clicks "Generate Report".
4. System aggregates and processes data from the database.
5. System displays the report on-screen in tabular and chart formats.
6. Admin/Manager optionally clicks "Export" (PDF/Excel) to download the report.

## Alternative Flows
- **AF1:** Empty dataset for selected parameters → System displays MSG016 (No data available for the selected criteria).

## Exceptions
- **EX1:** Timeout during large data aggregation → MSG017 (Report generation timed out, please narrow your search criteria).

## Additional Details
- **Priority:** Medium
- **Frequency of Use:** Weekly/Monthly
- **Business Rules:** Access to financial reports may be restricted to top-level Admins only.

## Other Information
- Crucial for business decision-making and auditing.

## Assumptions
- Data in the system is up-to-date.

---

# Use Case Specification: UC-12 – View Notification

**ID and Name:** UC-12 – View Notification (System & Tasks)  
**Primary Actor:** Operator

## Description
Allows the Operator to view system alerts and task assignments sent by the Admin/Manager. These notifications inform the Operator about new maintenance requests assigned to them, system-wide alerts, or urgent announcements.

## Trigger
Operator clicks on the "Notifications" icon (bell icon) in the header.

## Preconditions
- Operator is logged in.

## Postconditions
- Notifications are marked as read.
- Operator is informed of new tasks or system alerts.

## Normal Flow
1. Operator clicks the Notification icon.
2. System displays a list of recent system and task notifications, sorted chronologically.
3. Unread notifications are highlighted.
4. Operator clicks on a specific notification (e.g., a new job assignment).
5. System marks the notification as "Read".
6. System redirects the Operator to the relevant page (e.g., Request Detail page).

## Alternative Flows
- **AF1:** Mark all as read → Operator clicks "Mark all as read", system updates status for all notifications.

## Exceptions
- **EX1:** Notification links to deleted/reassigned task → System displays MSG018 (The task associated with this notification is no longer available).

## Additional Details
- **Priority:** High
- **Frequency of Use:** Very Frequent
- **Business Rules:** Operator notifications are strictly system-oriented and managerial task assignments.

## Other Information
- Ensures the Operator is immediately aware of newly assigned work and critical system updates.

## Assumptions
- The notification service pushes alerts to the Operator in real-time.

---
---

# Phiên bản Tiếng Việt (Vietnamese Version)

---

# Đặc tả Use Case: UC-01 – Đăng nhập (Login)

**ID và Tên:** UC-01 – Đăng nhập  
**Tác nhân chính:** Mọi người dùng (Khách thuê, Quản trị viên, Quản lý)

## Mô tả
Người dùng đăng nhập vào hệ thống bằng thông tin đã đăng ký (email/tên đăng nhập và mật khẩu) để truy cập các tính năng cá nhân và bảng điều khiển (dashboard) của họ.

## Kích hoạt (Trigger)
Người dùng truy cập trang đăng nhập và chọn "Đăng nhập".

## Điều kiện tiên quyết (Preconditions)
- Người dùng phải có tài khoản đang hoạt động và đã đăng ký.
- Người dùng hiện đang ở trạng thái chưa đăng nhập (logged out).

## Hậu điều kiện (Postconditions)
- Người dùng xác thực thành công và được chuyển hướng đến bảng điều khiển tương ứng.
- Một phiên làm việc (session) được tạo.

## Luồng cơ bản (Normal Flow)
1. Người dùng nhập email/tên đăng nhập và mật khẩu.
2. Người dùng nhấn nút "Đăng nhập".
3. Hệ thống kiểm tra thông tin với cơ sở dữ liệu.
4. Hệ thống xác thực người dùng và tạo token phiên làm việc.
5. Hệ thống chuyển hướng người dùng đến bảng điều khiển theo vai trò.

## Luồng thay thế (Alternative Flows)
- **AF1:** Sai thông tin → Hệ thống hiển thị lỗi MSG001 (Email hoặc mật khẩu không hợp lệ) và yêu cầu thử lại.
- **AF2:** Tài khoản bị khóa/vô hiệu hóa → Hệ thống hiển thị lỗi MSG002 (Tài khoản bị khóa, vui lòng liên hệ hỗ trợ).

## Ngoại lệ (Exceptions)
- **EX1:** Lỗi mạng → MSG041 (Lỗi kết nối mạng).
- **EX2:** Lỗi hệ thống → MSG003 (Lỗi hệ thống, vui lòng thử lại sau).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên
- **Quy tắc nghiệp vụ:** Mật khẩu phải được mã hóa. Tối đa 5 lần nhập sai trước khi khóa tài khoản.

## Thông tin khác
- Link "Quên mật khẩu" có sẵn trên màn hình đăng nhập.

## Giả định
- Người dùng có kết nối internet ổn định.
- Người dùng nhớ thông tin đăng nhập của mình.

---

# Đặc tả Use Case: UC-02 – Quên mật khẩu (Forgot Password)

**ID và Tên:** UC-02 – Quên mật khẩu  
**Tác nhân chính:** Mọi người dùng (Khách thuê, Quản trị viên, Quản lý)

## Mô tả
Cho phép người dùng đã quên mật khẩu yêu cầu gửi liên kết đặt lại mật khẩu qua địa chỉ email đã đăng ký.

## Kích hoạt
Người dùng nhấn vào liên kết "Quên mật khẩu" trên trang đăng nhập.

## Điều kiện tiên quyết
- Người dùng chưa đăng nhập.
- Người dùng có địa chỉ email đã đăng ký trong hệ thống.

## Hậu điều kiện
- Một liên kết đặt lại mật khẩu được gửi đến email người dùng.
- Người dùng có thể đặt lại mật khẩu và đăng nhập thành công.

## Luồng cơ bản
1. Người dùng nhập địa chỉ email đã đăng ký.
2. Người dùng nhấn nút "Gửi liên kết đặt lại".
3. Hệ thống kiểm tra email trong cơ sở dữ liệu.
4. Hệ thống tạo một token đặt lại bảo mật, có thời hạn.
5. Hệ thống gửi email chứa liên kết đến người dùng.
6. Hệ thống hiển thị thông báo MSG004 (Liên kết đã được gửi nếu email tồn tại).

## Luồng thay thế
- **AF1:** Không tìm thấy email → Hệ thống vẫn hiện MSG004 để tránh lộ lọt thông tin email.

## Ngoại lệ
- **EX1:** Dịch vụ email không khả dụng → MSG005 (Không thể gửi email lúc này).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thỉnh thoảng
- **Quy tắc nghiệp vụ:** Liên kết hết hạn sau 15 phút.

## Thông tin khác
- Sau khi đặt lại, chuyển hướng về trang Đăng nhập.

## Giả định
- Người dùng có quyền truy cập vào hộp thư email của họ.

---

# Đặc tả Use Case: UC-03 – Xem hồ sơ cá nhân (View Profile)

**ID và Tên:** UC-03 – Xem hồ sơ cá nhân  
**Tác nhân chính:** Mọi người dùng (Khách thuê, Quản trị viên, Quản lý)

## Mô tả
Người dùng xem thông tin cá nhân của mình, bao gồm thông tin liên hệ và cài đặt tài khoản.

## Kích hoạt
Người dùng nhấn vào ảnh đại diện hoặc liên kết "Hồ sơ" trên menu.

## Điều kiện tiên quyết
- Người dùng đã đăng nhập vào hệ thống.

## Hậu điều kiện
- Người dùng xem được thông tin hồ sơ hiện tại của họ.

## Luồng cơ bản
1. Người dùng điều hướng đến mục Hồ sơ (Profile).
2. Hệ thống lấy dữ liệu từ cơ sở dữ liệu.
3. Hệ thống hiển thị thông tin (Tên, Email, SĐT, Vai trò, v.v.).

## Luồng thay thế
- Không có.

## Ngoại lệ
- **EX1:** Lỗi lấy dữ liệu → MSG006 (Không thể tải dữ liệu hồ sơ).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Trung bình
- **Tần suất sử dụng:** Vừa phải
- **Quy tắc nghiệp vụ:** Chỉ xem trừ khi chọn "Chỉnh sửa".

## Thông tin khác
- Là điểm xuất phát để cập nhật thông tin cá nhân.

## Giả định
- Cơ sở dữ liệu hoạt động bình thường.

---

# Đặc tả Use Case: UC-04 – Xem Bảng điều khiển (View Dashboard)

**ID và Tên:** UC-04 – Xem Bảng điều khiển  
**Tác nhân chính:** Mọi người dùng (Khách thuê, Quản trị viên, Quản lý)

## Mô tả
Hiển thị tổng quan hệ thống theo vai trò. Khách thuê xem tiền phòng sắp tới, thông báo, yêu cầu đang mở. Quản lý xem tỷ lệ lấp đầy, doanh thu, sự cố chờ xử lý, v.v.

## Kích hoạt
Người dùng đăng nhập thành công hoặc nhấn vào "Dashboard" trên menu.

## Điều kiện tiên quyết
- Người dùng đã đăng nhập.

## Hậu điều kiện
- Người dùng thấy dữ liệu tóm tắt phù hợp với vai trò của họ.

## Luồng cơ bản
1. Người dùng yêu cầu trang Bảng điều khiển.
2. Hệ thống kiểm tra vai trò người dùng.
3. Hệ thống tổng hợp dữ liệu cần thiết.
4. Hệ thống hiển thị giao diện dashboard theo vai trò.

## Luồng thay thế
- **AF1:** Tùy chỉnh Dashboard → Người dùng sắp xếp lại các khối (widgets), hệ thống lưu lại tùy chọn.

## Ngoại lệ
- **EX1:** Lỗi tải một phần dữ liệu → Hiển thị "Không có dữ liệu" cho phần đó mà không làm sập toàn bộ trang.

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Cao (trang đích).
- **Quy tắc nghiệp vụ:** Dữ liệu phải được lọc nghiêm ngặt theo quyền hạn.

## Thông tin khác
- Là trung tâm điều hướng chính.

## Giả định
- Các số liệu tổng hợp được tính toán nhanh chóng.

---

# Đặc tả Use Case: UC-05 – Đăng xuất (Logout)

**ID và Tên:** UC-05 – Đăng xuất  
**Tác nhân chính:** Mọi người dùng (Khách thuê, Quản trị viên, Quản lý)

## Mô tả
Người dùng kết thúc phiên làm việc an toàn và thoát khỏi khu vực xác thực của hệ thống.

## Kích hoạt
Người dùng nhấn nút "Đăng xuất".

## Điều kiện tiên quyết
- Người dùng đang đăng nhập.

## Hậu điều kiện
- Phiên làm việc bị hủy.
- Người dùng được chuyển hướng về trang đăng nhập hoặc trang chủ.

## Luồng cơ bản
1. Người dùng thực hiện thao tác đăng xuất.
2. Hệ thống hỏi xác nhận (tùy chọn).
3. Hệ thống vô hiệu hóa token phiên làm việc hiện tại.
4. Hệ thống xóa dữ liệu người dùng khỏi bộ nhớ cục bộ/cookies.
5. Hệ thống chuyển hướng người dùng đến trang Đăng nhập.

## Luồng thay thế
- Không có.

## Ngoại lệ
- **EX1:** Phiên đã hết hạn trước đó → Hệ thống coi như đã đăng xuất thành công và chuyển hướng.

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên
- **Quy tắc nghiệp vụ:** Xóa sạch dữ liệu phiên ở cả client và server.

## Thông tin khác
- Giúp bảo mật tài khoản trên các thiết bị dùng chung.

## Giả định
- Yêu cầu đăng xuất được xử lý ngay lập tức.

---

# Đặc tả Use Case: UC-06 – Xem chỉ số đồng hồ (View Meter Reading)

**ID và Tên:** UC-06 – Xem danh sách chỉ số điện/nước  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Người vận hành xem danh sách chỉ số điện/nước của tất cả các phòng để theo dõi trạng thái cập nhật trong kỳ. Hệ thống hiển thị mã phòng, số điện/nước kỳ trước, thời gian cập nhật gần nhất và trạng thái (ĐÃ CẬP NHẬT hoặc CHƯA CẬP NHẬT).

## Kích hoạt
Người vận hành chọn "Danh sách chỉ số điện nước" từ menu.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập vào hệ thống.

## Hậu điều kiện
- Hệ thống hiển thị danh sách các phòng cùng trạng thái chỉ số điện nước hiện tại.

## Luồng cơ bản
1. Người vận hành truy cập vào màn hình danh sách chỉ số điện nước.
2. Hệ thống lấy danh sách các phòng đang hoạt động cùng chỉ số điện/nước kỳ trước.
3. Hệ thống kiểm tra từng phòng đã có bản ghi trong kỳ hiện tại chưa để gắn trạng thái cập nhật.
4. Hệ thống hiển thị dữ liệu dưới dạng bảng danh sách.

## Luồng thay thế
- **AF1:** Phòng chưa có bản ghi trong kỳ hiện tại → Hệ thống hiển thị trạng thái "CHƯA CẬP NHẬT" (CHUA_CAP_NHAT).
- **AF2:** Phòng đã có bản ghi trong kỳ hiện tại → Hệ thống hiển thị trạng thái "ĐÃ CẬP NHẬT" (DA_CAP_NHAT).

## Ngoại lệ
- **EX1:** Không tìm thấy dữ liệu phòng → Hệ thống hiển thị danh sách rỗng.

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên (đặc biệt vào thời điểm chốt cước).
- **Quy tắc nghiệp vụ:** Chỉ hiển thị các phòng đang hoạt động. (Khách thuê và Quản lý xem chỉ số thông qua Hóa đơn, không dùng tính năng danh sách này).

## Thông tin khác
- Dùng để theo dõi tiến độ ghi chỉ số trước khi chốt hóa đơn.

## Giả định
- Dữ liệu phòng và chỉ số đồng hồ luôn được lưu trữ và cập nhật chính xác.

---

# Đặc tả Use Case: UC-07 – Cập nhật chỉ số đồng hồ (Update Meter Reading)

**ID và Tên:** UC-07 – Cập nhật chỉ số điện/nước  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Người vận hành ghi nhận hoặc chỉnh sửa chỉ số đồng hồ (điện, nước) cho một phòng cụ thể trong chu kỳ tính cước, kèm theo hình ảnh minh chứng.

## Kích hoạt
Người vận hành chọn "Cập nhật chỉ số" hoặc "Thêm chỉ số mới" trên trang danh sách Chỉ số đồng hồ.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập.
- Đã chọn một phòng hợp lệ.

## Hậu điều kiện
- Chỉ số điện/nước mới được lưu lại cùng với hình ảnh minh chứng.
- Trạng thái bản ghi chuyển thành UPDATED.

## Luồng cơ bản
1. Người vận hành chọn phòng.
2. Người vận hành nhập giá trị chỉ số điện/nước mới và bắt buộc tải lên ảnh chụp công tơ điện/nước làm minh chứng.
3. Người vận hành nhấn "Lưu".
4. Hệ thống kiểm tra chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ.
5. Hệ thống lưu dữ liệu và hình ảnh vào cơ sở dữ liệu.
6. Hệ thống hiện thông báo thành công MSG008 (Đã cập nhật chỉ số đồng hồ).

## Luồng thay thế
- **AF1:** Sửa chỉ số đã nhập → Người vận hành sửa lại chỉ số của tháng hiện tại. Hệ thống ghi log sự thay đổi.

## Ngoại lệ
- **EX1:** Chỉ số mới nhỏ hơn chỉ số cũ → MSG009 (Chỉ số mới không thể nhỏ hơn chỉ số kỳ trước).
- **EX2:** Thiếu ảnh công tơ → MSG015 (Bắt buộc phải tải lên ảnh công tơ).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Hàng tháng
- **Quy tắc nghiệp vụ:** Chỉ số mới phải ≥ chỉ số cũ. Bắt buộc có ảnh minh chứng. (Manager/Admin không thực hiện thao tác này).

## Thông tin khác
- Cơ sở để tính tiền điện/nước hàng tháng.

## Giả định
- Đồng hồ vật lý hoạt động bình thường và Operator thao tác tại hiện trường.

---

# Đặc tả Use Case: UC-08 – Xem yêu cầu (View Request)

**ID và Tên:** UC-08 – Xem yêu cầu (Danh sách và Chi tiết)  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Gộp chung tính năng Xem danh sách yêu cầu và Xem chi tiết yêu cầu dành cho Người vận hành. Người vận hành có thể xem danh sách các yêu cầu sửa chữa/hỗ trợ được phân công, lọc theo các tiêu chí. Từ danh sách, người vận hành nhấn vào một yêu cầu để xem toàn bộ thông tin chi tiết (nội dung, ảnh đính kèm, lịch sử xử lý, trạng thái hiện tại).

## Kích hoạt
Người vận hành chọn "Danh sách yêu cầu" từ menu.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập vào hệ thống.

## Hậu điều kiện
- Hệ thống hiển thị danh sách yêu cầu được giao hoặc thông tin chi tiết của một yêu cầu.

## Luồng cơ bản
1. Người vận hành truy cập vào trang Danh sách yêu cầu.
2. Hệ thống lấy dữ liệu danh sách các yêu cầu được giao cho người vận hành.
3. Người vận hành sử dụng các bộ lọc (trạng thái, loại yêu cầu, phòng) để tìm kiếm.
4. Người vận hành nhấn vào một yêu cầu trong danh sách.
5. Hệ thống chuyển hướng sang trang Chi tiết yêu cầu.
6. Hệ thống hiển thị đầy đủ thông tin: tiêu đề, mô tả, ảnh chụp, thông tin phòng, lịch sử các bước xử lý và trạng thái.

## Luồng thay thế
- **AF1:** Không có dữ liệu phù hợp với bộ lọc → Hệ thống hiển thị "Không có yêu cầu nào phù hợp".

## Ngoại lệ
- **EX1:** Yêu cầu không tồn tại hoặc không có quyền truy cập → MSG010 (Không tìm thấy hoặc truy cập bị từ chối).

## Thông bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên
- **Quy tắc nghiệp vụ:** Người vận hành chỉ xem được các yêu cầu được phân công cho mình hoặc thuộc phạm vi quản lý.

## Thông tin khác
- Là màn hình trung tâm để điều hướng sang các thao tác nhận việc hoặc cập nhật trạng thái.

## Giả định
- Trạng thái yêu cầu luôn được cập nhật chính xác theo thời gian thực.

---

# Đặc tả Use Case: UC-09 – Cập nhật yêu cầu (Update Request)

**ID và Tên:** UC-09 – Cập nhật yêu cầu (Báo cáo hoàn thành)  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Cho phép Người vận hành (Operator) báo cáo kết quả sau khi hoàn thành sửa chữa sự cố. Bắt buộc phải nhập ghi chú kết quả và tải lên ít nhất một hình ảnh sau sửa chữa làm minh chứng để chuyển trạng thái yêu cầu sang Hoàn thành (COMPLETED).

## Kích hoạt
Người vận hành nhấn nút "Báo cáo hoàn thành" trên màn hình Chi tiết yêu cầu.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập và đang xem chi tiết một yêu cầu được giao ở trạng thái Đang xử lý (IN_PROGRESS).

## Hậu điều kiện
- Trạng thái yêu cầu chuyển thành COMPLETED.
- Ghi chú và hình ảnh minh chứng được lưu vào hệ thống.

## Luồng cơ bản
1. Người vận hành đang ở màn hình Chi tiết yêu cầu.
2. Người vận hành nhấn nút "Báo cáo hoàn thành".
3. Hệ thống mở form nhập Ghi chú kết quả và Đính kèm ảnh minh chứng.
4. Người vận hành nhập ghi chú và tải lên ít nhất 1 ảnh chụp sau khi đã sửa xong.
5. Người vận hành nhấn "Xác nhận lưu".
6. Hệ thống kiểm tra tính hợp lệ, lưu hình ảnh và dữ liệu, đồng thời tự động cập nhật thời gian hoàn thành.
7. Hệ thống chuyển trạng thái yêu cầu sang COMPLETED và hiển thị thông báo thành công.

## Luồng thay thế
- Không có.

## Ngoại lệ
- **EX1:** Thiếu ghi chú kết quả → Hệ thống báo lỗi yêu cầu nhập nội dung xử lý.
- **EX2:** Thiếu ảnh minh chứng → Hệ thống báo lỗi bắt buộc phải có ảnh chụp sau sửa chữa.
- **EX3:** Thao tác trên yêu cầu không được giao → Hệ thống báo lỗi không có quyền truy cập.

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên
- **Quy tắc nghiệp vụ:** Bắt buộc có hình ảnh để đảm bảo tính minh bạch. Hệ thống tự động lấy thời gian hiện tại làm thời gian hoàn thành để tránh gian lận.

## Thông tin khác
- Đây là bước cuối cùng trong luồng xử lý sự cố tại hiện trường.

## Giả định
- Người vận hành sử dụng thiết bị có camera hoặc có sẵn ảnh hợp lệ để tải lên.

---

# Đặc tả Use Case: UC-10 – Báo cáo sự cố (Create Incident)

**ID và Tên:** UC-10 – Báo cáo sự cố tại hiện trường  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Cho phép Người vận hành khởi tạo một báo cáo sự cố ngay lập tức trên hệ thống bằng thiết bị di động khi phát hiện các vấn đề (hỏng hóc, rò rỉ nước, an ninh) trong quá trình tuần tra, kiểm tra cơ sở vật chất. Báo cáo cần đính kèm hình ảnh hiện trường để thông tin được số hóa và gửi đến Ban quản lý.

## Kích hoạt
Người vận hành nhấn "Báo cáo sự cố" trên màn hình điều khiển.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập vào hệ thống.

## Hậu điều kiện
- Một Ticket báo cáo sự cố mới được tạo và gán trạng thái PENDING.
- Ticket được gắn với mã nhân viên (staff_id) của người báo cáo.

## Luồng cơ bản
1. Người vận hành mở form Báo cáo sự cố.
2. Người vận hành chọn cơ sở, chọn vị trí (Khu vực chung hoặc Phòng cụ thể).
3. Người vận hành chọn phân loại sự cố và mức độ ưu tiên.
4. Người vận hành nhập mô tả chi tiết và tải lên ảnh chụp tại hiện trường.
5. Người vận hành nhấn "Gửi báo cáo".
6. Hệ thống lưu báo cáo, gán staff_id hiện tại làm người báo cáo và đặt trạng thái "PENDING".
7. Hệ thống báo thành công MSG014 (Báo cáo sự cố thành công) và điều hướng về danh sách sự cố.

## Luồng thay thế
- **AF1:** Tối ưu hóa ảnh → Hệ thống nén ảnh ở Frontend trước khi gọi API để tiết kiệm băng thông.
- **AF2:** Thiếu thông tin bắt buộc → Hệ thống báo lỗi dưới các trường còn thiếu (Cơ sở, Vị trí, Phân loại, Mô tả) và chặn gửi form.

## Ngoại lệ
- **EX1:** Tệp đính kèm quá lớn hoặc sai định dạng → MSG015 (Tệp không hợp lệ hoặc quá dung lượng).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Thường xuyên
- **Quy tắc nghiệp vụ:** Bắt buộc nhập vị trí và có ảnh hiện trường. Hệ thống sử dụng PreparedStatement để chống SQL Injection.

## Thông tin khác
- Giảm thiểu việc báo cáo sự cố thủ công qua các app nhắn tin như Zalo, Messenger.

## Giả định
- Người vận hành sử dụng thiết bị di động có kết nối mạng và tính năng chụp ảnh.

---

# Đặc tả Use Case: UC-11 – Báo cáo thống kê (Reporting)

**ID và Tên:** UC-11 – Báo cáo thống kê  
**Tác nhân chính:** Quản trị viên, Quản lý

## Mô tả
Quản lý tạo và xem các báo cáo phân tích liên quan đến vận hành khu trọ, như báo cáo tài chính (doanh thu, nợ đọng), tỷ lệ lấp đầy phòng, và phân tích bảo trì.

## Kích hoạt
Quản lý truy cập mục "Báo cáo" hoặc "Phân tích".

## Điều kiện tiên quyết
- Quản lý đã đăng nhập với quyền hạn phù hợp.

## Hậu điều kiện
- Báo cáo được tạo và hiển thị hoặc cho phép tải về.

## Luồng cơ bản
1. Quản lý chọn loại báo cáo muốn xem.
2. Quản lý thiết lập các tham số (vd: Khoảng thời gian, Khu nhà, Danh mục báo cáo).
3. Quản lý nhấn "Tạo báo cáo".
4. Hệ thống tổng hợp và xử lý dữ liệu.
5. Hệ thống hiển thị báo cáo dưới dạng bảng và biểu đồ trên màn hình.
6. Quản lý có thể chọn "Xuất file" (PDF/Excel) để tải về.

## Luồng thay thế
- **AF1:** Tập dữ liệu rỗng → Hệ thống báo MSG016 (Không có dữ liệu cho tiêu chí đã chọn).

## Ngoại lệ
- **EX1:** Quá thời gian tổng hợp dữ liệu lớn → MSG017 (Tạo báo cáo quá hạn thời gian chờ, vui lòng thu hẹp tiêu chí tìm kiếm).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Trung bình
- **Tần suất sử dụng:** Hàng tuần/Hàng tháng
- **Quy tắc nghiệp vụ:** Quyền truy cập báo cáo tài chính có thể bị giới hạn ở cấp Admin cao nhất.

## Thông tin khác
- Rất quan trọng cho việc ra quyết định kinh doanh và kiểm toán.

## Giả định
- Dữ liệu trong hệ thống được cập nhật theo thời gian thực.

---

# Đặc tả Use Case: UC-12 – Xem thông báo (View Notification)

**ID và Tên:** UC-12 – Xem thông báo (Hệ thống & Công việc)  
**Tác nhân chính:** Người vận hành (Operator)

## Mô tả
Cho phép Người vận hành xem các cảnh báo hệ thống và thông báo phân công công việc từ Quản trị viên/Quản lý. Các thông báo này tập trung vào việc báo tin khi có sự cố mới được giao, cập nhật từ hệ thống, hoặc thông báo khẩn cấp.

## Kích hoạt
Người vận hành nhấn vào biểu tượng "Thông báo" (hình cái chuông) trên thanh tiêu đề.

## Điều kiện tiên quyết
- Người vận hành đã đăng nhập.

## Hậu điều kiện
- Các thông báo được đánh dấu là đã đọc.
- Người vận hành nắm được nhiệm vụ mới được giao và các cảnh báo hệ thống.

## Luồng cơ bản
1. Người vận hành nhấn biểu tượng Thông báo.
2. Hệ thống hiển thị danh sách các thông báo công việc và hệ thống gần nhất, sắp xếp theo thời gian.
3. Thông báo chưa đọc được làm nổi bật.
4. Người vận hành nhấn vào một thông báo cụ thể (vd: "Bạn được giao xử lý sự cố điện").
5. Hệ thống đánh dấu thông báo là "Đã đọc".
6. Hệ thống chuyển hướng Người vận hành đến trang liên quan (vd: trang Chi tiết yêu cầu).

## Luồng thay thế
- **AF1:** Đánh dấu tất cả là đã đọc → Người vận hành nhấn "Đánh dấu tất cả đã đọc", hệ thống cập nhật toàn bộ trạng thái.

## Ngoại lệ
- **EX1:** Thông báo trỏ đến công việc đã bị xóa hoặc giao cho người khác → MSG018 (Công việc liên kết với thông báo này không còn tồn tại hoặc đã thay đổi).

## Thông tin bổ sung
- **Mức độ ưu tiên:** Cao
- **Tần suất sử dụng:** Rất thường xuyên
- **Quy tắc nghiệp vụ:** Thông báo của Operator mang thiên hướng hệ thống và điều phối công việc từ Admin, không bao gồm các thông báo hóa đơn hay tiền phòng.

## Thông tin khác
- Đảm bảo Người vận hành nhận nhiệm vụ kịp thời và không bỏ sót công việc.

## Giả định
- Hệ thống có cơ chế đẩy thông báo theo thời gian thực (real-time) đến thiết bị của Operator.
