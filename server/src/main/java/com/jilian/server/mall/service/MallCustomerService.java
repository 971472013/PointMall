package com.jilian.server.mall.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jilian.server.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:53 下午 2020/2/5
 */
@Service
public class MallCustomerService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private MallService mallInfoService;

    /**
     * 根据手机号查询特定商城下的用户
     *
     * @param mallID
     * @param phone
     * @return
     */
    public Customer findCustomerByPhoneAndMall(String mallID, String phone) {
        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(phone).and("belongMall").is(mallID));
        Customer customer = mongoTemplate.findOne(query, Customer.class, "mall_customer");
        return customer;
    }

    /**
     * 获取某个商城下的特定用户的信息
     * @param mallID
     * @param phone
     * @return
     */
    public JSONObject getInfo(String mallID, String phone) {
        Customer customer = findCustomerByPhoneAndMall(mallID, phone);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", customer.getPhone());
        jsonObject.put("addressList", customer.getAddressList());
        jsonObject.put("id", customer.getId());
        jsonObject.put("remain", customer.getRemain());

        JSONArray addressList = jsonObject.getJSONArray("addressList");
        for (int i = 0; i < addressList.size(); i++) {
            JSONObject tem = addressList.getJSONObject(i);
            tem.put("isDefault", tem.getBooleanValue("adefault"));
            tem.remove("adefault");
        }
        jsonObject.put("addressList", addressList);

        List<JSONObject> cartGoods = new ArrayList<>();
        List<Pair<String, Integer>> customerGoodsList = customer.getGoodsList();

        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Map<String, Object>> mallGoodsList = mall.getGoodsList();
        for (Pair<String, Integer> item : customerGoodsList) {
            Goods goods = mallGoodsService.findGoodByID(item.getKey());
            JSONObject temGoods = new JSONObject();
            Map<String, Object> goodsInfo = mallGoodsList.get(goods.getId());
            temGoods.put("id", goods.getId());
            temGoods.put("name", goods.getName());
            temGoods.put("describe", goods.getDescribe());
            temGoods.put("supplier", goods.getSupplier());
            temGoods.put("marketPrice", goods.getMarketPrice());
            temGoods.put("supplyPrice", goods.getSupplyPrice());
            temGoods.put("source", goods.getSource());
            temGoods.put("stockCount", goods.getStockCount());
            temGoods.put("goodsType", goods.getGoodsType());
            temGoods.put("needDistribution", goods.getNeedDistribution());
            if (goodsInfo != null) {
                temGoods.put("pointPrice", Double.parseDouble(goodsInfo.get("pointPrice").toString()));
                temGoods.put("pointType", goodsInfo.get("pointType").toString());
                temGoods.put("goodsStatus", goodsInfo.get("goodsStatus").toString());
                temGoods.put("payMethod", PayMethod.valueOf(goodsInfo.get("payMethod").toString()));
            } else {
                temGoods.put("pointPrice", null);
                temGoods.put("pointType", null);
                temGoods.put("goodsStatus", "下架");
                temGoods.put("payMethod", null);
            }
            temGoods.put("num", item.getValue());
            temGoods.put("image", goods.getImageList().get(0));
            cartGoods.add(temGoods);
        }
        jsonObject.put("goodsList", cartGoods);
        List<Order> orderList = mallOrderService.getAllOrderByCustomerID(customer.getId());
        jsonObject.put("orderList", orderList);
        return jsonObject;
    }

    /**
     * 更新用户的地址列表
     * @param data
     * @return
     */
    public boolean updateAddress(JSONObject data) {
        String customerID = data.getString("customerID");
        JSONArray address = data.getJSONArray("addressList");
        for (int i = 0; i < address.size(); i++) {
            JSONObject tem = address.getJSONObject(i);
            tem.put("adefault", tem.getBooleanValue("isDefault"));
            tem.remove("isDefault");
        }
        Query query = Query.query(Criteria.where("_id").is(customerID));
        Update update = new Update();
        update.set("addressList", address);
        mongoTemplate.upsert(query, update, "mall_customer");
        return true;
    }

    /**
     * 更新用户购物车列表
     * @param data
     * @return
     */
    public boolean updateCartGoods(JSONObject data) {
        String id = data.getString("id");
        JSONArray goodsList = data.getJSONArray("goodsList");
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("goodsList", goodsList);
        mongoTemplate.upsert(query, update, "mall_customer");
        return true;
    }
}