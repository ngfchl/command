package top.laonie.command.service.model;

import javax.validation.constraints.*;

public class UserModel {

    private Integer id;
    @NotBlank(message = "用户名不能为空！")
    @Size(min = 6, max = 16, message = "请输入6-16个字符的用户名！")
    private String username;
    @NotNull(message = "性别不能为空！")
    private Integer gender;
    @NotNull(message = "年龄不能为空！")
    @Pattern(regexp = "^[0-9]{1,2}$", message = "年龄必须是整数！")
    @Min(value = 18, message = "未满18周岁的未成年人如需注册请与管理员联系！")
    @Max(value = 99, message = "超过100岁的老寿星你好，如需注册，请与管理员联系！")
    private Integer age;
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "1[3|4|5|6|7|8|9][0-9]\\d{8}", message = "请输入正确的手机号码")//正则表达式来限制手机号格式
    private String telphone;
    private String registration;
    private Integer thirdId;
    @NotNull(message = "密码不能为空！")
    private String encrptPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public Integer getThirdId() {
        return thirdId;
    }

    public void setThirdId(Integer thirdId) {
        this.thirdId = thirdId;
    }

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }
}
