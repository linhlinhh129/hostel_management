package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.DependentDAO;
import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.service.impl.DependentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * DependentServiceImpl: pure DAO delegation — mỗi method gọi đúng DAO method
 * và truyền thẳng kết quả về. Test xác nhận wiring đúng.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DependentServiceImpl — người phụ thuộc (Tenant)")
class DependentServiceImplTest {

    private DependentDAO mockDAO;
    private DependentServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockDAO = mock(DependentDAO.class);
        service = new DependentServiceImpl();
        Field f = DependentServiceImpl.class.getDeclaredField("dependentDAO");
        f.setAccessible(true);
        f.set(service, mockDAO);
    }

    private Dependent dep(int id, int tenantId) {
        Dependent d = new Dependent();
        d.setId(id); d.setTenantId(tenantId); d.setFullName("Thành viên " + id);
        return d;
    }

    @Test @DisplayName("getDependentsByTenantId delegates to DAO")
    void getByTenantId() {
        when(mockDAO.findByTenantId(1)).thenReturn(List.of(dep(1, 1), dep(2, 1)));
        List<Dependent> result = service.getDependentsByTenantId(1);
        assertThat(result).hasSize(2);
    }

    @Test @DisplayName("getDependentById delegates to DAO")
    void getById() {
        when(mockDAO.findByIdAndTenantId(3, 1)).thenReturn(Optional.of(dep(3, 1)));
        assertThat(service.getDependentById(3, 1)).isPresent();
    }

    @Test @DisplayName("getDependentById trả về empty khi không tìm thấy")
    void getById_notFound() {
        when(mockDAO.findByIdAndTenantId(99, 1)).thenReturn(Optional.empty());
        assertThat(service.getDependentById(99, 1)).isEmpty();
    }

    @Test @DisplayName("addDependent delegates to DAO và trả về kết quả")
    void addDependent() {
        Dependent d = dep(0, 1);
        when(mockDAO.insert(d)).thenReturn(true);
        assertThat(service.addDependent(d)).isTrue();
        verify(mockDAO).insert(d);
    }

    @Test @DisplayName("updateDependent delegates to DAO và trả về kết quả")
    void updateDependent() {
        Dependent d = dep(1, 1);
        when(mockDAO.update(d)).thenReturn(true);
        assertThat(service.updateDependent(d)).isTrue();
        verify(mockDAO).update(d);
    }

    @Test @DisplayName("removeDependent delegates to DAO và trả về kết quả")
    void removeDependent() {
        when(mockDAO.softDelete(1, 1)).thenReturn(true);
        assertThat(service.removeDependent(1, 1)).isTrue();
        verify(mockDAO).softDelete(1, 1);
    }

    @Test @DisplayName("removeDependent trả về false khi DAO trả false")
    void removeDependent_false() {
        when(mockDAO.softDelete(99, 1)).thenReturn(false);
        assertThat(service.removeDependent(99, 1)).isFalse();
    }
}
