package com.jilian.server.mall.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.Mall;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:17 下午 2020/2/18
 */
@Service
public class MallService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MallGoodsService goodsService;

    /**
     * 获得商城信息
     *
     * @param mallID
     * @return
     */
    public Mall getMallByMallID(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        return mongoTemplate.findOne(query, Mall.class, "mall");
    }

    /**
     * 获得商城首页的信息，包含首页展示的专题下的物品
     * @param mallID
     * @return
     */
    public JSONObject getMallWithSubjectGoods(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        JSONObject ans = mongoTemplate.findOne(query, JSONObject.class, "mall");
        JSONObject idObject = ans.getJSONObject("_id");
        ObjectId objectId = new ObjectId(idObject.getIntValue("timestamp"),
                idObject.getIntValue("machineIdentifier"), idObject.getShortValue("processIdentifier"),
                idObject.getIntValue("counter"));
        ans.put("id", objectId.toString());
        ans.remove("_id");
        JSONObject subjectList = ans.getJSONObject("subjectList");
        JSONObject goodsList = ans.getJSONObject("goodsList");
        Set<String> subSet = subjectList.keySet();
        for (String subjectID : subSet) {
            JSONObject eachSubject = subjectList.getJSONObject(subjectID);
            JSONArray goodsInSub = eachSubject.getJSONArray("goodsList");
            int n = goodsInSub.size();
            List<JSONObject> showList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (i == 10) {
                    break;
                }
                String goodsID = goodsInSub.getString(i);
                JSONObject goodsInfo = Utils.toJSONObject(goodsService.findGoodByID(goodsID));
                JSONObject goodsInfoInMall = goodsList.getJSONObject(goodsID);
                goodsInfo.put("pointPrice", goodsInfoInMall.getDoubleValue("pointPrice"));
                goodsInfo.put("pointType", goodsInfoInMall.getString("pointType"));
                showList.add(goodsInfo);
            }
            eachSubject.put("showGoodsList", showList);
            subjectList.put(subjectID, eachSubject);
        }
        ans.put("subjectList", subjectList);
        return ans;
    }
}
