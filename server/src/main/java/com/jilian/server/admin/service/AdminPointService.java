package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.*;
import com.jilian.server.mall.service.MallCustomerService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 9:27 下午 2020/2/11
 */
@Service
public class AdminPointService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private MallCustomerService mallCustomerService;

    /**
     * 获取商城中的积分信息
     *
     * @param id
     * @return
     */
    public Point getPointInfoByMallID(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Mall mall = mongoTemplate.findOne(query, Mall.class, "mall");
        System.out.println(JSONObject.toJSON(mall));
        return mall.getPoint();
    }

    /**
     * 更新积分信息
     * @param id
     * @param data
     * @return
     */
    public boolean updatePointByMallID(String id, JSONObject data) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("point", data);
        mongoTemplate.upsert(query, update, "mall");
        return true;
    }

    /**
     * 删除积分信息
     * @param mallID
     * @return
     */
    public boolean deletePointByMallID(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(mallID));
        Update update = new Update();
        update.set("point", null);
        mongoTemplate.upsert(query, update, "mall");
        return true;
    }

    /**
     * 单个用户积分发放
     * @param jsonObject
     * @return
     */
    public boolean distributePointOne(JSONObject jsonObject) {
        String recordID = new ObjectId().toString();
        String mallID = jsonObject.getString("mallID");
        LocalDateTime createTime = LocalDateTime.now();
        String phone = jsonObject.getString("phone");
        double quantity = Double.parseDouble(jsonObject.get("quantity").toString());

        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(phone).and("belongMall").is(mallID));
        Customer customer = mongoTemplate.findOne(query, Customer.class, "mall_customer");
        if (customer == null) {
            return false;
        }
        Update update = new Update();
        update.inc("remain", quantity);
        mongoTemplate.upsert(query, update, "mall_customer");

        List<Pair<String, Double>> disList = new ArrayList<>();
        disList.add(new Pair<>(phone, quantity));
        PointRecord pointRecord = new PointRecord(recordID, mallID, createTime, disList, quantity, 1, "系统管理员");
        mongoTemplate.insert(pointRecord, "point_record");
        return true;
    }

    /**
     * 生成新用户并发放积分
     * @param jsonObject
     * @return
     */
    public boolean newCustomerDistributePointOne(JSONObject jsonObject) {
        String recordID = new ObjectId().toString();
        String customerID = new ObjectId().toString();
        String mallID = jsonObject.getString("mallID");
        LocalDateTime createTime = LocalDateTime.now();
        String phone = jsonObject.getString("phone");
        double quantity = Double.parseDouble(jsonObject.get("quantity").toString());

        Customer customer = new Customer(customerID, phone, phone, "00000", LocalDateTime.now().plusMinutes(5),
                "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), quantity, mallID, LocalDateTime.now());
        mongoTemplate.insert(customer, "mall_customer");

        List<Pair<String, Double>> disList = new ArrayList<>();
        disList.add(new Pair<>(phone, quantity));
        PointRecord pointRecord = new PointRecord(recordID, mallID, createTime, disList, quantity, 1, "系统管理员");
        mongoTemplate.insert(pointRecord, "point_record");
        return true;
    }

    /**
     * 获得积分发放历史记录
     * @param mallID
     * @return
     */
    public List<PointRecord> loadDistributeRecord(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mallID").is(mallID));
        query.with(Sort.by(Sort.Order.desc("createTime")));
        return mongoTemplate.find(query, PointRecord.class, "point_record");
    }

    /**
     * 查询特定用户的当前积分
     * @param mallID
     * @param phone
     * @return
     */
    public JSONObject queryPointByPhone(String mallID, String phone) {
        Customer customer = mallCustomerService.findCustomerByPhoneAndMall(mallID, phone);
        if (customer == null) {
            return null;
        }
        JSONObject data = new JSONObject();
        data.put("id", customer.getId());
        data.put("phone", customer.getPhone());
        data.put("remain", customer.getRemain());
        return data;
    }

    /**
     * 查询所有用户的当前积分
     *
     * @param mallID
     * @return
     */
    public List<JSONObject> queryAllPoint(String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("belongMall").is(mallID));
        List<Customer> list = mongoTemplate.find(query, Customer.class, "mall_customer");
        if (list == null) {
            list = new ArrayList<>();
        }
        List<JSONObject> ans = new ArrayList<>();
        for (Customer customer : list) {
            JSONObject data = new JSONObject();
            data.put("id", customer.getId());
            data.put("phone", customer.getPhone());
            data.put("remain", customer.getRemain());
            ans.add(data);
        }
        return ans;
    }

    /**
     * 批量发放积分
     *
     * @param file
     * @param mallID
     * @return
     */
    public List<PointRecord> distributeAll(MultipartFile file, String mallID) {
        String txt = Utils.convertStreamToString(file).split("###############################")[1];
        String[] allPhone = txt.split("BEGIN:\n")[1].split("\nEND;")[0].split("\n");
        String pattern = "^((13[0-9])|(14[579])|(15([0-3,5-9]))|(16[6])|(17[0135678])|(18[0-9]|19[89]))\\d{8}$";
        Pattern p = Pattern.compile(pattern);
        for (String tem : allPhone) {
            try {
                String phone = tem.split("。")[0];
                Double q = Double.parseDouble(tem.split("。")[1]);
                if (p.matcher(phone).matches()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("phone", phone);
                    jsonObject.put("quantity", q);
                    jsonObject.put("mallID", mallID);
                    if (!distributePointOne(jsonObject)) {
                        newCustomerDistributePointOne(jsonObject);
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }
        return loadDistributeRecord(mallID);
    }
}
