package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.DependentDAO;
import com.quanlyphongtro.model.Dependent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DependentServiceImplTest {

    @Mock
    private DependentDAO dependentDAO;

    @InjectMocks
    private DependentServiceImpl dependentService;

    // ==========================================
    // FIXTURES & MOCK DATA FACTORIES 
    // ==========================================
    private Dependent buildMockDependent(Integer id, Integer tenantId, String name, String cccd, boolean isDeleted) {
        Dependent d = new Dependent();
        d.setId(id);
        d.setTenantId(tenantId);
        d.setFullName(name);
        d.setIdentityNumber(cccd);
        d.setDob(LocalDate.of(2000, 1, 1));
        d.setGender("Male");
        d.setRelationship("Son");
        d.setPhone("0123456789");
        d.setCreatedAt(LocalDateTime.now());
        if (isDeleted) {
            d.setDeletedAt(LocalDateTime.now());
        }
        return d;
    }

    private Dependent buildMockDependentMissingFields(Integer id, Integer tenantId, String name) {
        Dependent d = new Dependent();
        d.setId(id);
        d.setTenantId(tenantId);
        d.setFullName(name);
        // Explicitly leaving dob, phone, cccd as null
        return d;
    }

    @BeforeEach
    void setUp() throws Exception {
        // Inject mock DAO via reflection since it is initialized inline in DependentServiceImpl
        Field daoField = DependentServiceImpl.class.getDeclaredField("dependentDAO");
        daoField.setAccessible(true);
        daoField.set(dependentService, dependentDAO);
    }

    // ==========================================
    // PHASE 3: HAPPY PATH SCENARIOS
    // ==========================================

    // # EARS [TC-HP-01], [TC-HP-03]
    @Test
    @DisplayName("Lấy danh sách thành công và đã được sắp xếp (Mock DAO)")
    void testGetDependents_SuccessAndSorted() {
        // Given
        Integer tenantId = 1;
        Dependent dep1 = buildMockDependent(101, tenantId, "Nguyen Van A", "079012345678", false);
        Dependent dep2 = buildMockDependent(102, tenantId, "Le Thi B", "079087654321", false);
        
        when(dependentDAO.findByTenantId(tenantId)).thenReturn(Arrays.asList(dep2, dep1)); // Giả lập DAO trả về list

        // When
        List<Dependent> result = dependentService.getDependentsByTenantId(tenantId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dep2, dep1);
        verify(dependentDAO, times(1)).findByTenantId(tenantId);
    }

    // # EARS [TC-HP-02]
    @Test
    @DisplayName("Lấy chi tiết thành công và test hàm Masking CCCD")
    void testGetDependentDetail_Success_Masked() {
        // Given
        Integer tenantId = 1;
        Integer depId = 101;
        Dependent dep = buildMockDependent(depId, tenantId, "Nguyen Van A", "079012345678", false);
        
        when(dependentDAO.findByIdAndTenantId(depId, tenantId)).thenReturn(Optional.of(dep));

        // When
        Optional<Dependent> result = dependentService.getDependentById(depId, tenantId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIdentityNumber()).isEqualTo("079012345678");
        // Test trực tiếp logic masking của entity
        assertThat(result.get().getMaskedIdentityNumber()).isEqualTo("079******678"); 
        verify(dependentDAO, times(1)).findByIdAndTenantId(depId, tenantId);
    }

    // ==========================================
    // PHASE 4: ERROR CASES
    // ==========================================

    // # EARS [TC-ERR-01]
    @Test
    @DisplayName("Lỗi IDOR / Không có quyền truy cập (Sai tenantId)")
    void testGetDependentDetail_Unauthorized() {
        // Given
        Integer wrongTenantId = 999;
        Integer depId = 101;
        
        // DAO sẽ trả về empty nếu tenantId không khớp
        when(dependentDAO.findByIdAndTenantId(depId, wrongTenantId)).thenReturn(Optional.empty());

        // When
        Optional<Dependent> result = dependentService.getDependentById(depId, wrongTenantId);

        // Then
        assertThat(result).isEmpty();
        verify(dependentDAO, times(1)).findByIdAndTenantId(depId, wrongTenantId);
    }

    // # EARS [TC-ERR-02]
    @Test
    @DisplayName("Lỗi Unauthenticated (TenantId null/invalid)")
    void testGetDependents_Unauthenticated() {
        // Given
        Integer unauthTenantId = -1; 
        when(dependentDAO.findByTenantId(unauthTenantId)).thenReturn(Collections.emptyList());

        // When
        List<Dependent> result = dependentService.getDependentsByTenantId(unauthTenantId);

        // Then
        assertThat(result).isEmpty();
        verify(dependentDAO, times(1)).findByTenantId(unauthTenantId);
    }

    // # EARS [TC-ERR-03]
    @Test
    @DisplayName("Lỗi Not Found hoặc Soft Delete (Đã xóa)")
    void testGetDependentDetail_NotFoundOrDeleted() {
        // Given
        Integer tenantId = 1;
        Integer deletedDepId = 102;
        
        // DAO giả lập không tìm thấy vì bản ghi đã bị soft delete (DAO query chặn deletedAt IS NULL)
        when(dependentDAO.findByIdAndTenantId(deletedDepId, tenantId)).thenReturn(Optional.empty());

        // When
        Optional<Dependent> result = dependentService.getDependentById(deletedDepId, tenantId);

        // Then
        assertThat(result).isEmpty();
    }

    // # EARS [TC-ERR-04]
    @Test
    @DisplayName("Lỗi System Failure (Ngoại lệ từ DB)")
    void testGetDependents_SystemFailure() {
        // Given
        Integer tenantId = 1;
        when(dependentDAO.findByTenantId(tenantId)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            dependentService.getDependentsByTenantId(tenantId);
        });
        
        assertThat(exception.getMessage()).isEqualTo("Database connection failed");
        verify(dependentDAO, times(1)).findByTenantId(tenantId);
    }

    // ==========================================
    // PHASE 5: BOUNDARY VALUES
    // ==========================================

    // # EARS [TC-BV-01]
    @Test
    @DisplayName("Truy xuất mảng rỗng khi Tenant không có người phụ thuộc")
    void testGetDependents_EmptyList() {
        // Given
        Integer tenantId = 2; // Tenant mới chưa có ai
        when(dependentDAO.findByTenantId(tenantId)).thenReturn(Collections.emptyList());

        // When
        List<Dependent> result = dependentService.getDependentsByTenantId(tenantId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // # EARS [TC-BV-02]
    @Test
    @DisplayName("Parse an toàn khi thiếu các trường Optional (DOB, CCCD, Phone)")
    void testGetDependentDetail_MissingOptionalFields() {
        // Given
        Integer tenantId = 1;
        Integer depId = 103;
        Dependent dep = buildMockDependentMissingFields(depId, tenantId, "Child without ID");
        
        when(dependentDAO.findByIdAndTenantId(depId, tenantId)).thenReturn(Optional.of(dep));

        // When
        Optional<Dependent> result = dependentService.getDependentById(depId, tenantId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDob()).isNull();
        assertThat(result.get().getDobLabel()).isEqualTo("N/A"); // Test logic fallback
        assertThat(result.get().getMaskedIdentityNumber()).isNull();
    }

    // # EARS [TC-BV-03]
    @Test
    @DisplayName("Xử lý chuỗi tên siêu dài")
    void testGetDependentDetail_LongNames() {
        // Given
        Integer tenantId = 1;
        Integer depId = 104;
        String veryLongName = "A".repeat(255); // 255 ký tự
        Dependent dep = buildMockDependent(depId, tenantId, veryLongName, "079012345678", false);
        
        when(dependentDAO.findByIdAndTenantId(depId, tenantId)).thenReturn(Optional.of(dep));

        // When
        Optional<Dependent> result = dependentService.getDependentById(depId, tenantId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo(veryLongName);
    }

    // ==========================================
    // PHASE 6: CONCURRENT SCENARIOS
    // ==========================================

    // # EARS [TC-CS-01]
    @Test
    @DisplayName("Race condition: Tranh chấp đọc / xóa mềm đồng thời")
    void testGetDependent_ConcurrentSoftDelete() {
        // Given
        Integer tenantId = 1;
        Integer depId = 105;
        
        // Mô phỏng: Thread 1 vừa query xong nhưng Thread 2 đã gọi delete
        // Trong unit test mức service, ta giả lập thao tác update/delete thành công 
        // ngay trước khi Thread 1 fetch lại.
        when(dependentDAO.softDelete(depId, tenantId)).thenReturn(true);
        when(dependentDAO.findByIdAndTenantId(depId, tenantId)).thenReturn(Optional.empty());

        // When
        boolean deleteResult = dependentService.removeDependent(depId, tenantId);
        Optional<Dependent> fetchResult = dependentService.getDependentById(depId, tenantId);

        // Then
        assertThat(deleteResult).isTrue();
        assertThat(fetchResult).isEmpty(); // Phải không tìm thấy nữa
    }
}
