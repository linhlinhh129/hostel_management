import pandas as pd
import os

file_path = r"d:\Semester 5\SWP391\HostelManagement-main-a\HostelManagement-main-a (1) (1)\sheet-test.xlsx"

test_cases = [
    {"Test Case ID": "TC_CC_01", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng thành công với đầy đủ thông tin hợp lệ", "Expected Result": "Hệ thống lưu hợp đồng trạng thái ACTIVE, mã tự sinh đúng định dạng, lưu thông tin người tạo."},
    {"Test Case ID": "TC_CC_02", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng nhưng bỏ trống Họ tên khách thuê", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi TENANT_NAME_REQUIRED."},
    {"Test Case ID": "TC_CC_03", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng nhưng bỏ trống số CMND/CCCD", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi TENANT_IDENTITY_REQUIRED."},
    {"Test Case ID": "TC_CC_04", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng nhưng chưa chọn phòng thuê", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi ROOM_REQUIRED."},
    {"Test Case ID": "TC_CC_05", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng nhưng bỏ trống Ngày lập hợp đồng", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi SIGNED_DATE_REQUIRED."},
    {"Test Case ID": "TC_CC_06", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng nhưng bỏ trống Ngày hết hạn hợp đồng", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi END_DATE_REQUIRED."},
    {"Test Case ID": "TC_CC_07", "Feature": "Tạo hợp đồng", "Scenario": "Ngày hết hạn hợp đồng nhỏ hơn Ngày lập hợp đồng", "Expected Result": "Hệ thống từ chối tạo và hiển thị lỗi INVALID_CONTRACT_DATE."},
    {"Test Case ID": "TC_CC_08", "Feature": "Tạo hợp đồng", "Scenario": "Chọn phòng không tồn tại trong hệ thống", "Expected Result": "Hệ thống hiển thị lỗi ROOM_NOT_FOUND (HTTP 404)."},
    {"Test Case ID": "TC_CC_09", "Feature": "Tạo hợp đồng", "Scenario": "Chọn phòng thuộc cơ sở mà Ban quản lý không có quyền quản lý", "Expected Result": "Hệ thống từ chối và hiển thị lỗi ROOM_ACCESS_DENIED (HTTP 403)."},
    {"Test Case ID": "TC_CC_10", "Feature": "Tạo hợp đồng", "Scenario": "Chọn phòng đang có một hợp đồng khác ở trạng thái ACTIVE", "Expected Result": "Hệ thống từ chối và hiển thị lỗi ROOM_ALREADY_HAS_ACTIVE_CONTRACT (HTTP 400)."},
    {"Test Case ID": "TC_CC_11", "Feature": "Tạo hợp đồng", "Scenario": "Không nhập Ngày bắt đầu hợp đồng", "Expected Result": "Hệ thống tự động lấy Ngày bắt đầu bằng Ngày lập hợp đồng (signed_date)."},
    {"Test Case ID": "TC_CC_12", "Feature": "Tạo hợp đồng", "Scenario": "Tạo hợp đồng thành công và kiểm tra thông tin snapshot của phòng", "Expected Result": "Hệ thống lưu cứng giá phòng, giá cọc tại thời điểm tạo hợp đồng để tránh sai lệch giá sau này."},
    {"Test Case ID": "TC_CC_13", "Feature": "Tạo hợp đồng", "Scenario": "Kiểm tra định dạng mã hợp đồng tự sinh", "Expected Result": "Mã hợp đồng phải đúng định dạng HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}."}
]

df_new = pd.DataFrame(test_cases)

if os.path.exists(file_path):
    with pd.ExcelWriter(file_path, engine="openpyxl", mode="a", if_sheet_exists="replace") as writer:
        df_new.to_excel(writer, sheet_name="Test Cases_TaoHopDong", index=False)
else:
    with pd.ExcelWriter(file_path, engine="openpyxl", mode="w") as writer:
        df_new.to_excel(writer, sheet_name="Test Cases_TaoHopDong", index=False)

print("Successfully written test cases to sheet-test.xlsx")
