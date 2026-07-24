package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.service.AuditLogService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public List<AuditLog> list(String actor, String role, String entityType, String action,
                               String dateFrom, String dateTo, int page, int pageSize) {
        if (!isValidDate(dateFrom) || !isValidDate(dateTo) || !isRangeValid(dateFrom, dateTo)) {
            return Collections.emptyList();
        }
        return auditLogDAO.findAll(actor, role, entityType, action, dateFrom, dateTo, page, pageSize);
    }

    @Override
    public int count(String actor, String role, String entityType, String action,
                     String dateFrom, String dateTo) {
        if (!isValidDate(dateFrom) || !isValidDate(dateTo) || !isRangeValid(dateFrom, dateTo)) {
            return 0;
        }
        return auditLogDAO.count(actor, role, entityType, action, dateFrom, dateTo);
    }

    @Override
    public AuditLog getById(int id) throws NotFoundException {
        return auditLogDAO.findById(id).orElseThrow(NotFoundException::new);
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true;
        }
        try {
            LocalDate.parse(dateStr.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isRangeValid(String dateFrom, String dateTo) {
        if (dateFrom == null || dateFrom.trim().isEmpty() || dateTo == null || dateTo.trim().isEmpty()) {
            return true;
        }
        try {
            LocalDate from = LocalDate.parse(dateFrom.trim());
            LocalDate to = LocalDate.parse(dateTo.trim());
            return !from.isAfter(to);
        } catch (Exception e) {
            return false;
        }
    }
}
