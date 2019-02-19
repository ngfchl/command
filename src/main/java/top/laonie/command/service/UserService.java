package top.laonie.command.service;

import top.laonie.command.error.BusinessException;
import top.laonie.command.service.model.UserModel;

public interface UserService {
    /**
     * 通过id获取用户
     *
     * @param id
     */
    UserModel getUserById(Integer id);

    /**
     * 用户注册接口
     */
    void register(UserModel userModel) throws BusinessException;

    /**
     * 用户登录接口！
     *
     * @param username
     * @param encrptPassword
     */
    UserModel checkLogin(String username, String encrptPassword) throws BusinessException;
}
