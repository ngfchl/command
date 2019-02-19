package top.laonie.command.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.laonie.command.dao.UserDOMapper;
import top.laonie.command.dao.UserPasswordDOMapper;
import top.laonie.command.dataobject.UserDO;
import top.laonie.command.dataobject.UserPasswordDO;
import top.laonie.command.error.BusinessException;
import top.laonie.command.error.EmbusinessError;
import top.laonie.command.service.UserService;
import top.laonie.command.service.model.UserModel;
import top.laonie.command.validator.ValidationResult;
import top.laonie.command.validator.ValidatorImpl;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //获取对应用户的DataObject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }

        //通过用户id获取用户密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);

    }

    @Override
    @Transactional//数据库事物管理
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(EmbusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //按照注解的规则校验信息，不再需要单独验证！
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmbusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //实现model到dataObject的方法
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmbusinessError.PARAMETER_VALIDATION_ERROR, "用户名或手机号已被注册！");
        }
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    /**
     * 用户登录信息检查
     *
     * @param username
     * @param encrptPassword
     * @throws BusinessException
     */
    @Override
    public UserModel checkLogin(String username, String encrptPassword) throws BusinessException {
        //  通过用户名获取用户信息
        UserDO userDO = userDOMapper.selectByUsername(username);
        if (userDO == null) {
            throw new BusinessException(EmbusinessError.USER_LOGIN_FAIL);
        }
        //获取密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if (userPasswordDO == null) {
            throw new BusinessException(EmbusinessError.USER_LOGIN_FAIL);
        }
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        //  比对获取的密码是否匹配
        if (!StringUtils.equals(encrptPassword, userModel != null ? userModel.getEncrptPassword() : null)) {
            throw new BusinessException(EmbusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    //从UserModel中提取UserPassword所需信息
    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    //从UserModel中提取UserDO所需要的信息
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        UserModel userModel = new UserModel();
        if (userDO == null) {
            return null;
        }
        BeanUtils.copyProperties(userDO, userModel);
        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }

}
