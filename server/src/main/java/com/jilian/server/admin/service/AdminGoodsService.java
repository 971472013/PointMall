package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jilian.server.IdWorker;
import com.jilian.server.Utils;
import com.jilian.server.bean.Goods;
import com.jilian.server.bean.Mall;
import com.jilian.server.bean.PayMethod;
import com.jilian.server.bean.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 7:12 下午 2020/2/13
 */
@Service("adminGoodsService")
public class AdminGoodsService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AdminMallInfoService adminMallInfoService;

    /**
     * 按条件过滤商品
     *
     * @param data
     * @return
     */
    public List<JSONObject> filterGoods(JSONObject data) {
        String userID = data.getString("userID");
        String mallID = data.getString("mallID");
        String type = data.getString("goodsType");
        String status = data.getString("goodsStatus");
        String name = data.getString("goodsName");
        String needDistribution = data.getString("needDistribution");
        String supplier = data.getString("supplier");
        String stockStatus = data.getString("stockStatus");
        List<JSONObject> goods = getGoodsByMallID(userID, mallID);
        List<JSONObject> ans = new ArrayList<>();
        for (JSONObject i : goods) {
            if ((type.equals("全部") || type.equals(i.getString("goodsType"))) &&
                    (status.equals("全部") || status.equals(i.getString("goodsStatus"))) &&
                    (i.getString("name").contains(name)) &&
                    (needDistribution.equals("全部") || needDistribution.equals(i.getString("needDistribution"))) &&
                    (supplier.equals("全部") || supplier.equals(i.getString("supplier"))) &&
                    (stockStatus.equals("全部") ||
                            ((stockStatus.equals("有") && i.getIntValue("stockCount") > 0) ||
                                    (stockStatus.equals("无") && i.getIntValue("stockCount") <= 0)))) {
                ans.add(i);
            }
        }
        return ans;
    }

    /**
     * 获取商城中的所有商品的详细信息
     * @param userID
     * @param mallID
     * @return
     */
    public List<JSONObject> getGoodsByMallID(String userID, String mallID) {
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Map<String, Map<String, Object>> map = mall.getGoodsList();
        List<JSONObject> ans = new ArrayList<>();
        List<Goods> goodsList = getGoodsByUserID(userID);
        Set<String> keySet = map.keySet();
        goodsList.forEach(v -> {
            if (keySet.contains(v.getId())) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(v));
                jsonObject.put("goodsStatus", map.get(v.getId()).get("goodsStatus").toString());
                jsonObject.put("payMethod", PayMethod.valueOf(map.get(v.getId()).get("payMethod").toString()).getCode());
                jsonObject.put("pointPrice", Double.parseDouble(map.get(v.getId()).get("pointPrice").toString()));
                jsonObject.put("pointType", map.get(v.getId()).get("pointType").toString());
                ans.add(jsonObject);
            }
        });
        return ans;
    }

    /**
     * 获取某个用户的所有商品信息
     * @param userID
     * @return
     */
    public List<Goods> getGoodsByUserID(String userID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("belongUser").is(userID));
        return mongoTemplate.find(query, Goods.class, "goods");
    }

    /**
     * 获取商城中所有商品的详细信息，附加额外信息标记商品是否存在当前商城中
     * @param userID
     * @param mallID
     * @return
     */
    public List<JSONObject> getAllGoods(String userID, String mallID) {
        List<Goods> goodsList = getGoodsByUserID(userID);
        List<JSONObject> ans = new ArrayList<>();
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Set<String> set = mall.getGoodsList().keySet();
        for (Goods goods : goodsList) {
            JSONObject object = JSON.parseObject(JSON.toJSONString(goods));
            if (set.contains(goods.getId())) {
                object.put("inMall", true);
            } else {
                object.put("inMall", false);
            }
            ans.add(object);
        }
        return ans;
    }

    /**
     * 更新商品上架状态
     * @param mallID
     * @param goodsID
     * @param to
     * @return
     */
    public boolean toggleGoodsStatus(String mallID, String goodsID, String to) {
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Map<String, Map<String, Object>> map = mall.getGoodsList();
        Map<String, Object> goods = map.get(goodsID);
        goods.put("goodsStatus", to);
        map.put(goodsID, goods);
        return adminMallInfoService.updateMall(mall);
    }

    /**
     * 移出商城中的商品
     * @param mallID
     * @param goodsID
     * @return
     */
    public boolean deleteMallGoods(String mallID, String goodsID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Map<String, Map<String, Object>> map = mall.getGoodsList();
        map.remove(goodsID);
        Map<String, Subject> subjectMap = mall.getSubjectList();
        subjectMap.forEach((k, v) -> {
            v.getGoodsList().remove(goodsID);
        });
        return adminMallInfoService.updateMall(mall);
    }

    /**
     * 更新商品价格
     * @param jsonObject
     * @return
     */
    public boolean updatePrice(JSONObject jsonObject) {
        String mallID = jsonObject.getString("mallID");
        String goodsID = jsonObject.getString("goodsID");
        double price = Double.parseDouble(jsonObject.get("pointPrice").toString());
        PayMethod payMethod = PayMethod.valueOf((int) jsonObject.get("payMethod"));
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Map<String, Map<String, Object>> map = mall.getGoodsList();
        Map<String, Object> goods = map.get(goodsID);
        goods.put("pointPrice", price);
        goods.put("payMethod", payMethod);
        return adminMallInfoService.updateMall(mall);
    }

    /**
     * 将商品加入商城中
     * @param jsonObject
     * @return
     */
    public boolean addToMall(JSONObject jsonObject) {
        String mallID = jsonObject.getString("mallID");
        String goodsID = jsonObject.getString("goodsID");
        double supplyPrice = Double.parseDouble(jsonObject.get("supplyPrice").toString());
        double marketPrice = Double.parseDouble(jsonObject.get("marketPrice").toString());
        Mall mall = adminMallInfoService.getMallByMallID(mallID);
        Map<String, Object> add = new HashMap<>();
        add.put("payMethod", PayMethod.ONLY_POINT);
        add.put("pointPrice", mall.getPoint().getExchangeRate() * marketPrice);
        add.put("pointType", mall.getPoint().getName());
        add.put("goodsStatus", "上架");
        add.put("supplyPrice", supplyPrice);
        add.put("marketPrice", marketPrice);
        mall.getGoodsList().put(goodsID, add);
        adminMallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 在添加商品界面 修改商品
     *
     * @param object
     * @return
     */
    public boolean updateGoods(JSONObject object) {
        String goodsID = object.get("id").toString();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(goodsID));
        Update update = Utils.JSONObjectToUpdate(object);
        mongoTemplate.upsert(query, update, "goods");
        return true;
    }

    /**
     * 在添加商品界面 增加自有商品
     *
     * @param object
     * @return
     */
    public JSONObject createGoods(JSONObject object) {
        String goodsID = IdWorker.getGoodsID();
        object.remove("id");
        object.put("_id", goodsID);
        object.put("source", "自营");
        mongoTemplate.insert(object, "goods");

        object.remove("_id");
        object.put("id", goodsID);
        object.put("inMall", false);
        return object;
    }

    /**
     * 根据goodsID获得一个goods
     *
     * @param id
     * @return
     */
    public JSONObject getGoodsByID(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, JSONObject.class, "goods");
    }
}
