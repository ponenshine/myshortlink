package org.enshine.myshortlink.project.common.enums;


import org.enshine.myshortlink.project.common.convention.errorcode.IErrorCode;

public enum LinkErrorCodeEnum implements IErrorCode {
    LINK_CREATE_FAIL("B000400","短链接创建失败"),
    LINK_EXIST("B000400","短链接已存在");
    private final String code;

    private final String message;

    LinkErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
