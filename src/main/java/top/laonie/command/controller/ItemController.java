package top.laonie.command.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.laonie.command.controller.viewobject.ItemVO;
import top.laonie.command.error.BusinessException;
import top.laonie.command.response.CommonReturnType;
import top.laonie.command.service.ItemService;
import top.laonie.command.service.model.ItemModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")//后端跨域请求解决方案，结局json数据传输问题
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    /**
     * 商品列表浏览
     */
    @RequestMapping(value = "/listitem", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();
        //使用stream接口将list中的数据转化为itemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    /**
     * 创建商品实体
     *
     * @param title
     * @param price
     * @param description
     * @param stock
     * @param imgUrl
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = CONTENT_TYPE_FOMED)
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "stock") Integer stock,
//                                       @RequestParam(name = "sales") String sales,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setDescription(description);
        itemModel.setStock(stock);
//        itemModel.setTitle(sales);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    /**
     * 从model转化为VO输出模型
     *
     * @param itemModel
     * @return
     */
    private ItemVO convertItemVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);

        return itemVO;
    }
}
