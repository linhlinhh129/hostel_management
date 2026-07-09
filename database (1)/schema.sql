-- ============================================================
-- Hostel Management System - Database Schema
-- SQL Server 2022
-- Version: fixed syntax + named primary IDs
-- ============================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'HostelManagement')
BEGIN
    CREATE DATABASE HostelManagement;
END
GO

USE HostelManagement;
GO

-- ============================================================
-- 1. USERS TABLE
-- (Combines auth, user profile, tenant details, and user_facilities)
-- ============================================================
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        user_id             INT IDENTITY(1,1)   PRIMARY KEY,
        username            NVARCHAR(50)        NOT NULL UNIQUE,
        password_hash       NVARCHAR(255)       NOT NULL,
        role                NVARCHAR(20)        NOT NULL DEFAULT 'TENANT',  -- ADMIN, MANAGER, OPERATOR, TENANT
        full_name           NVARCHAR(100)       NOT NULL,
        email               NVARCHAR(100)       NULL,
        phone               NVARCHAR(20)        NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'ACTIVE',
        avatar_url          NVARCHAR(500)       NULL,
        force_change_pass   BIT                 NOT NULL DEFAULT 1,
        -- Tenant Extensions
        identity_number     NVARCHAR(50)        NULL,
        dob                 DATE                NULL,
        gender              NVARCHAR(20)        NULL,
        permanent_address   NVARCHAR(500)       NULL,

        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL
    );
END
GO

-- ============================================================
-- 2. FACILITIES TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.facilities', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.facilities (
        facility_id         INT IDENTITY(1,1)   PRIMARY KEY,
        code                NVARCHAR(50)        NOT NULL UNIQUE,
        name                NVARCHAR(200)       NOT NULL,
        address             NVARCHAR(500)       NOT NULL,
        floor_count         INT                 NOT NULL,
		rooms_per_floor     INT                 NOT NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'DRAFT', -- DRAFT, ACTIVE, INACTIVE
        manager_id          INT                 NULL,
		operator_id         INT                 NULL,   -- Nhân viên vận hành (OPERATOR)
        electricity_price   DECIMAL(10,2)       NULL,
        water_price         DECIMAL(10,2)       NULL,
        internet_fee        DECIMAL(10,2)       NULL,
        service_fee         DECIMAL(10,2)       NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_facilities_users_manager
            FOREIGN KEY (manager_id) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 3. ROOMS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.rooms', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.rooms (
        room_id             INT IDENTITY(1,1)   PRIMARY KEY,-- HN0103
        facility_id         INT                 NOT NULL,
        code                NVARCHAR(50)        NOT NULL UNIQUE,
        area                DECIMAL(10,2)       NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED, INACTIVE
        tenant_id           INT                 NULL,
        deposit_amount      DECIMAL(18,2)       NOT NULL DEFAULT 0, -- tiền cọc
        contract_start_date DATE                NULL,
        contract_end_date   DATE                NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,
        room_fee            DECIMAL(18,2)       NOT NULL,

        CONSTRAINT FK_rooms_facilities
            FOREIGN KEY (facility_id) REFERENCES dbo.facilities(facility_id),
        CONSTRAINT FK_rooms_users_tenant
            FOREIGN KEY (tenant_id) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 4. DEPENDENTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.dependents', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.dependents (
        dependent_id        INT IDENTITY(1,1)   PRIMARY KEY,
        tenant_id           INT                 NOT NULL,
        full_name           NVARCHAR(100)       NOT NULL,
        dob                 DATE                NULL,
        gender              NVARCHAR(20)        NULL,
        relationship        NVARCHAR(50)        NULL,
        phone               NVARCHAR(20)        NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,
        identity_number     NVARCHAR(50)        NULL,
        permanent_address   NVARCHAR(500)       NULL,

        CONSTRAINT FK_dependents_users_tenant
            FOREIGN KEY (tenant_id) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 5. METER READINGS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.meter_readings', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.meter_readings (
        meter_id            INT IDENTITY(1,1)   PRIMARY KEY,
        room_id             INT                 NOT NULL,
        electric            INT                 NOT NULL,
        water               INT                 NOT NULL,
        reading_date        DATE                NOT NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'UPDATED',
        created_by          INT                 NULL, -- Nhân viên chốt số
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,
        water_img           NVARCHAR(500)       NULL,
        electric_img        NVARCHAR(500)       NULL,

        CONSTRAINT FK_meter_readings_rooms
            FOREIGN KEY (room_id) REFERENCES dbo.rooms(room_id),
        CONSTRAINT FK_meter_readings_users_creator
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 6. INVOICES TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.invoices', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.invoices (
        invoice_id          INT IDENTITY(1,1)   PRIMARY KEY,
        code                NVARCHAR(50)        NULL UNIQUE,
        room_id             INT                 NOT NULL,
        meter_id            INT                 NULL,
        due_date            DATE                NOT NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'UNPAID', -- UNPAID, PAID, OVERDUE
        tax                 DECIMAL(10,2)       NULL,
        other_fee           DECIMAL(18,2)       NULL,
        room_fee            DECIMAL(18,2)       NULL,-- từ room_id lấy ra  room_fee
        electricity_price   DECIMAL(10,2)       NULL,-- từ room_id lấy ra facilities_id rồi từ đó lấy ra electricity_price
        water_price         DECIMAL(10,2)       NULL,-- từ room_id lấy ra facilities_id rồi từ đó lấy ra water_price
        internet_fee        DECIMAL(10,2)       NULL,-- từ room_id lấy ra facilities_id rồi từ đó lấy ra internet_fee
        service_fee         DECIMAL(10,2)       NULL,-- từ room_id lấy ra facilities_id rồi từ đó lấy ra service_fee
        total_amount        DECIMAL(18,2)       NULL, -- snapshot tại thời điểm chốt
        note                NVARCHAR(1000)      NULL,
        created_by          INT                 NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_invoices_rooms
            FOREIGN KEY (room_id) REFERENCES dbo.rooms(room_id),
        CONSTRAINT FK_invoices_meter_readings
            FOREIGN KEY (meter_id) REFERENCES dbo.meter_readings(meter_id),
        CONSTRAINT FK_invoices_users_creator
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 7. PAYMENTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.payments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.payments (
        payment_id          INT IDENTITY(1,1)   PRIMARY KEY,
        code                NVARCHAR(50)        NOT NULL UNIQUE,
        invoice_id          INT                 NULL,
        room_id             INT                 NOT NULL,
        status              NVARCHAR(20)        NULL DEFAULT 'SUCCESS', -- PENDING, SUCCESS, REJECTED  
        payment_date        DATE                NOT NULL,
        payment_method      NVARCHAR(50)        NOT NULL DEFAULT 'BANK_TRANSFER',
        payment_amount		DECIMAL(18,2)		NOT NULL,
		created_by          INT                 NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_payments_invoices
            FOREIGN KEY (invoice_id) REFERENCES dbo.invoices(invoice_id),
        CONSTRAINT FK_payments_rooms
            FOREIGN KEY (room_id) REFERENCES dbo.rooms(room_id),
        CONSTRAINT FK_payments_users_creator
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 8. REQUESTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.requests', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.requests (
        request_id          INT IDENTITY(1,1)   PRIMARY KEY,
        code                NVARCHAR(50)        NOT NULL UNIQUE,
        sender_id           INT                 NOT NULL,
        category            NVARCHAR(50)        NOT NULL,
        title               NVARCHAR(200)       NOT NULL,
        content             NVARCHAR(MAX)       NOT NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'PENDING',
        attachment_urls1    NVARCHAR(MAX)       NULL, -- Comma-separated list of image/file URLs
        attachment_urls2    NVARCHAR(MAX)       NULL, -- Comma-separated list of image/file URLs
        assigned_staff_id   INT                 NULL,
        rejection_reason    NVARCHAR(500)       NULL,
		appoint_schedule	DATETIME2			NULL, 
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_requests_users_sender
            FOREIGN KEY (sender_id) REFERENCES dbo.users(user_id),
        CONSTRAINT FK_requests_users_staff
            FOREIGN KEY (assigned_staff_id) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 9. NOTIFICATIONS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.notifications', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.notifications (
        notification_id     INT IDENTITY(1,1)   PRIMARY KEY,
        code                NVARCHAR(50)        NOT NULL UNIQUE,
        title               NVARCHAR(250)       NOT NULL,
        content             NVARCHAR(MAX)       NOT NULL,
        target_type         NVARCHAR(20)        NOT NULL DEFAULT 'ALL', -- ALL, FACILITY, ROOM
        facility_id         INT                 NULL,
        room_id             INT                 NULL,
        status              NVARCHAR(20)        NOT NULL DEFAULT 'DRAFT', -- DRAFT, SENT
        created_by          INT                 NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),
        sent_at             DATETIME2           NULL,
        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_notifications_users_creator
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id),
        CONSTRAINT FK_notifications_facilities
            FOREIGN KEY (facility_id) REFERENCES dbo.facilities(facility_id),
        CONSTRAINT FK_notifications_rooms
            FOREIGN KEY (room_id) REFERENCES dbo.rooms(room_id),

		CONSTRAINT CK_notifications_target
			CHECK (
				(
					target_type = 'ALL'
					AND facility_id IS NULL
					AND room_id IS NULL
				)
				OR
				(
					target_type = 'FACILITY'
					AND facility_id IS NOT NULL
					AND room_id IS NULL
				)
				OR
				(
					target_type = 'ROOM'
					AND room_id IS NOT NULL
					AND facility_id IS NULL
				)
			)
	);
END
GO

-- ============================================================
-- 10. AUDIT LOGS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.audit_logs', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.audit_logs (
        audit_log_id        INT IDENTITY(1,1)   PRIMARY KEY,
        entity_type         NVARCHAR(50)        NOT NULL, -- SYSTEM_LOG, BUSINESS_HISTORY, REQUEST, SERVICE_PRICE, INVOICE, etc.
        entity_id           INT                 NOT NULL,
        action              NVARCHAR(50)        NOT NULL,
        old_value           NVARCHAR(MAX)       NULL,
        new_value           NVARCHAR(MAX)       NULL,
        ip_address          VARCHAR(45)         NULL,
        comment             NVARCHAR(MAX)       NULL,
        created_by          INT                 NULL,
        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),

        CONSTRAINT FK_audit_logs_users_creator
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- 11. CONTRACTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.contracts', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.contracts (
		contract_id                INT IDENTITY(1,1) PRIMARY KEY,
		code                       NVARCHAR(50) NOT NULL UNIQUE,

		room_id                    INT NOT NULL,
		tenant_id                  INT NULL,

		tenant_full_name           NVARCHAR(100) NOT NULL,
		tenant_dob                 DATE NULL,
		tenant_permanent_address   NVARCHAR(500) NULL,
		tenant_identity_number     NVARCHAR(50) NOT NULL,
		tenant_identity_issue_date DATE NULL,
		tenant_identity_issue_place NVARCHAR(200) NULL,
		tenant_phone               NVARCHAR(20) NULL,

		amount_in_words            NVARCHAR(500) NULL,

		signed_date                DATE NOT NULL,
		start_date                 DATE NOT NULL,
		end_date                   DATE NOT NULL,

		status                     NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',--'ACTIVE','INACTIVE'

		created_by                 INT NULL,
		created_at                 DATETIME2 NOT NULL DEFAULT GETDATE(),
		updated_at                 DATETIME2 NOT NULL DEFAULT GETDATE(),
		deleted_at                 DATETIME2 NULL,

		CONSTRAINT FK_contracts_rooms
			FOREIGN KEY (room_id) REFERENCES dbo.rooms(room_id),

		CONSTRAINT FK_contracts_users_tenant
			FOREIGN KEY (tenant_id) REFERENCES dbo.users(user_id),

		CONSTRAINT FK_contracts_users_creator
			FOREIGN KEY (created_by) REFERENCES dbo.users(user_id)
	);
END
GO
-- ============================================================
-- 12. COMMUNITY POSTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.community_posts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.community_posts (

        post_id             INT IDENTITY(1,1) PRIMARY KEY,

        title               NVARCHAR(250)      NOT NULL,
        content             NVARCHAR(MAX)      NOT NULL,
        image_url           NVARCHAR(500)      NULL,

        author_id           INT                NOT NULL,

        status              NVARCHAR(20)       NOT NULL DEFAULT 'PENDING',
        -- PENDING, APPROVED, REJECTED

        reviewed_by         INT                NULL,

        created_at          DATETIME2          NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME2          NOT NULL DEFAULT GETDATE(),
        deleted_at          DATETIME2          NULL,

        CONSTRAINT FK_community_posts_author
            FOREIGN KEY(author_id)
            REFERENCES dbo.users(user_id),

        CONSTRAINT FK_community_posts_reviewer
            FOREIGN KEY(reviewed_by)
            REFERENCES dbo.users(user_id)
    );
END
GO
-- ============================================================
-- 13. POST REACTIONS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.post_reactions', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.post_reactions (

        post_id         INT             NOT NULL,
        user_id         INT             NOT NULL,

        created_at      DATETIME2       NOT NULL DEFAULT GETDATE(),

        CONSTRAINT PK_post_reactions
            PRIMARY KEY(post_id,user_id),

        CONSTRAINT FK_post_reactions_post
            FOREIGN KEY(post_id)
            REFERENCES dbo.community_posts(post_id),

        CONSTRAINT FK_post_reactions_user
            FOREIGN KEY(user_id)
            REFERENCES dbo.users(user_id)
    );
END
GO
-- ============================================================
-- 14. POST COMMENTS TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.post_comments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.post_comments (

        comment_id          INT IDENTITY(1,1) PRIMARY KEY,

        post_id             INT                 NOT NULL,

        user_id             INT                 NOT NULL,

        content             NVARCHAR(1000)      NOT NULL,

        created_at          DATETIME2           NOT NULL DEFAULT GETDATE(),

        updated_at          DATETIME2           NOT NULL DEFAULT GETDATE(),

        deleted_at          DATETIME2           NULL,

        CONSTRAINT FK_post_comments_post
            FOREIGN KEY(post_id)
            REFERENCES dbo.community_posts(post_id),

        CONSTRAINT FK_post_comments_user
            FOREIGN KEY(user_id)
            REFERENCES dbo.users(user_id)
    );
END
GO
-- ============================================================
-- COMMUNITY INDEXES
-- ============================================================

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name='IX_community_posts_status'
)
CREATE NONCLUSTERED INDEX IX_community_posts_status
ON dbo.community_posts(status)
WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name='IX_community_posts_author'
)
CREATE NONCLUSTERED INDEX IX_community_posts_author
ON dbo.community_posts(author_id);
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name='IX_post_comments_post'
)
CREATE NONCLUSTERED INDEX IX_post_comments_post
ON dbo.post_comments(post_id);
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name='IX_post_reactions_post'
)
CREATE NONCLUSTERED INDEX IX_post_reactions_post
ON dbo.post_reactions(post_id);
GO
-- ============================================================
-- 12. SYSTEM CONFIG TABLE
-- ============================================================
IF OBJECT_ID(N'dbo.system_config', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.system_config (
        config_key      NVARCHAR(100)   NOT NULL,
        config_value    NVARCHAR(500)   NOT NULL,
        updated_at      DATETIME2       NOT NULL DEFAULT GETDATE(),
        updated_by      INT             NULL,

        CONSTRAINT PK_system_config
            PRIMARY KEY (config_key),
        CONSTRAINT FK_system_config_users
            FOREIGN KEY (updated_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- ============================================================
-- INDEXES
-- ============================================================
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_users_username' AND object_id = OBJECT_ID(N'dbo.users'))
    CREATE NONCLUSTERED INDEX IX_users_username ON dbo.users(username) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_users_role' AND object_id = OBJECT_ID(N'dbo.users'))
    CREATE NONCLUSTERED INDEX IX_users_role ON dbo.users(role) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_audit_logs_entity' AND object_id = OBJECT_ID(N'dbo.audit_logs'))
    CREATE NONCLUSTERED INDEX IX_audit_logs_entity ON dbo.audit_logs(entity_type, entity_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_facilities_manager' AND object_id = OBJECT_ID(N'dbo.facilities'))
    CREATE UNIQUE INDEX UX_facilities_manager ON dbo.facilities(manager_id) WHERE manager_id IS NOT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_facilities_operator'AND object_id = OBJECT_ID(N'dbo.facilities'))
    CREATE UNIQUE INDEX UX_facilities_operator ON dbo.facilities(operator_id) WHERE operator_id IS NOT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_rooms_tenant' AND object_id = OBJECT_ID(N'dbo.rooms'))
    CREATE UNIQUE INDEX UX_rooms_tenant ON dbo.rooms(tenant_id) WHERE tenant_id IS NOT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_rooms_facility' AND object_id = OBJECT_ID(N'dbo.rooms'))
    CREATE NONCLUSTERED INDEX IX_rooms_facility ON dbo.rooms(facility_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_rooms_status' AND object_id = OBJECT_ID(N'dbo.rooms'))
    CREATE NONCLUSTERED INDEX IX_rooms_status ON dbo.rooms(status) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_dependents_tenant' AND object_id = OBJECT_ID(N'dbo.dependents'))
    CREATE NONCLUSTERED INDEX IX_dependents_tenant ON dbo.dependents(tenant_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_requests_sender' AND object_id = OBJECT_ID(N'dbo.requests'))
    CREATE NONCLUSTERED INDEX IX_requests_sender ON dbo.requests(sender_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_requests_staff' AND object_id = OBJECT_ID(N'dbo.requests'))
    CREATE NONCLUSTERED INDEX IX_requests_staff ON dbo.requests(assigned_staff_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_requests_status' AND object_id = OBJECT_ID(N'dbo.requests'))
    CREATE NONCLUSTERED INDEX IX_requests_status ON dbo.requests(status) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_invoices_room' AND object_id = OBJECT_ID(N'dbo.invoices'))
    CREATE NONCLUSTERED INDEX IX_invoices_room ON dbo.invoices(room_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_invoices_status' AND object_id = OBJECT_ID(N'dbo.invoices'))
    CREATE NONCLUSTERED INDEX IX_invoices_status ON dbo.invoices(status) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_payments_invoice' AND object_id = OBJECT_ID(N'dbo.payments'))
    CREATE NONCLUSTERED INDEX IX_payments_invoice ON dbo.payments(invoice_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_payments_room' AND object_id = OBJECT_ID(N'dbo.payments'))
    CREATE NONCLUSTERED INDEX IX_payments_room ON dbo.payments(room_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_notifications_status' AND object_id = OBJECT_ID(N'dbo.notifications'))
    CREATE NONCLUSTERED INDEX IX_notifications_status ON dbo.notifications(status) WHERE deleted_at IS NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_meter_room_date' AND object_id = OBJECT_ID(N'dbo.meter_readings'))
    CREATE UNIQUE INDEX UX_meter_room_date ON dbo.meter_readings(room_id, reading_date);
GO