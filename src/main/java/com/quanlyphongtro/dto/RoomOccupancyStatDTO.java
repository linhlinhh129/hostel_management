package com.quanlyphongtro.dto;

/**
 * DTO chứa thống kê tình trạng phòng (trống/đang thuê) toàn hệ thống.
 * Dùng cho Admin Dashboard.
 */
public class RoomOccupancyStatDTO {
    private int totalRooms;
    private int occupiedRooms;
    private int availableRooms;
    private int occupancyRate; // phần trăm (0-100)

    public RoomOccupancyStatDTO() {
    }

    public RoomOccupancyStatDTO(int totalRooms, int occupiedRooms, int availableRooms) {
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.availableRooms = availableRooms;
        this.occupancyRate = totalRooms == 0 ? 0 : (occupiedRooms * 100) / totalRooms;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
        recalculateRate();
    }

    public int getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(int occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
        recalculateRate();
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(int occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    private void recalculateRate() {
        this.occupancyRate = totalRooms == 0 ? 0 : (occupiedRooms * 100) / totalRooms;
    }
}
