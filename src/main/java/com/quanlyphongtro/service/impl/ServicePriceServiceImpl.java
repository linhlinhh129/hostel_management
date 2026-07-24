package com.quanlyphongtro.service.impl;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            Map<String, LocalDateTime> updatesMap = auditLogDAO.getLatestPriceUpdates(facility.getFacilityId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            String defaultTime = facility.getUpdatedAt() != null
                    ? facility.getUpdatedAt().format(formatter)
                    : (facility.getCreatedAt() != null ? facility.getCreatedAt().format(formatter) : "");

            String elecTime = updatesMap.containsKey("ELECTRICITY") ? updatesMap.get("ELECTRICITY").format(formatter) : defaultTime;
            String waterTime = updatesMap.containsKey("WATER") ? updatesMap.get("WATER").format(formatter) : defaultTime;
            String svcFeeTime = updatesMap.containsKey("SERVICE_FEE") ? updatesMap.get("SERVICE_FEE").format(formatter) : defaultTime;
            String netTime = updatesMap.containsKey("INTERNET") ? updatesMap.get("INTERNET").format(formatter) : defaultTime;
            
            prices.add(new ServicePriceDTO("ELECTRICITY", "Giá điện", "VNĐ/kWh", facility.getElectricityPrice(), elecTime, null, null));
            prices.add(new ServicePriceDTO("WATER", "Giá nước", "VNĐ/m3", facility.getWaterPrice(), waterTime, null, null));
            prices.add(new ServicePriceDTO("SERVICE_FEE", "Phí dịch vụ", "VNĐ/tháng", facility.getServiceFee(), svcFeeTime, null, null));
            prices.add(new ServicePriceDTO("INTERNET", "Phí Internet", "VNĐ/tháng", facility.getInternetFee(), netTime, null, null));
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

        if (oldPrice != null && oldPrice.compareTo(newPrice) == 0) {
            return true; // Trả về true nhưng không ghi đè DB hay log lịch sử mới nếu giá trị không đổi
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
