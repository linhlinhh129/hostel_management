# Quickstart & Validation Guide: operator-utility-request-display

## Overview
This guide provides steps to manually validate that "Wrong Utility Number Reports" sent by Managers correctly display their mapped room and facility information on the Operator's UI, and that the `UTILITY` category is correctly localized to Vietnamese.

## Prerequisites
- System deployed locally via `mvn clean package` and `Tomcat` OR running via your configured IDE standard run process.
- Database configured with at least one Manager, one Facility, one Room, one Operator, and one Invoice with status `INCORRECT`.

## Scenario 1: Creating a utility report as a Manager
1. Login with a Manager account.
2. Navigate to `Hóa đơn` -> `Chờ kiểm tra` (or equivalent tab to find `INCORRECT` utility invoices).
3. Click "Gửi báo cáo lỗi" (Send error report) to an Operator.
4. Verify that the form submits successfully.

## Scenario 2: Validating Operator's Request List UI
1. Logout as Manager and Login as the assigned Operator.
2. Navigate to `Yêu cầu sửa chữa` -> Danh sách yêu cầu.
3. Validate that the newly created report is in the list.
4. **Validation Point 1**: Check the "Thể loại" (Category) column. It should show `SỰ CỐ ĐIỆN NƯỚC` instead of `UTILITY`.
5. **Validation Point 2**: Check the "Phòng" (Room) column. It should show the correct Room Code and Facility Name instead of a dash (`—`).
6. **Validation Point 3**: In the filter dropdown at the top, select the "Thể loại" filter. Verify `Điện nước` is an available option.

## Scenario 3: Validating Operator's Request Detail UI
1. Click on the row of the newly created report to view its details.
2. **Validation Point 4**: Check the "Phòng / Cơ sở" metadata row. It should show `P.[RoomCode] — [FacilityName]` instead of `P. — [FacilityName]`.

If all validation points pass, the feature works end-to-end.
