package org.enshine.myshortlink.admin.common.enums;

import org.enshine.myshortlink.admin.common.convention.errorcode.IErrorCode;

public enum GroupErrorCodeEnum implements IErrorCode {
    GROUP_SAVE_FAIL("B000300","分组添加失败"),
    GROUP_UPDATE_FAIL("B000301","分组名称更新失败"),
    GROUP_COUNT_LIMITED("B000303","分组数量已达上限"),
    GROUP_DELETE_FAIL("B000304","分组删除失败");
    private final String code;

    private final String message;

    GroupErrorCodeEnum(String code, String message) {
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
