package com.quanlyphongtro.dto;

import java.math.BigDecimal;

public class SystemRevenueDTO {
    private BigDecimal totalRevenue;
    private int paidCount;
    private int unpaidCount;
    private int overdueCount;
    private int collectionRate; // percentage
    private int growthRate;     // vs previous period
    private BigDecimal totalOutstanding;
    private BigDecimal totalBilledAmount;

    public SystemRevenueDTO() {}

    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }

    public BigDecimal getTotalBilledAmount() { return totalBilledAmount; }
    public void setTotalBilledAmount(BigDecimal totalBilledAmount) { this.totalBilledAmount = totalBilledAmount; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getPaidCount() { return paidCount; }
    public void setPaidCount(int paidCount) { this.paidCount = paidCount; }

    public int getUnpaidCount() { return unpaidCount; }
    public void setUnpaidCount(int unpaidCount) { this.unpaidCount = unpaidCount; }

    public int getOverdueCount() { return overdueCount; }
    public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }

    public int getCollectionRate() { return collectionRate; }
    public void setCollectionRate(int collectionRate) { this.collectionRate = collectionRate; }

    public int getGrowthRate() { return growthRate; }
    public void setGrowthRate(int growthRate) { this.growthRate = growthRate; }
}
