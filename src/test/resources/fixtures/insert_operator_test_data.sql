-- 1. Insert Users
SET IDENTITY_INSERT dbo.users ON;
INSERT INTO dbo.users (user_id, username, password_hash, role, full_name, email, phone, status, force_change_pass, created_at, updated_at) 
VALUES (9991, 'test_manager', 'hash', 'MANAGER', 'Test Manager', 'mgr@test.com', '0123456789', 'ACTIVE', 0, GETDATE(), GETDATE());

INSERT INTO dbo.users (user_id, username, password_hash, role, full_name, email, phone, status, force_change_pass, created_at, updated_at) 
VALUES (9992, 'test_operator', 'hash', 'OPERATOR', 'Test Operator', 'op@test.com', '0123456788', 'ACTIVE', 0, GETDATE(), GETDATE());

INSERT INTO dbo.users (user_id, username, password_hash, role, full_name, email, phone, status, force_change_pass, created_at, updated_at) 
VALUES (9993, 'test_tenant', 'hash', 'TENANT', 'Test Tenant', 'tenant@test.com', '0123456787', 'ACTIVE', 0, GETDATE(), GETDATE());
SET IDENTITY_INSERT dbo.users OFF;

-- 2. Insert Facility
SET IDENTITY_INSERT dbo.facilities ON;
INSERT INTO dbo.facilities (facility_id, code, name, address, floor_count, rooms_per_floor, status, manager_id, operator_id, electricity_price, water_price, internet_fee, service_fee)
VALUES (9991, 'F_TEST', 'Test Facility', '123 Test St', 5, 10, 'ACTIVE', 9991, 9992, 3500, 25000, 100000, 50000);
SET IDENTITY_INSERT dbo.facilities OFF;

-- 3. Insert Room
SET IDENTITY_INSERT dbo.rooms ON;
INSERT INTO dbo.rooms (room_id, facility_id, code, area, status, tenant_id, deposit_amount, room_fee, created_at, updated_at)
VALUES (9991, 9991, 'R_TEST_101', 25.0, 'OCCUPIED', 9993, 1000000, 3000000, GETDATE(), GETDATE());
SET IDENTITY_INSERT dbo.rooms OFF;

-- 4. Insert Request (Incident)
SET IDENTITY_INSERT dbo.requests ON;
INSERT INTO dbo.requests (request_id, code, sender_id, category, title, content, status, assigned_staff_id, created_at, updated_at)
VALUES (9991, 'REQ_TEST_01', 9993, 'Incident', 'Broken light', 'Light in bathroom is broken', 'PENDING', 9992, GETDATE(), GETDATE());
SET IDENTITY_INSERT dbo.requests OFF;

-- 5. Insert Meter Reading
SET IDENTITY_INSERT dbo.meter_readings ON;
INSERT INTO dbo.meter_readings (meter_id, room_id, electric, water, reading_date, status, created_by, created_at, updated_at)
VALUES (9991, 9991, 100, 10, '2026-06-30', 'UPDATED', 9992, GETDATE(), GETDATE());
SET IDENTITY_INSERT dbo.meter_readings OFF;
