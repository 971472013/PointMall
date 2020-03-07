package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.Order;
import com.jilian.server.bean.OrderGoods;
import com.jilian.server.bean.OrderStatus;
import com.jilian.server.bean.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 4:45 下午 2020/2/15
 */
@Service
public class AdminOrderService {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据条件筛选订单
     *
     * @param jsonObject
     * @return
     */
    public List<JSONObject> filterOrder(JSONObject jsonObject) {
        String mallID = jsonObject.getString("mallID");
        List<JSONObject> list = getAllOrderByMallID(mallID);
        List<JSONObject> ans = new ArrayList<>();
        String orderID = jsonObject.getString("orderID");
        String orderStatus = jsonObject.getString("orderStatus");
        String goodsID = jsonObject.getString("goodsID");
        String customerID = jsonObject.getString("customerID");
        String time = jsonObject.getString("createTime");

        for (JSONObject tem : list) {
            if (tem.getString("id").contains(orderID) &&
                    tem.getString("customerID").contains(customerID) &&
                    (orderStatus.equals("全部") || tem.getJSONObject("orderGoods").getString("orderStatus").contains(orderStatus)) &&
                    tem.getJSONObject("orderGoods").getString("goodsID").contains(goodsID)) {
                if (time != null) {
                    LocalDateTime creatTime = LocalDateTime.parse(time, Utils.worldNormalZ);
                    LocalDateTime start = LocalDateTime.of(creatTime.toLocalDate(), LocalTime.MIN);
                    LocalDateTime end = LocalDateTime.of(creatTime.toLocalDate(), LocalTime.MAX);
                    creatTime = tem.getObject("createTime", Date.class).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if (creatTime.isAfter(start) && creatTime.isBefore(end)) {
                        ans.add(tem);
                    }
                } else {
                    ans.add(tem);
                }
            }
        }
        return ans;
    }

    /**
     * 获取商城中的所有订单
     *
     * @param mallID
     * @return
     */
    public List<JSONObject> getAllOrderByMallID(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mallID").is(mallID));
        List<JSONObject> list = mongoTemplate.find(query, JSONObject.class, "order");
        List<JSONObject> ans = new ArrayList<>();
        for (JSONObject tem : list) {
            JSONObject arr = tem.getJSONObject("orderGoodsList");
            for (String arrString : arr.keySet()) {
                JSONObject each = (JSONObject) tem.clone();
                each.remove("orderGoodsList");
                each.put("orderGoods", arr.getJSONObject(arrString));
                String goodsID = each.getString("_id");
                each.put("id", goodsID);
                each.remove("_id");
                ans.add(each);
            }
        }
        return ans;
    }

    /**
     * 驳回退款申请
     *
     * @param jsonObject
     * @return
     */
    public boolean rejectBack(JSONObject jsonObject) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(jsonObject.getString("orderID")));
        Order order = mongoTemplate.findOne(query, Order.class, "order");
        String goodsID = jsonObject.getString("goodsID");
        OrderGoods orderGoods = order.getOrderGoodsList().get(goodsID);
        orderGoods.setOrderStatus(OrderStatus.退款失败);
        order.getOrderGoodsList().put(goodsID, orderGoods);
        Update update = new Update();
        update.set("orderGoodsList", order.getOrderGoodsList());
        mongoTemplate.upsert(query, update, "order");
        return true;
    }

    /**
     * 同意退款申请
     * @param jsonObject
     * @return
     */
    public boolean backConfirm(JSONObject jsonObject) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(jsonObject.getString("orderID")));
        Order order = mongoTemplate.findOne(query, Order.class, "order");
        String goodsID = jsonObject.getString("goodsID");
        OrderGoods orderGoods = order.getOrderGoodsList().get(goodsID);
        orderGoods.setOrderStatus(OrderStatus.订单关闭);
        order.getOrderGoodsList().put(goodsID, orderGoods);
        Update update = new Update();
        update.set("orderGoodsList", order.getOrderGoodsList());
        mongoTemplate.upsert(query, update, "order");

        int num = orderGoods.getNum();
        double pointPrice = orderGoods.getPointPrice();

        query = new Query();
        query.addCriteria(Criteria.where("_id").is(goodsID));
        update = new Update();
        update.inc("stockCount", num);
        mongoTemplate.upsert(query, update, "goods");

        query = new Query();
        query.addCriteria(Criteria.where("_id").is(order.getCustomerID()));
        update = new Update();
        update.inc("remain", (num * 1.0) * pointPrice + orderGoods.getPointExpress());
        mongoTemplate.upsert(query, update, "mall_customer");
        return true;
    }

    /**
     * 更新订单信息
     * @param jsonObject
     * @return
     */
    public boolean updateOrder(JSONObject jsonObject) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(jsonObject.getString("id")));
        Order order = mongoTemplate.findOne(query, Order.class, "order");
        String goodID = jsonObject.getJSONObject("orderGoods").getString("goodsID");
        OrderGoods orderGoods = Utils.JSONObject2orderGoods(jsonObject.getJSONObject("orderGoods"));
        order.getOrderGoodsList().put(goodID, orderGoods);
        Update update = Utils.toUpdate(order);
        mongoTemplate.upsert(query, update, "order");
        return true;
    }

    /**
     * 获取近一个月内的订单
     * @param mallID
     * @return
     */
    public List<Pair<String, List<Order>>> getNearMonthOrder(String mallID) {
        List<Pair<String, List<Order>>> ans = new ArrayList<>();
        for (int i = 30; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.MAX);
            Query query = new Query();
            query.addCriteria(Criteria.where("mallID").is(mallID).andOperator(
                    Criteria.where("createTime").lte(end),
                    Criteria.where("createTime").gte(start)
            ));
            List<Order> orderList = mongoTemplate.find(query, Order.class, "order");

            if (orderList == null) {
                orderList = new ArrayList<>();
            }
            ans.add(new Pair<>(LocalDate.now().minusDays(i).toString(), orderList));
        }
        return ans;
    }
}
