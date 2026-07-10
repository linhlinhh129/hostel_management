package com.quanlyphongtro.service;

import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;

import java.util.List;

public interface AuditLogService {

    /**
     * Lấy danh sách audit log có filter và phân trang.
     * Mặc định chỉ hiển thị log của MANAGER và OPERATOR khi role = null.
     */
    List<AuditLog> list(String actor, String role, String entityType, String action,
                        String dateFrom, String dateTo, int page, int pageSize);

    int count(String actor, String role, String entityType, String action,
              String dateFrom, String dateTo);

    AuditLog getById(int id) throws NotFoundException;
}
