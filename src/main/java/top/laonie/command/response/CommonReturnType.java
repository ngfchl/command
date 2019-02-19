package top.laonie.command.response;

public class CommonReturnType {

    //表明对应请求的返回处理结果“success”或者“fail”
    private String status;
    //若states返回success，则返回前端需要的json数据
    //若为fail，则fdata内使用通用的错误码格式
    private Object data;

    //定义一个通的创建方法
    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    private void setData(Object data) {
        this.data = data;
    }
}
