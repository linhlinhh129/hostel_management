package com.quanlyphongtro.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VNPayConfigDTOTest {

    @Test
    void testSecretKeyMasking() {
        VNPayConfigDTO dto = new VNPayConfigDTO();
        dto.setTmnCode("VNPAY123");
        // VNPayConfigDTO shouldn't contain secretKey field for security reasons when returned to UI
        assertThrows(NoSuchMethodException.class, () -> {
            VNPayConfigDTO.class.getMethod("getSecretKey");
        }, "SecretKey getter should not exist in DTO");
        
        assertEquals("VNPAY123", dto.getTmnCode());
    }
}
