package top.laonie.command.error;

/**
 * 错误类型枚举类
 */
public enum EmbusinessError implements CommonError {
    //通用错误类型1000开头
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    UNKNOW_ERROR(10002, "未知错误"),

    //2000开头的为用信息相关的错误定义
    USER_NOT_EXIST(20001, "用户不存在！"),
    USER_LOGIN_FAIL(20002, "用户名或者密码错误！"),
    ;

    private int errCode;
    private String errMsg;

    EmbusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
