package org.enshine.myshortlink.admin.common.enums;

import org.enshine.myshortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {
    USER_NULL("B000200","用户记录不存在"),
    USER_NAME_EXIST("B000201","用户名已存在"),
    USER_EXIST("B000202","用户记录已存在"),
    USER_SAVE_ERROR("B000203","用户记录新增失败"),
    USER_UPDATE_ERROR("B000204","用户记录新增失败"),
    USER_NAME_NOT_EXIST("B000205","用户名不存在"),
    USER_PWD_ERROR("B000206","密码错误"),
    USER_LOGOFF("B000207","用户已注销"),
    USER_HAS_LOGIN("B000208","用户已登录"),
    USER_NOT_LOGIN("B000209","用户未登录");
    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
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
