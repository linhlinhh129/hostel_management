USE HostelManagement;
GO


-- ============================================================
-- Hostel Management System - Seed Data for Testing (Đã sửa lỗi đồng bộ)
-- SQL Server 2022
-- ============================================================


SET NOCOUNT ON;
GO


BEGIN TRY
    BEGIN TRANSACTION;


    -- ============================================================
    -- 1. USERS
    -- Roles: ADMIN, MANAGER, OPERATOR, TENANT
    -- ============================================================
    DECLARE @pwd NVARCHAR(255) = '$2a$10$IrTJt3qW7TtGHeOvRHYUsehGxBK4fgR.ywsmhP5SEs3RjbRXP3WVy';


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'mquoc1202@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'mquoc1202@gmail.com', @pwd, N'ADMIN', N'Đoàn Minh Quốc', N'mquoc1202@gmail.com', N'0916355559', N'ACTIVE', NULL, 0, N'001201000201', '2003-01-12', N'MALE', N'Hà Tĩnh');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'mn03112005@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'mn03112005@gmail.com', @pwd, N'MANAGER', N'Nguyễn Minh Nhật', N'mn03112005@gmail.com', N'0900000002', N'ACTIVE', NULL, 1, N'001241000001', '2001-02-15', N'FEMALE', N'Cầu Giấy, Hà Nội');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'buidinhyt@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'buidinhyt@gmail.com', @pwd, N'MANAGER', N'Bùi Đỉnh', N'buidinhyt@gmail.com', N'0862158523', N'ACTIVE', NULL, 1, N'011201000001', '2005-10-27', N'MALE', N'Ninh Bình');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'atu02378@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'atu02378@gmail.com', @pwd, N'OPERATOR', N'Phạm Anh Tú (Atus)', N'atu02378@gmail.com', N'0900000004', N'ACTIVE', NULL, 1, N'001201050001', '2005-01-15', N'MALE', N'Mê Linh, Hà Nội');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'huynguyenn1108@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'huynguyenn1108@gmail.com', @pwd, N'OPERATOR', N'Nguyễn Công Huy', N'huynguyenn1108@gmail.com', N'0900000005', N'ACTIVE', NULL, 1, N'001201000301', '2006-01-15', N'FEMALE', N'Đống Đa, Hà Nội');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'lethithuylinhtl12@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'lethithuylinhtl12@gmail.com', @pwd, N'TENANT', N'Lê Thị Thuỳ Linh', N'lethithuylinhtl12@gmail.com', N'0981032853', N'ACTIVE', NULL, 0, N'001201000001', '2005-09-12', N'FEMALE', N'Ý Yên, Nam Định');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'dov62995@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, avatar_url, force_change_pass, identity_number, dob, gender, permanent_address)
    VALUES (N'dov62995@gmail.com', @pwd, N'TENANT', N'Đỗ Danh Việt', N'dov62995@gmail.com', N'0922222222', N'ACTIVE', NULL, 0, N'001202000002', '2002-03-20', N'MALE', N'Gia Lộc, Hải Dương');


    -- Thêm các Tenant thiếu để map dữ liệu phía dưới
    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'tenant03@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address)
    VALUES (N'tenant03@gmail.com', @pwd, N'TENANT', N'Lê Minh Triết', N'tenant03@gmail.com', N'0933333333', N'ACTIVE', N'001203000003', '2003-05-12', N'MALE', N'Hà Đông, Hà Nội');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'tenant04@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address)
    VALUES (N'tenant04@gmail.com', @pwd, N'TENANT', N'Phạm Hải Yến', N'tenant04@gmail.com', N'0944444444', N'ACTIVE', N'001204000004', '2004-08-22', N'FEMALE', N'Thanh Xuân, Hà Nội');


    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'tenant05@gmail.com')
    INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address)
    VALUES (N'tenant05@gmail.com', @pwd, N'TENANT', N'Hoàng Trung Kiên', N'tenant05@gmail.com', N'0955555555', N'ACTIVE', N'001205000005', '2001-11-30', N'MALE', N'Đống Đa, Hà Nội');


    -- Lưu User IDs vào biến tạm
    DECLARE @admin_id      INT = (SELECT user_id FROM dbo.users WHERE username = N'mquoc1202@gmail.com');
    DECLARE @manager01_id  INT = (SELECT user_id FROM dbo.users WHERE username = N'mn03112005@gmail.com');
    DECLARE @manager02_id  INT = (SELECT user_id FROM dbo.users WHERE username = N'buidinhyt@gmail.com');
    DECLARE @operator01_id INT = (SELECT user_id FROM dbo.users WHERE username = N'atu02378@gmail.com');
    DECLARE @operator02_id INT = (SELECT user_id FROM dbo.users WHERE username = N'huynguyenn1108@gmail.com');
    DECLARE @tenant01_id   INT = (SELECT user_id FROM dbo.users WHERE username = N'lethithuylinhtl12@gmail.com');
    DECLARE @tenant02_id   INT = (SELECT user_id FROM dbo.users WHERE username = N'dov62995@gmail.com');
    DECLARE @tenant03_id   INT = (SELECT user_id FROM dbo.users WHERE username = N'tenant03@gmail.com');
    DECLARE @tenant04_id   INT = (SELECT user_id FROM dbo.users WHERE username = N'tenant04@gmail.com');
    DECLARE @tenant05_id   INT = (SELECT user_id FROM dbo.users WHERE username = N'tenant05@gmail.com');


    -- ============================================================
    -- 2. FACILITIES
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.facilities WHERE code = N'CG')
    INSERT INTO dbo.facilities (code, name, address, floor_count, rooms_per_floor, status, manager_id, operator_id, electricity_price, water_price, internet_fee, service_fee)
    VALUES (N'CG', N'Ký túc xá Cầu Giấy', N'Số 10 Trần Thái Tông, Cầu Giấy, Hà Nội', 2, 2, N'ACTIVE', @manager01_id, NULL, 4000, 30000, 100000, 50000);


    IF NOT EXISTS (SELECT 1 FROM dbo.facilities WHERE code = N'MD')
    INSERT INTO dbo.facilities (code, name, address, floor_count, rooms_per_floor, status, manager_id, operator_id, electricity_price, water_price, internet_fee, service_fee)
    VALUES (N'MD', N'Nhà trọ Mỹ Đình', N'Số 25 Lê Đức Thọ, Nam Từ Liêm, Hà Nội', 2, 2, N'ACTIVE', @manager02_id, NULL, 4200, 32000, 120000, 60000);


    IF NOT EXISTS (SELECT 1 FROM dbo.facilities WHERE code = N'HM')
    INSERT INTO dbo.facilities (code, name, address, floor_count, rooms_per_floor, status, manager_id, operator_id, electricity_price, water_price, internet_fee, service_fee)
    VALUES (N'HM', N'Nhà trọ Hoàng Mai', N'Số 5 Giải Phóng, Hoàng Mai, Hà Nội', 3, 5, N'DRAFT', NULL, NULL, 4100, 31000, 110000, 55000);


    DECLARE @facility_cg INT = (SELECT facility_id FROM dbo.facilities WHERE code = N'CG');
    DECLARE @facility_md INT = (SELECT facility_id FROM dbo.facilities WHERE code = N'MD');
    DECLARE @facility_hm INT = (SELECT facility_id FROM dbo.facilities WHERE code = N'HM');


    -- ============================================================
    -- 3. ROOMS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'CG0101')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_cg, N'CG0101', 22.50, N'OCCUPIED', @tenant01_id, 2000000, '2026-01-01', '2026-12-31', 2500000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'CG0102')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_cg, N'CG0102', 24.00, N'OCCUPIED', @tenant02_id, 2000000, '2026-02-01', '2027-01-31', 2700000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'CG0103')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_cg, N'CG0103', 20.00, N'AVAILABLE', NULL, 0, NULL, NULL, 2300000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'CG0104')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_cg, N'CG0104', 26.00, N'AVAILABLE', NULL, 0, NULL, NULL, 2800000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'MD0201')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_md, N'MD0201', 21.50, N'OCCUPIED', @tenant03_id, 1800000, '2026-03-01', '2027-02-28', 2400000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'MD0202')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_md, N'MD0202', 23.00, N'OCCUPIED', @tenant04_id, 1800000, '2026-04-01', '2027-03-31', 2600000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'MD0203')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_md, N'MD0203', 25.00, N'OCCUPIED', @tenant05_id, 1800000, '2026-05-01', '2027-04-30', 2800000);


    IF NOT EXISTS (SELECT 1 FROM dbo.rooms WHERE code = N'MD0204')
    INSERT INTO dbo.rooms (facility_id, code, area, status, tenant_id, deposit_amount, contract_start_date, contract_end_date, room_fee)
    VALUES (@facility_md, N'MD0204', 19.50, N'AVAILABLE', NULL, 0, NULL, NULL, 2200000);


    DECLARE @room_cg0101 INT = (SELECT room_id FROM dbo.rooms WHERE code = N'CG0101');
    DECLARE @room_cg0102 INT = (SELECT room_id FROM dbo.rooms WHERE code = N'CG0102');
    DECLARE @room_md0201 INT = (SELECT room_id FROM dbo.rooms WHERE code = N'MD0201');
    DECLARE @room_md0202 INT = (SELECT room_id FROM dbo.rooms WHERE code = N'MD0202');
    DECLARE @room_md0203 INT = (SELECT room_id FROM dbo.rooms WHERE code = N'MD0203');


    -- ============================================================
    -- 4. DEPENDENTS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.dependents WHERE tenant_id = @tenant01_id AND full_name = N'Nguyễn Thị Hoa')
    INSERT INTO dbo.dependents (tenant_id, full_name, dob, gender, relationship, phone, identity_number, permanent_address)
    VALUES (@tenant01_id, N'Nguyễn Thị Hoa', '2003-09-10', N'FEMALE', N'Em gái', N'0961111111', N'001203100001', N'Nam Sách, Hải Dương');


    IF NOT EXISTS (SELECT 1 FROM dbo.dependents WHERE tenant_id = @tenant02_id AND full_name = N'Trần Văn Nam')
    INSERT INTO dbo.dependents (tenant_id, full_name, dob, gender, relationship, phone, identity_number, permanent_address)
    VALUES (@tenant02_id, N'Trần Văn Nam', '2001-12-05', N'MALE', N'Anh trai', N'0962222222', N'001201200002', N'Gia Lộc, Hải Dương');


    -- ============================================================
    -- 5. METER READINGS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_cg0101 AND reading_date = '2026-05-31')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_cg0101, 120, 35, '2026-05-31', N'UPDATED', @operator01_id, N'https://example.com/water/CG0101-202605.jpg', N'https://example.com/electric/CG0101-202605.jpg');


    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_cg0101 AND reading_date = '2026-06-30')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_cg0101, 168, 42, '2026-06-30', N'UPDATED', @operator01_id, N'https://example.com/water/CG0101-202606.jpg', N'https://example.com/electric/CG0101-202606.jpg');


    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_cg0102 AND reading_date = '2026-06-30')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_cg0102, 210, 60, '2026-06-30', N'UPDATED', @operator01_id, N'https://example.com/water/CG0102-202606.jpg', N'https://example.com/electric/CG0102-202606.jpg');


    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_md0201 AND reading_date = '2026-06-30')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_md0201, 145, 39, '2026-06-30', N'UPDATED', @operator01_id, N'https://example.com/water/MD0201-202606.jpg', N'https://example.com/electric/MD0201-202606.jpg');


    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_md0202 AND reading_date = '2026-06-30')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_md0202, 190, 50, '2026-06-30', N'UPDATED', @operator01_id, N'https://example.com/water/MD0202-202606.jpg', N'https://example.com/electric/MD0202-202606.jpg');


    IF NOT EXISTS (SELECT 1 FROM dbo.meter_readings WHERE room_id = @room_md0203 AND reading_date = '2026-06-30')
    INSERT INTO dbo.meter_readings (room_id, electric, water, reading_date, status, created_by, water_img, electric_img)
    VALUES (@room_md0203, 175, 45, '2026-06-30', N'UPDATED', @operator01_id, N'https://example.com/water/MD0203-202606.jpg', N'https://example.com/electric/MD0203-202606.jpg');


    DECLARE @meter_cg0101_0630 INT = (SELECT meter_id FROM dbo.meter_readings WHERE room_id = @room_cg0101 AND reading_date = '2026-06-30');
    DECLARE @meter_cg0102_0630 INT = (SELECT meter_id FROM dbo.meter_readings WHERE room_id = @room_cg0102 AND reading_date = '2026-06-30');
    DECLARE @meter_md0201_0630 INT = (SELECT meter_id FROM dbo.meter_readings WHERE room_id = @room_md0201 AND reading_date = '2026-06-30');
    DECLARE @meter_md0202_0630 INT = (SELECT meter_id FROM dbo.meter_readings WHERE room_id = @room_md0202 AND reading_date = '2026-06-30');
    DECLARE @meter_md0203_0630 INT = (SELECT meter_id FROM dbo.meter_readings WHERE room_id = @room_md0203 AND reading_date = '2026-06-30');


    -- ============================================================
    -- 6. INVOICES
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.invoices WHERE code = N'INV-CG0101-202606')
    INSERT INTO dbo.invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by)
    VALUES (N'INV-CG0101-202606', @room_cg0101, @meter_cg0101_0630, '2026-07-05', N'UNPAID', 0, 0, 2500000, 4000, 30000, 100000, 50000, 3052000, N'Hóa đơn tháng 06/2026 phòng CG0101', @manager01_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.invoices WHERE code = N'INV-CG0102-202606')
    INSERT INTO dbo.invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by)
    VALUES (N'INV-CG0102-202606', @room_cg0102, @meter_cg0102_0630, '2026-07-05', N'PAID', 0, 20000, 2700000, 4000, 30000, 100000, 50000, 5510000, N'Hóa đơn tháng 06/2026 phòng CG0102', @manager01_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.invoices WHERE code = N'INV-MD0201-202606')
    INSERT INTO dbo.invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by)
    VALUES (N'INV-MD0201-202606', @room_md0201, @meter_md0201_0630, '2026-07-05', N'PAID', 0, 0, 2400000, 4200, 32000, 120000, 60000, 4437000, N'Hóa đơn tháng 06/2026 phòng MD0201', @manager01_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.invoices WHERE code = N'INV-MD0202-202606')
    INSERT INTO dbo.invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by)
    VALUES (N'INV-MD0202-202606', @room_md0202, @meter_md0202_0630, '2026-07-05', N'UNPAID', 0, 50000, 2600000, 4200, 32000, 120000, 60000, 5228000, N'Hóa đơn tháng 06/2026 phòng MD0202', @manager01_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.invoices WHERE code = N'INV-MD0203-202606')
    INSERT INTO dbo.invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by)
    VALUES (N'INV-MD0203-202606', @room_md0203, @meter_md0203_0630, '2026-07-05', N'UNPAID', 0, 0, 2800000, 4200, 32000, 120000, 60000, 5155000, N'Hóa đơn tháng 06/2026 phòng MD0203', @manager01_id);


    -- ============================================================
    -- 7. PAYMENTS
    -- ============================================================
    DECLARE @invoice_id INT = (SELECT invoice_id FROM dbo.invoices WHERE code = N'INV-CG0102-202606');
    DECLARE @invoice_id2 INT = (SELECT invoice_id FROM dbo.invoices WHERE code = N'INV-MD0201-202606');
    DECLARE @invoice_id3 INT = (SELECT invoice_id FROM dbo.invoices WHERE code = N'INV-MD0202-202606');


    DECLARE @payment_amount INT = (SELECT total_amount FROM dbo.invoices WHERE invoice_id = @invoice_id);
    DECLARE @payment_amount2 INT = (SELECT total_amount FROM dbo.invoices WHERE invoice_id = @invoice_id2);
    DECLARE @payment_amount3 INT = (SELECT total_amount FROM dbo.invoices WHERE invoice_id = @invoice_id3);


    IF NOT EXISTS (SELECT 1 FROM dbo.payments WHERE code = N'PAY-CG0102-202606')
    INSERT INTO dbo.payments (code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_by)
    VALUES (N'PAY-CG0102-202606', @invoice_id, @room_cg0102, N'SUCCESS', '2026-07-02', N'BANK_TRANSFER', @payment_amount, @tenant02_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.payments WHERE code = N'PAY-MD0201-202606')
    INSERT INTO dbo.payments (code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_by)
    VALUES (N'PAY-MD0201-202606', @invoice_id2, @room_md0201, N'SUCCESS', '2026-07-01', N'CASH', @payment_amount2, @tenant03_id);


    IF NOT EXISTS (SELECT 1 FROM dbo.payments WHERE code = N'PAY-MD0202-PARTIAL')
    INSERT INTO dbo.payments (code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_by)
    VALUES (N'PAY-MD0202-PARTIAL', @invoice_id3, @room_md0202, N'PENDING', '2026-07-06', N'BANK_TRANSFER', @payment_amount3, @tenant04_id);


    -- ============================================================
    -- 8. REQUESTS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.requests WHERE code = N'REQ-CG0101-001')
    INSERT INTO dbo.requests (code, sender_id, category, title, content, status, attachment_urls1, attachment_urls2, assigned_staff_id, rejection_reason)
    VALUES (N'REQ-CG0101-001', @tenant01_id, N'MAINTENANCE', N'Sửa bóng đèn phòng', N'Bóng đèn phòng CG0101 bị hỏng, cần thay mới.', N'PENDING', N'https://example.com/request/req001-1.jpg', NULL, @operator01_id, NULL);


    IF NOT EXISTS (SELECT 1 FROM dbo.requests WHERE code = N'REQ-CG0102-001')
    INSERT INTO dbo.requests (code, sender_id, category, title, content, status, attachment_urls1, attachment_urls2, assigned_staff_id, rejection_reason)
    VALUES (N'REQ-CG0102-001', @tenant02_id, N'CLEANING', N'Yêu cầu vệ sinh hành lang', N'Hành lang tầng 1 có rác, cần dọn vệ sinh.', N'IN_PROGRESS', NULL, NULL, @operator01_id, NULL);


    IF NOT EXISTS (SELECT 1 FROM dbo.requests WHERE code = N'REQ-MD0202-001')
    INSERT INTO dbo.requests (code, sender_id, category, title, content, status, attachment_urls1, attachment_urls2, assigned_staff_id, rejection_reason)
    VALUES (N'REQ-MD0202-001', @tenant04_id, N'COMPLAINT', N'Phản ánh tiếng ồn', N'Phòng bên cạnh gây tiếng ồn sau 23h.', N'REJECTED', NULL, NULL, @manager02_id, N'Không đủ bằng chứng xác minh.');


    -- ============================================================
    -- 9. NOTIFICATIONS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.notifications WHERE code = N'NTF-ALL-001')
    INSERT INTO dbo.notifications (code, title, content, target_type, facility_id, room_id, status, created_by, sent_at)
    VALUES (N'NTF-ALL-001', N'Thông báo bảo trì hệ thống', N'Hệ thống sẽ bảo trì từ 22h đến 23h ngày 30/06/2026.', N'ALL', NULL, NULL, N'SENT', @admin_id, '2026-06-28 09:00:00');


    IF NOT EXISTS (SELECT 1 FROM dbo.notifications WHERE code = N'NTF-FAC-001')
    INSERT INTO dbo.notifications (code, title, content, target_type, facility_id, room_id, status, created_by, sent_at)
    VALUES (N'NTF-FAC-001', N'Thông báo cắt nước', N'Cơ sở Cầu Giấy tạm cắt nước từ 8h đến 10h ngày 01/07/2026.', N'FACILITY', @facility_cg, NULL, N'SENT', @manager01_id, '2026-06-29 08:30:00');


    -- ============================================================
    -- 10. AUDIT LOGS
    -- ============================================================
    IF NOT EXISTS (SELECT 1 FROM dbo.audit_logs WHERE entity_type = N'ROOM' AND entity_id = @room_cg0101 AND action = N'CREATE_ROOM')
    INSERT INTO dbo.audit_logs (entity_type, entity_id, action, old_value, new_value, ip_address, comment, created_by)
    VALUES (N'ROOM', @room_cg0101, N'CREATE_ROOM', NULL, N'CG0101', N'127.0.0.1', N'Tạo dữ liệu phòng CG0101', @admin_id);


    COMMIT TRANSACTION;
    PRINT N'Seed data inserted successfully.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;


    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();


    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH;
GO


-- ============================================================
-- 11. CONTRACTS DATA (Đồng bộ chuẩn xác theo mã phòng mới)
-- ============================================================
INSERT INTO dbo.contracts (
    code, room_id, tenant_id, tenant_full_name, tenant_dob, tenant_permanent_address,
    tenant_identity_number, tenant_identity_issue_date, tenant_identity_issue_place, tenant_phone,
    amount_in_words, signed_date, start_date, end_date, status, created_by
)
SELECT
    v.contract_code, r.room_id, u.user_id, u.full_name, u.dob, u.permanent_address,
    u.identity_number, NULL, NULL, u.phone,
    v.amount_in_words, r.contract_start_date, r.contract_start_date, r.contract_end_date, N'ACTIVE', f.manager_id
FROM (VALUES
    (N'CG0101', N'lethithuylinhtl12@gmail.com', N'HD-CG0101-20260101-001', N'Hai triệu năm trăm nghìn đồng'),
    (N'CG0102', N'dov62995@gmail.com', N'HD-CG0102-20260201-001', N'Hai triệu bảy trăm nghìn đồng'),
    (N'MD0201', N'tenant03@gmail.com', N'HD-MD0201-20260301-001', N'Hai triệu bốn trăm nghìn đồng'),
    (N'MD0202', N'tenant04@gmail.com', N'HD-MD0202-20260401-001', N'Hai triệu sáu trăm nghìn đồng'),
    (N'MD0203', N'tenant05@gmail.com', N'HD-MD0203-20260501-001', N'Hai triệu tám trăm nghìn đồng')
) AS v(room_code, username, contract_code, amount_in_words)
JOIN dbo.rooms r ON r.code = v.room_code
JOIN dbo.users u ON u.username = v.username AND u.user_id = r.tenant_id
JOIN dbo.facilities f ON f.facility_id = r.facility_id
WHERE NOT EXISTS (
    SELECT 1
    FROM dbo.contracts c
    WHERE c.code = v.contract_code
);
GO


-- ============================================================
-- Quick check
-- ============================================================
SELECT 'users'          AS table_name, COUNT(*) AS total_rows FROM dbo.users
UNION ALL SELECT 'facilities',  COUNT(*) FROM dbo.facilities
UNION ALL SELECT 'rooms',       COUNT(*) FROM dbo.rooms
UNION ALL SELECT 'dependents',  COUNT(*) FROM dbo.dependents
UNION ALL SELECT 'meter_readings', COUNT(*) FROM dbo.meter_readings
UNION ALL SELECT 'invoices',    COUNT(*) FROM dbo.invoices
UNION ALL SELECT 'payments',    COUNT(*) FROM dbo.payments
UNION ALL SELECT 'requests',    COUNT(*) FROM dbo.requests
UNION ALL SELECT 'notifications', COUNT(*) FROM dbo.notifications
UNION ALL SELECT 'audit_logs',  COUNT(*) FROM dbo.audit_logs
UNION ALL SELECT 'contracts',  COUNT(*) FROM dbo.contracts;
GO


