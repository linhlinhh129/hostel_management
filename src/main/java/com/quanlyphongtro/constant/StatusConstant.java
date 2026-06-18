package com.quanlyphongtro.constant;

public final class StatusConstant {
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String LOCKED = "LOCKED";
    public static final String DRAFT = "DRAFT";

    public static final String ROOM_AVAILABLE = "AVAILABLE";
    public static final String ROOM_OCCUPIED = "OCCUPIED";
    public static final String ROOM_MAINTENANCE = "MAINTENANCE";
    public static final String ROOM_RESERVED = "RESERVED";

    public static final String INVOICE_UNPAID = "UNPAID";
    public static final String INVOICE_PAID = "PAID";
    public static final String INVOICE_OVERDUE = "OVERDUE";

    public static final String REQUEST_PENDING = "PENDING";
    public static final String REQUEST_ASSIGNED = "ASSIGNED";
    public static final String REQUEST_IN_PROGRESS = "IN_PROGRESS";
    public static final String REQUEST_DONE = "DONE";
    public static final String REQUEST_REJECTED = "REJECTED";
    public static final String REQUEST_CANCELLED = "CANCELLED";

    public static final String NOTIFICATION_DRAFT = "DRAFT";
    public static final String NOTIFICATION_SENT = "SENT";

    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_PAID = "PAID";
    public static final String PAYMENT_REJECTED = "REJECTED";

    public static final String DEBT_PENDING = "PENDING";
    public static final String DEBT_PAID = "PAID";
    public static final String DEBT_OVERDUE = "OVERDUE";

    private StatusConstant() {}
}
