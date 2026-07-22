-- T020: Schema setup for Dependent Management testing
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Dependent' and xtype='U')
BEGIN
    CREATE TABLE Dependent (
        id INT IDENTITY(1,1) PRIMARY KEY,
        tenant_id INT NOT NULL,
        full_name NVARCHAR(255) NOT NULL,
        dob DATE NULL,
        gender NVARCHAR(10) NULL,
        relationship NVARCHAR(50) NULL,
        phone VARCHAR(20) NULL,
        identity_number VARCHAR(20) NULL,
        permanent_address NVARCHAR(500) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        deleted_at DATETIME NULL
    );
END

-- Seed test data (2 Tenants, 3 Dependents, 1 deleted)
TRUNCATE TABLE Dependent;
INSERT INTO Dependent (tenant_id, full_name, dob, gender, relationship, identity_number, deleted_at) VALUES 
(1, N'Nguyễn Văn A', '2000-01-01', N'Nam', N'Con trai', '079012345678', NULL),
(1, N'Lê Thị B', '1998-05-10', N'Nữ', N'Vợ', '079087654321', NULL),
(1, N'Người Đã Xóa', '1990-01-01', N'Nam', N'Khác', '123456789', GETDATE()),
(2, N'Trần C', '2010-01-01', N'Nam', N'Con trai', '0011223344', NULL);
