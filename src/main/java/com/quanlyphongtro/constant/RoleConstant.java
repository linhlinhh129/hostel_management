package com.quanlyphongtro.constant;

public final class RoleConstant {
    public static final String ADMIN    = "ADMIN";
    public static final String MANAGER  = "MANAGER";
    public static final String TENANT   = "TENANT";
    public static final String OPERATOR = "OPERATOR";

    public static final int MAX_LOGIN_ATTEMPTS    = 5;
    public static final int LOCK_DURATION_MINUTES = 1;

    private RoleConstant() {}
}
