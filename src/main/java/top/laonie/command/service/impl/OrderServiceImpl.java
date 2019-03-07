package top.laonie.command.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.laonie.command.dao.OrderDOMapper;
import top.laonie.command.dao.SequenceDOMapper;
import top.laonie.command.dataobject.OrderDO;
import top.laonie.command.dataobject.SequenceDO;
import top.laonie.command.error.BusinessException;
import top.laonie.command.service.ItemService;
import top.laonie.command.service.OrderService;
import top.laonie.command.service.UserService;
import top.laonie.command.service.model.ItemModel;
import top.laonie.command.service.model.OrderModel;
import top.laonie.command.service.model.UserModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static top.laonie.command.error.EmbusinessError.PARAMETER_VALIDATION_ERROR;
import static top.laonie.command.error.EmbusinessError.STOCK_NOT_ENOUGH;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        //校验下单状态：商品是否存在，用户是否合法，数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }

        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
//        if (amount < 0 || amount > itemModel.getStock()) {
        if (amount < 0 || amount > 99) {
            throw new BusinessException(PARAMETER_VALIDATION_ERROR, "购买数量有误！");
        }

        //落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(STOCK_NOT_ENOUGH);
        }
        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
        //生成交易订单号码
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
//        System.out.println(orderDO);
        orderDOMapper.insertSelective(orderDO);
        //返回前端
        return orderModel;
    }

    /**
     * 将orderModel转化为orderDO
     *
     * @param orderModel
     * @return
     */
    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

    /**
     * 订单号的生成
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected String generateOrderNo() {
        //假设订单号有16位，前八位位时间信息
        StringBuilder stringBuilder = new StringBuilder();
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.BASIC_ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //  中间六位为自增序列
        int sequence = 0;
        //从自增序列表中获取默认值
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        //默认值赋给自增序列
        sequence = sequenceDO.getCurrentValue();
        //自增序列加上自己的步长后更新数据库
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        //如果自增序列不满6位，则以0填充为6位
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        // 最后两位为分库分表位
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
