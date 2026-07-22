import csv
import os

file_path = r"d:\Semester 5\SWP391\HostelManagement-main-a\HostelManagement-main-a (1) (1)\sheet-test.csv"

test_cases = [
    ["Test Case ID", "Feature", "Scenario", "Expected Result"],
    ["TC_CC_01", "Tạo hợp đồng", "Tạo hợp đồng thành công với đầy đủ thông tin hợp lệ", "Hệ thống lưu hợp đồng trạng thái ACTIVE, mã tự sinh đúng định dạng, lưu thông tin người tạo."],
    ["TC_CC_02", "Tạo hợp đồng", "Tạo hợp đồng nhưng bỏ trống Họ tên khách thuê", "Hệ thống từ chối tạo và hiển thị lỗi TENANT_NAME_REQUIRED."],
    ["TC_CC_03", "Tạo hợp đồng", "Tạo hợp đồng nhưng bỏ trống số CMND/CCCD", "Hệ thống từ chối tạo và hiển thị lỗi TENANT_IDENTITY_REQUIRED."],
    ["TC_CC_04", "Tạo hợp đồng", "Tạo hợp đồng nhưng chưa chọn phòng thuê", "Hệ thống từ chối tạo và hiển thị lỗi ROOM_REQUIRED."],
    ["TC_CC_05", "Tạo hợp đồng", "Tạo hợp đồng nhưng bỏ trống Ngày lập hợp đồng", "Hệ thống từ chối tạo và hiển thị lỗi SIGNED_DATE_REQUIRED."],
    ["TC_CC_06", "Tạo hợp đồng", "Tạo hợp đồng nhưng bỏ trống Ngày hết hạn hợp đồng", "Hệ thống từ chối tạo và hiển thị lỗi END_DATE_REQUIRED."],
    ["TC_CC_07", "Tạo hợp đồng", "Ngày hết hạn hợp đồng nhỏ hơn Ngày lập hợp đồng", "Hệ thống từ chối tạo và hiển thị lỗi INVALID_CONTRACT_DATE."],
    ["TC_CC_08", "Tạo hợp đồng", "Chọn phòng không tồn tại trong hệ thống", "Hệ thống hiển thị lỗi ROOM_NOT_FOUND (HTTP 404)."],
    ["TC_CC_09", "Tạo hợp đồng", "Chọn phòng thuộc cơ sở mà Ban quản lý không có quyền quản lý", "Hệ thống từ chối và hiển thị lỗi ROOM_ACCESS_DENIED (HTTP 403)."],
    ["TC_CC_10", "Tạo hợp đồng", "Chọn phòng đang có một hợp đồng khác ở trạng thái ACTIVE", "Hệ thống từ chối và hiển thị lỗi ROOM_ALREADY_HAS_ACTIVE_CONTRACT (HTTP 400)."],
    ["TC_CC_11", "Tạo hợp đồng", "Không nhập Ngày bắt đầu hợp đồng", "Hệ thống tự động lấy Ngày bắt đầu bằng Ngày lập hợp đồng (signed_date)."],
    ["TC_CC_12", "Tạo hợp đồng", "Tạo hợp đồng thành công và kiểm tra thông tin snapshot của phòng", "Hệ thống lưu cứng giá phòng, giá cọc tại thời điểm tạo hợp đồng để tránh sai lệch giá sau này."],
    ["TC_CC_13", "Tạo hợp đồng", "Kiểm tra định dạng mã hợp đồng tự sinh", "Mã hợp đồng phải đúng định dạng HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}."]
]

with open(file_path, mode='w', encoding='utf-8-sig', newline='') as file:
    writer = csv.writer(file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    writer.writerows(test_cases)

print(f"Successfully written test cases to {file_path}")
