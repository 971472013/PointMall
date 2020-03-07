package com.jilian.server.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.IdWorker;
import com.jilian.server.Utils;
import com.jilian.server.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description: 商城端订单Service
 * @Date: Create in 3:37 下午 2020/2/9
 */
@Service
public class MallOrderService {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获得某个用户的所有订单
     *
     * @param id
     * @return
     */
    public List<Order> getAllOrderByCustomerID(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("customerID").is(id));
        List<Order> ans = mongoTemplate.find(query, Order.class, "order");
        ans.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getCreateTime()
                        .isAfter(o2.getCreateTime())) {
                    return -1;
                } else if (o1.getCreateTime()
                        .isBefore(o2.getCreateTime())) {
                    return 1;
                }
                return 0;
            }
        });
        return ans;
    }

    /**
     * 更新订单状态为 to
     *
     * @param jsonObject
     * @return
     */
    public boolean updateOrderStatus(JSONObject jsonObject) {
        Query query = new Query();
        String orderID = jsonObject.getString("orderID");
        String goodsID = jsonObject.getString("goodsID");
        String to = jsonObject.getString("to");
        query.addCriteria(Criteria.where("_id").is(orderID));
        Order order = mongoTemplate.findOne(query, Order.class, "order");
        System.out.println(order);
        order.getOrderGoodsList().get(goodsID).setOrderStatus(OrderStatus.valueOf(to));
        Update update = new Update();
        update.set("orderGoodsList", order.getOrderGoodsList());
        mongoTemplate.upsert(query, update, "order");
        return true;
    }

    /**
     * 申请退款
     *
     * @param jsonObject
     * @return
     */
    public boolean backOrderGoods(JSONObject jsonObject) {
        Query query = new Query();
        String orderID = jsonObject.getString("orderID");
        String goodsID = jsonObject.getString("goodsID");
        String backExpressID = jsonObject.getString("backExpressID");
        String backExpressCompany = jsonObject.getString("backExpressCompany");
        query.addCriteria(Criteria.where("_id").is(orderID));
        Order order = mongoTemplate.findOne(query, Order.class, "order");
        order.getOrderGoodsList().get(goodsID).setOrderStatus(OrderStatus.valueOf("售后中"));
        order.getOrderGoodsList().get(goodsID).setBackExpressID(backExpressID);
        order.getOrderGoodsList().get(goodsID).setBackExpressCompany(backExpressCompany);
        Update update = new Update();
        update.set("orderGoodsList", order.getOrderGoodsList());
        mongoTemplate.upsert(query, update, "order");
        return true;
    }

    /**
     * 生成新订单
     *
     * @param jsonObject
     * @return
     */
    public JSONObject createOneOrder(JSONObject jsonObject) {
        String orderID = IdWorker.getOrderID();

        jsonObject.put("createTime", LocalDateTime.now());
        JSONObject list = jsonObject.getJSONObject("orderGoodsList");
        Map<String, OrderGoods> map = new HashMap<>();
        list.forEach((k, v) -> {
            JSONObject each = list.getJSONObject(k);
            each.put("payTime", LocalDateTime.now());
            list.put(k, each);
            OrderGoods orderGoods = Utils.JSONObject2orderGoods(each);
            orderGoods.setPayTime(LocalDateTime.now());
            map.put(orderGoods.getGoodsID(), orderGoods);
        });
        jsonObject.put("orderGoodsList", list);
        JSONObject address = jsonObject.getJSONObject("address");
        address.put("adefault", address.getBooleanValue("isDefault"));
        address.remove("isDefault");
        jsonObject.put("address", address);

        Order order = new Order(orderID, jsonObject.getString("customerPhone"), jsonObject.getString("customerID"),
                jsonObject.getString("userID"), jsonObject.getString("mallID"), LocalDateTime.now(),
                PayMethod.valueOf(jsonObject.getString("payMethod")), map, jsonObject.getDoubleValue("totalMarketPrice"),
                jsonObject.getDoubleValue("totalPointPrice"), jsonObject.getIntValue("totalNum"), jsonObject.getString("pointType"), address.toJavaObject(Address.class),
                null, jsonObject.getDoubleValue("express"), jsonObject.getDoubleValue("pointExpress"));
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(orderID));
        mongoTemplate.insert(order, "order");

        String customerID = jsonObject.getString("customerID");
        String mallID = jsonObject.getString("mallID");
        query = new Query();
        query.addCriteria(Criteria.where("_id").is(customerID).and("belongMall").is(mallID));
        Update update = new Update();
        update.inc("remain", -1 * Double.parseDouble(jsonObject.getString("totalPointPrice")));
        mongoTemplate.upsert(query, update, "mall_customer");
        jsonObject.put("id", orderID);
        jsonObject.put("createTime", LocalDateTime.now().format(Utils.worldNormal));
        return jsonObject;
    }
}
