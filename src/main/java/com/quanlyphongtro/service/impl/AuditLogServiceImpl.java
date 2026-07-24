package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.service.AuditLogService;

import java.util.List;

public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public List<AuditLog> list(String actor, String role, String entityType, String action,
                               String dateFrom, String dateTo, int page, int pageSize) {
        return auditLogDAO.findAll(actor, role, entityType, action, dateFrom, dateTo, page, pageSize);
    }

    @Override
    public int count(String actor, String role, String entityType, String action,
                     String dateFrom, String dateTo) {
        return auditLogDAO.count(actor, role, entityType, action, dateFrom, dateTo);
    }

    @Override
    public AuditLog getById(int id) throws NotFoundException {
        return auditLogDAO.findById(id).orElseThrow(NotFoundException::new);
    }
}
