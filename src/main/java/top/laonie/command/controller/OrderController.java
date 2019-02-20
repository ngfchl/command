package top.laonie.command.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.laonie.command.error.BusinessException;
import top.laonie.command.response.CommonReturnType;
import top.laonie.command.service.OrderService;
import top.laonie.command.service.model.OrderModel;
import top.laonie.command.service.model.UserModel;

import javax.servlet.http.HttpServletRequest;

import static top.laonie.command.error.EmbusinessError.USER_NOT_LOGIN;

@Controller("order")
@RequestMapping("order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")//后端跨域请求解决方案，结局json数据传输问题
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = CONTENT_TYPE)
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId, @RequestParam("amount") Integer amount) throws BusinessException {
        //获取用户登录信息
        Boolean is_Login = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (is_Login == null || !is_Login.booleanValue()) {
            throw new BusinessException(USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, amount);

        return CommonReturnType.create(orderModel);
    }
}
