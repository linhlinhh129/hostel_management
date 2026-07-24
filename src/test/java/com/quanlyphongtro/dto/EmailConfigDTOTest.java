package com.quanlyphongtro.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailConfigDTOTest {

    @Test
    void testPasswordMasking() {
        EmailConfigDTO dto = new EmailConfigDTO();
        dto.setHost("smtp.gmail.com");
        // DTO design should inherently avoid storing password or return mask
        // DTO design should inherently avoid storing password
        assertThrows(NoSuchMethodException.class, () -> {
            EmailConfigDTO.class.getMethod("getPassword");
        }, "Password getter should not exist in DTO");
        
        dto.setPort("587");
        assertEquals("587", dto.getPort());
    }
}
