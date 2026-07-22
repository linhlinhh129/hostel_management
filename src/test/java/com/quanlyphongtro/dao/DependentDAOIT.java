package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.util.DatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DependentDAOIT extends BaseTestContainer {

    private DependentDAO dependentDAO;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;

    @BeforeEach
    void setUp() {
        dependentDAO = new DependentDAO();
        // Mock DatabaseUtil.getConnection() để trả về connection của Testcontainers
        mockedDatabaseUtil = Mockito.mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDatabaseUtil != null) {
            mockedDatabaseUtil.close();
        }
    }

    @Test
    @DisplayName("T022: Test findByTenantId - Không lấy bản ghi đã xóa mềm")
    void testFindByTenantId() {
        // Tenant 1 có 2 dependent active và 1 deleted (Dữ liệu từ init-test-db.sql)
        List<Dependent> list = dependentDAO.findByTenantId(1);

        assertThat(list).hasSize(2); // Phải là 2, bản ghi thứ 3 bị bỏ qua
        assertThat(list).extracting(Dependent::getFullName)
                .containsExactlyInAnyOrder("Nguyễn Văn A", "Lê Thị B");
    }

    @Test
    @DisplayName("T023: Test findByIdAndTenantId - Trả về Optional.empty() nếu sai Tenant")
    void testFindByIdAndTenantId_WrongTenant() {
        // ID 1 thuộc về Tenant 1, truyền Tenant 2
        Optional<Dependent> result = dependentDAO.findByIdAndTenantId(1, 2);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("T024: Test softDelete - Update deleted_at thành công")
    void testSoftDelete() {
        // Thực hiện xóa mềm ID 1 của Tenant 1
        boolean isDeleted = dependentDAO.softDelete(1, 1);
        assertThat(isDeleted).isTrue();

        // Query lại thử, phải trả về rỗng vì câu query chặn deleted_at IS NULL
        Optional<Dependent> result = dependentDAO.findByIdAndTenantId(1, 1);
        assertThat(result).isEmpty();
    }
}
