package top.laonie.command.service;

import org.springframework.transaction.annotation.Transactional;
import top.laonie.command.error.BusinessException;
import top.laonie.command.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    /**
     * 创建商品
     *
     * @return
     */
    @Transactional
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    /**
     * 商品列表
     *
     * @return
     */
    List<ItemModel> listItem();

    /**
     * 商品详情
     *
     * @return
     */
    ItemModel getItemById(Integer id);

    /**
     * 扣减库存数量
     */
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;
}
