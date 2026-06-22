package com.quanlyphongtro.dto;

import com.quanlyphongtro.model.Request;
import java.util.List;

public class DashboardSummaryDTO {
    private int pendingRequestsCount;
    private int inProgressRequestsCount;
    private int totalRooms;
    private int updatedMeterReadingsCount;
    private int incidentsReportedCount;
    private List<Request> todaysAppointments;

    public DashboardSummaryDTO() {}

    public int getPendingRequestsCount() { return pendingRequestsCount; }
    public void setPendingRequestsCount(int pendingRequestsCount) { this.pendingRequestsCount = pendingRequestsCount; }

    public int getInProgressRequestsCount() { return inProgressRequestsCount; }
    public void setInProgressRequestsCount(int inProgressRequestsCount) { this.inProgressRequestsCount = inProgressRequestsCount; }

    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

    public int getUpdatedMeterReadingsCount() { return updatedMeterReadingsCount; }
    public void setUpdatedMeterReadingsCount(int updatedMeterReadingsCount) { this.updatedMeterReadingsCount = updatedMeterReadingsCount; }

    public int getIncidentsReportedCount() { return incidentsReportedCount; }
    public void setIncidentsReportedCount(int incidentsReportedCount) { this.incidentsReportedCount = incidentsReportedCount; }

    public List<Request> getTodaysAppointments() { return todaysAppointments; }
    public void setTodaysAppointments(List<Request> todaysAppointments) { this.todaysAppointments = todaysAppointments; }
}
