package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.Customer;
import com.jilian.server.bean.Mall;
import com.jilian.server.bean.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 2:17 下午 2020/2/14
 */
@Service
public class AdminMallInfoService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AdminGoodsService goodsService;

    /**
     * 新建商城
     *
     * @param jsonObject
     * @return
     */
    public Mall createMall(JSONObject jsonObject) {
        String userID = jsonObject.getString("userID");
        JSONObject logo = jsonObject.getJSONObject("logo");
        String title = jsonObject.getString("title");
        String mallID = new ObjectId().toString();
        Mall mall = new Mall(mallID, null, new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(),
                true, 3, "#000000", new ArrayList<>(), title, logo, 50, 8, "", Utils.getEntrance());
        mongoTemplate.insert(mall, "mall");
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userID));
        User user = mongoTemplate.findOne(query, User.class, "admin_user");
        List<JSONObject> mallList = user.getMallList();
        mallList = mallList == null ? new ArrayList<>() : mallList;
        JSONObject newMall = new JSONObject();
        newMall.put("id", mallID);
        newMall.put("title", title);
        mallList.add(newMall);
        Update update = new Update();
        update.set("mallList", mallList);
        mongoTemplate.upsert(query, update, "admin_user");
        return mall;
    }

    /**
     * 获取商城基本信息
     * @param mallID
     * @return
     */
    public Mall getMallByMallID(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        return mongoTemplate.findOne(query, Mall.class, "mall");
    }

    /**
     * 获取今日登陆人数
     * @param mallID
     * @return
     */
    public int getTodayCustomerNum(String mallID) {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Query query = new Query();
        query.addCriteria(Criteria.where("belongMall").is(mallID).andOperator(
                Criteria.where("lastLogin").gte(start),
                Criteria.where("lastLogin").lte(end)
        ));
        List<Customer> list = mongoTemplate.find(query, Customer.class, "mall_customer");
        return list.size();
    }

    /**
     * 获取带有专题下商品的商城信息
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
                JSONObject goodsInfo = goodsService.getGoodsByID(goodsID);
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

    /**
     * 更新商城信息
     * @param mall
     * @return
     */
    public boolean updateMall(Mall mall) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mall.getId()));
        Update update = Utils.toUpdate(mall);
        mongoTemplate.upsert(query, update, "mall");
        return true;
    }

    /**
     * 更新商城装修信息
     * @param jsonObject
     * @return
     */
    public boolean updateMallFitment(JSONObject jsonObject) {
        String mallID = jsonObject.getString("id");
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        Update update = new Update();
        update.set("color", jsonObject.getString("color"));
        update.set("advertise", jsonObject.getJSONArray("advertise"));
        update.set("entrance", jsonObject.getJSONArray("entrance"));
        update.set("showList", jsonObject.getBooleanValue("showList"));
        update.set("interval", jsonObject.getDoubleValue("interval"));
        update.set("subjectOrder", jsonObject.getJSONArray("subjectOrder"));
        mongoTemplate.upsert(query, update, "mall");
        return true;
    }

    /**
     * 更新商城基本信息
     * @param jsonObject
     * @return
     */
    public boolean updateMallBasic(JSONObject jsonObject) {
        String mallID = jsonObject.getString("id");
        String userID = jsonObject.getString("userID");
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        Update update = new Update();
        update.set("title", jsonObject.getString("title"));
        update.set("logo", jsonObject.getJSONObject("logo"));
        update.set("over", jsonObject.getDoubleValue("over"));
        update.set("less", jsonObject.getDoubleValue("less"));
        update.set("servePhone", jsonObject.getString("servePhone"));
        mongoTemplate.upsert(query, update, "mall");

        update = new Update();
        query = new Query();
        query.addCriteria(Criteria.where("_id").is(userID));
        User user = mongoTemplate.findOne(query, User.class, "admin_user");
        user.getMallList().forEach(v -> {
            if (v.getString("id").equals(mallID)) {
                v.put("title", jsonObject.getString("title"));
            }
        });
        update.set("mallList", user.getMallList());
        mongoTemplate.upsert(query, update, "admin_user");
        return true;
    }
}
