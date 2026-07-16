package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.RevenueDAO;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import com.quanlyphongtro.service.impl.RevenueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * RevenueServiceImpl: pure DAO delegation.
 * Tất cả 5 method chuyển thẳng sang DAO không thêm logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RevenueServiceImpl — thống kê doanh thu")
class RevenueServiceImplTest {

    private RevenueDAO mockDAO;
    private RevenueServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockDAO = mock(RevenueDAO.class);
        service = new RevenueServiceImpl();
        Field f = RevenueServiceImpl.class.getDeclaredField("revenueDAO");
        f.setAccessible(true);
        f.set(service, mockDAO);
    }

    @Test @DisplayName("getSystemRevenue delegates to DAO")
    void getSystemRevenue() {
        SystemRevenueDTO dto = new SystemRevenueDTO();
        when(mockDAO.getSystemRevenue("06/2025")).thenReturn(dto);
        assertThat(service.getSystemRevenue("06/2025")).isSameAs(dto);
    }

    @Test @DisplayName("getFacilityRevenues delegates to DAO")
    void getFacilityRevenues() {
        FacilityRevenueStatDTO stat = new FacilityRevenueStatDTO();
        stat.setFacilityName("Cơ sở A");
        when(mockDAO.getFacilityRevenues("06/2025")).thenReturn(List.of(stat));
        List<FacilityRevenueStatDTO> result = service.getFacilityRevenues("06/2025");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFacilityName()).isEqualTo("Cơ sở A");
    }

    @Test @DisplayName("getFacilityRevenuesPaged delegates with correct params")
    void getFacilityRevenuesPaged() {
        when(mockDAO.getFacilityRevenuesPaged("06/2025", 2, 10)).thenReturn(List.of());
        service.getFacilityRevenuesPaged("06/2025", 2, 10);
        verify(mockDAO).getFacilityRevenuesPaged("06/2025", 2, 10);
    }

    @Test @DisplayName("countFacilitiesWithRevenue delegates to DAO")
    void countFacilities() {
        when(mockDAO.countFacilitiesWithRevenue("06/2025")).thenReturn(7);
        assertThat(service.countFacilitiesWithRevenue("06/2025")).isEqualTo(7);
    }

    @Test @DisplayName("getRevenueTrend delegates with correct months param")
    void getRevenueTrend() {
        when(mockDAO.getRevenueTrend(6)).thenReturn(List.of());
        service.getRevenueTrend(6);
        verify(mockDAO).getRevenueTrend(6);
    }
}
