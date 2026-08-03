package com.quanlyphongtro.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Date;
import java.sql.Timestamp;

public class PenaltyCalculator {

    public static BigDecimal calculateLateFee(BigDecimal roomFee, Date dueDateSql, Timestamp pendingTimestamp, LocalDate currentDate) {
        if (dueDateSql == null || roomFee == null) {
            return BigDecimal.ZERO;
        }

        LocalDate dueDate = dueDateSql.toLocalDate();
        LocalDate endDate = currentDate;

        if (pendingTimestamp != null) {
            LocalDate pendingDate = pendingTimestamp.toLocalDateTime().toLocalDate();
            endDate = pendingDate;
        }

        if (endDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
            return roomFee.multiply(new BigDecimal("0.01"))
                          .multiply(new BigDecimal(daysLate))
                          .setScale(0, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    public static BigDecimal calculateLateFee(BigDecimal roomFee, Date dueDateSql, Date pendingDateSql, LocalDate currentDate) {
        if (dueDateSql == null || roomFee == null) {
            return BigDecimal.ZERO;
        }

        LocalDate dueDate = dueDateSql.toLocalDate();
        LocalDate endDate = currentDate;

        if (pendingDateSql != null) {
            LocalDate pendingDate = pendingDateSql.toLocalDate();
            endDate = pendingDate;
        }

        if (endDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
            return roomFee.multiply(new BigDecimal("0.01"))
                          .multiply(new BigDecimal(daysLate))
                          .setScale(0, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}
