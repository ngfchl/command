package top.laonie.command.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import top.laonie.command.controller.viewobject.UserVO;
import top.laonie.command.error.BusinessException;
import top.laonie.command.error.EmbusinessError;
import top.laonie.command.response.CommonReturnType;
import top.laonie.command.service.UserService;
import top.laonie.command.service.model.UserModel;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")//后端跨域请求解决方案，结局json数据传输问题
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    /**
     * 用户登录接口！！
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = CONTENT_TYPE)
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "username") String username,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //校验传入的参数
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmbusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务，校验用户名密码是否合法！
        UserModel userModel = userService.checkLogin(username, this.encodeByMd5(password));
        //设置session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        return CommonReturnType.create(null);
    }

    /**
     * 用户注册接口
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = CONTENT_TYPE)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "username") String username,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号与otp验证码是否相符合
        String inSessionOptCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOptCode)) {
            throw new BusinessException(EmbusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码错误！");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setUsername(username);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setRegistration("byPhone");
        userModel.setTelphone(telphone);
        //把密码进行MD5加密
        userModel.setEncrptPassword(this.encodeByMd5(password));
        //调用用户注册方法，将数据信息传入后台
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //    MD5加密方法
    private String encodeByMd5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    /**
     * 用户获取otp短信接口
     *
     * @return
     */
    @RequestMapping(value = "/getotp", method = RequestMethod.POST, consumes = CONTENT_TYPE)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam("telphone") String telphone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        //数字转为字符串
        String otpCode = String.valueOf(randomInt);
        //将验证码与手机号关联,使用HTTPSession的方式绑定
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        //将验证码发送给用户（省略，需要发送短信的接口）
        //生产环境严禁如此操作，相当于将用户机密信息暴露给生产方
        System.out.println(telphone + "..&&.." + otpCode);
        return CommonReturnType.create(null);
    }

    /**
     * 输出最终的用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/getuser")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //从service层获取对应id的用户对象并返回
        UserModel userModel = userService.getUserById(id);
        //若获取的用户不存在
        if (userModel == null) {
            throw new BusinessException(EmbusinessError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(convertFromModel(userModel));
    }

    /**
     * 从完整的用户信息转化为可以展示的用户信息
     * 即隐藏需要保密的信息
     *
     * @param userModel
     * @return
     */
    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }


}
