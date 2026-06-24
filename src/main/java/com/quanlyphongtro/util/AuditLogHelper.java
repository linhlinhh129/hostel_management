package com.quanlyphongtro.util;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.model.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuditLogHelper {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogHelper.class);

    private AuditLogHelper() {}

    public static void log(AuditLogDAO dao, HttpServletRequest req,
                           String entityType, int entityId, String action,
                           String oldValue, String newValue, Integer userId) {
        try {
            AuditLog log = new AuditLog();
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setAction(action);
            log.setOldValue(oldValue);
            log.setNewValue(newValue);
            log.setIpAddress(req != null ? req.getRemoteAddr() : null);
            log.setCreatedBy(userId);
            dao.insert(log);
        } catch (Exception e) {
            logger.warn("AuditLog insert failed for entityType={} entityId={} action={}",
                entityType, entityId, action, e);
        }
    }
}
