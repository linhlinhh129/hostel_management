package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.EmailConfigDTO;
import com.quanlyphongtro.dto.VNPayConfigDTO;
import com.quanlyphongtro.exception.ValidationException;

public interface SystemConfigService {
    EmailConfigDTO getUIEmailConfig();
    VNPayConfigDTO getUIVNPayConfig();
    
    void updateEmailConfig(String host, String portStr, String username, String password, int updatedBy) throws ValidationException;
    void updateVNPayConfig(String payUrl, String returnUrl, String tmnCode, String secretKey, String apiUrl, int updatedBy) throws ValidationException;
}
