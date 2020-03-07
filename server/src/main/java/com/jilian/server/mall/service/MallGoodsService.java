package com.jilian.server.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.Goods;
import com.jilian.server.bean.Mall;
import com.jilian.server.bean.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 10:42 下午 2020/2/8
 */
@Service("mallGoodsService")
public class MallGoodsService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MallService mallService;

    /**
     * 获取商品在特定商城中的信息
     *
     * @param id
     * @param mallID
     * @return
     */
    public JSONObject getGoodsInfoByID(String id, String mallID) {
        JSONObject ans = Utils.toJSONObject(findGoodByID(id));
        Mall mall = mallService.getMallByMallID(mallID);
        Map<String, Object> goodsList = mall.getGoodsList().get(id);
        ans.put("goodsStatus", goodsList.get("goodsStatus").toString());
        ans.put("payMethod", goodsList.get("payMethod").toString());
        ans.put("pointPrice", Double.parseDouble(goodsList.get("pointPrice").toString()));
        ans.put("marketPrice", Double.parseDouble(goodsList.get("marketPrice").toString()));
        ans.put("pointType", goodsList.get("pointType").toString());
        ans.put("supplyPrice", goodsList.get("supplyPrice").toString());
        return ans;
    }

    /**
     * 通过id查找商品
     *
     * @param id
     * @return
     */
    public Goods findGoodByID(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Goods goods = mongoTemplate.findOne(query, Goods.class, "goods");
        return goods;
    }


    /**
     * 获取某个商城中的所有商品
     * @param id
     * @return
     */
    public List<JSONObject> getAllGoodsByMallID(String id) {
        Mall mall = mallService.getMallByMallID(id);
        List<JSONObject> ans = new ArrayList<>();
        mall.getGoodsList().forEach((k, v) -> {
            if (v.get("goodsStatus").equals("上架")) {
                JSONObject goods = Utils.toJSONObject(findGoodByID(k));
                goods.put("payMethod", v.get("payMethod").toString());
                goods.put("pointPrice", Double.parseDouble(v.get("pointPrice").toString()));
                goods.put("marketPrice", Double.parseDouble(v.get("marketPrice").toString()));
                goods.put("supplyPrice", Double.parseDouble(v.get("supplyPrice").toString()));
                goods.put("pointType", v.get("pointType").toString());
                goods.put("goodsStatus", v.get("goodsStatus").toString());
                ans.add(goods);
            }
        });
        return ans;
    }

    /**
     * 商城首页搜索
     *
     * @param mallID
     * @param rxg
     * @return
     */
    public List<JSONObject> search(String mallID, String rxg) {
        List<JSONObject> list = getAllGoodsByMallID(mallID);
        List<JSONObject> ans = new ArrayList<>();
        list.forEach(v -> {
            if (v.getString("name").contains(rxg)) {
                ans.add(v);
            }
        });
        return ans;
    }

    /**
     * 获取特定快捷入口的商品
     *
     * @param mallID
     * @param rxg
     * @return
     */
    public List<JSONObject> entranceGoods(String mallID, String rxg) {
        List<JSONObject> list = getAllGoodsByMallID(mallID);
        List<JSONObject> ans = new ArrayList<>();
        list.forEach(v -> {
            if (v.getString("goodsType").contains(rxg)) {
                ans.add(v);
            }
        });
        return ans;
    }

    /**
     * 获取某个专题下的所有商品信息
     *
     * @param mallID
     * @param subjectID
     * @return
     */
    public List<JSONObject> subjectGoods(String mallID, String subjectID) {
        Mall mall = mallService.getMallByMallID(mallID);
        Subject subject = mall.getSubjectList().get(subjectID);
        List<String> list = subject.getGoodsList();
        List<JSONObject> ans = new ArrayList<>();
        for (String goodsID : list) {
            ans.add(getGoodsInfoByID(goodsID, mallID));
        }
        return ans;
    }
}
