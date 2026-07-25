package com.quanlyphongtro.service;

import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;

import java.util.List;

public interface AuditLogService {

    // hiển thị log của MANAGER và OPERATOR khi role = null.
    List<AuditLog> list(String actor, String role, String entityType, String action,
                        String dateFrom, String dateTo, int page, int pageSize);

    // đếm số lượng log
    int count(String actor, String role, String entityType, String action,
              String dateFrom, String dateTo);

    AuditLog getById(int id) throws NotFoundException;
}
