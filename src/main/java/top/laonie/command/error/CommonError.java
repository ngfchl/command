package top.laonie.command.error;

public interface CommonError {

    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
