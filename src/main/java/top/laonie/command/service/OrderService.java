package top.laonie.command.service;

import top.laonie.command.error.BusinessException;
import top.laonie.command.service.model.OrderModel;

public interface OrderService {
    /**
     * 创建订单
     *
     * @param userId
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;


}
