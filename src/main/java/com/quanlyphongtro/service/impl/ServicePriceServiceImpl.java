package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dto.ServicePriceDTO;
import com.quanlyphongtro.dto.ServicePriceHistoryDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.service.ServicePriceService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicePriceServiceImpl implements ServicePriceService {

    private final FacilityDAO facilityDAO;
    private final AuditLogDAO auditLogDAO;

    public ServicePriceServiceImpl() {
        this.facilityDAO = new FacilityDAO();
        this.auditLogDAO = new AuditLogDAO();
    }

    @Override
    public List<ServicePriceDTO> getCurrentPrices(int managerId) {
        Optional<Facility> facilityOpt = facilityDAO.findByManagerId(managerId);
        List<ServicePriceDTO> prices = new ArrayList<>();
        
        if (facilityOpt.isPresent()) {
            Facility facility = facilityOpt.get();
            String updatedAt = facility.getUpdatedAt() != null
                    ? facility.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "";
            
            prices.add(new ServicePriceDTO("ELECTRICITY", "Giá điện", "VNĐ/kWh", facility.getElectricityPrice(), updatedAt, null, null));
            prices.add(new ServicePriceDTO("WATER", "Giá nước", "VNĐ/m3", facility.getWaterPrice(), updatedAt, null, null));
            prices.add(new ServicePriceDTO("SERVICE_FEE", "Phí dịch vụ", "VNĐ/tháng", facility.getServiceFee(), updatedAt, null, null));
            prices.add(new ServicePriceDTO("INTERNET", "Phí Internet", "VNĐ/tháng", facility.getInternetFee(), updatedAt, null, null));
        }
        
        return prices;
    }

    @Override
    public boolean updatePrice(int managerId, String priceType, BigDecimal newPrice, String note) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        Optional<Facility> facilityOpt = facilityDAO.findByManagerId(managerId);
        if (facilityOpt.isEmpty()) {
            return false;
        }
        
        Facility facility = facilityOpt.get();
        BigDecimal oldPrice = null;
        switch (priceType) {
            case "ELECTRICITY": 
                oldPrice = facility.getElectricityPrice(); 
                facility.setElectricityPrice(newPrice);
                break;
            case "WATER": 
                oldPrice = facility.getWaterPrice(); 
                facility.setWaterPrice(newPrice);
                break;
            case "SERVICE_FEE": 
                oldPrice = facility.getServiceFee(); 
                facility.setServiceFee(newPrice);
                break;
            case "INTERNET": 
                oldPrice = facility.getInternetFee(); 
                facility.setInternetFee(newPrice);
                break;
            default: return false;
        }
        
        boolean updated = facilityDAO.update(facility);
        if (updated) {
            String oldValueStr = oldPrice != null ? oldPrice.toString() : "0";
            String newValueStr = newPrice.toString();
            String action = "UPDATE_" + priceType;
            
            auditLogDAO.logWithComment(
                "SERVICE_PRICE", 
                facility.getFacilityId(), 
                action, 
                oldValueStr, 
                newValueStr, 
                "", // ipAddress could be passed from servlet if needed, ignoring for now or could pass from controller
                managerId, 
                note
            );
        }
        
        return updated;
    }

    @Override
    public List<ServicePriceHistoryDTO> getPriceHistory(int managerId, String priceType, int page, int size) {
        Optional<Facility> facilityOpt = facilityDAO.findByManagerId(managerId);
        if (facilityOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        return auditLogDAO.getPriceHistories(facilityOpt.get().getFacilityId(), priceType, page, size);
    }
}
