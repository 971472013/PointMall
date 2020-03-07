package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jilian.server.Utils;
import com.jilian.server.bean.Mall;
import com.jilian.server.bean.Subject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 7:53 下午 2020/2/14
 */
@Service
public class AdminSubjectService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AdminMallInfoService mallInfoService;
    @Autowired
    AdminGoodsService goodsService;

    /**
     * 新建专题
     *
     * @param mallID
     * @param jsonObject
     * @return
     */
    public Subject createSubject(String mallID, JSONObject jsonObject) {
        String id = new ObjectId().toString();
        Subject subject = new Subject(id, jsonObject.get("name").toString(), jsonObject.getJSONObject("image"), jsonObject.get("type").toString(),
                "上架", jsonObject.get("describe").toString(), LocalDateTime.now(), new ArrayList<>());
        Mall mall = mallInfoService.getMallByMallID(mallID);
        mall.getSubjectList().put(id, subject);
        List<String> newOrder = new ArrayList<>();
        newOrder.add(id);
        newOrder.addAll(mall.getSubjectOrder());
        mall.setSubjectOrder(newOrder);
        mallInfoService.updateMall(mall);
        return subject;
    }

    /**
     * 获取商城下所有专题
     * @param mallID
     * @return
     */
    public List<Subject> getSubjectByMallID(String mallID) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        List<Subject> ans = new ArrayList<>();
        mall.getSubjectList().forEach((k, v) -> {
            v.setEid(k);
            ans.add(v);
        });
        return ans;
    }

    /**
     * 按条件筛选专题
     * @param jsonObject
     * @return
     */
    public List<Subject> filterSubject(JSONObject jsonObject) {
        String mallID = jsonObject.getString("mallID");
        String name = jsonObject.getString("subjectName");
        JSONArray arr = jsonObject.getJSONArray("time");
        List<Subject> list = getSubjectByMallID(mallID);
        List<Subject> ans = new ArrayList<>();
        for (Subject i : list) {
            if (i.getName().contains(name)) {
                if (arr != null) {
                    LocalDateTime start = LocalDateTime.parse(arr.getString(0), Utils.worldNormalZ);
                    LocalDateTime end = LocalDateTime.parse(arr.getString(1), Utils.worldNormalZ);
                    if (i.getCreateTime().isAfter(start) &&
                            i.getCreateTime().isBefore(end)) {
                        ans.add(i);
                    }
                } else {
                    ans.add(i);
                }
            }
        }
        return ans;
    }

    /**
     * 更新专题信息
     * @param mallID
     * @param subjectID
     * @param jsonObject
     * @return
     */
    public boolean updateSubject(String mallID, String subjectID, JSONObject jsonObject) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Subject> map = mall.getSubjectList();
        Subject subject = Utils.JSONObject2subject(jsonObject);
        map.put(subjectID, subject);
        if (subject.getStatus().equals("上架")) {
            mall.getSubjectOrder().add(subjectID);
        } else {
            mall.getSubjectOrder().remove(subjectID);
        }
        mallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 删除专题
     * @param mallID
     * @param subjectID
     * @return
     */
    public boolean deleteSubject(String mallID, String subjectID) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Subject> map = mall.getSubjectList();
        map.remove(subjectID);
        mall.getSubjectOrder().remove(subjectID);
        mallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 将商品加入专题中
     * @param mallID
     * @param subjectID
     * @param goodsID
     * @return
     */
    public boolean addGoodsToSubject(String mallID, String subjectID, String goodsID) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Subject> map = mall.getSubjectList();
        Subject subject = map.get(subjectID);
        List<String> newList = new ArrayList<>();
        newList.add(goodsID);
        newList.addAll(subject.getGoodsList());
        subject.setGoodsList(newList);
        mallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 在专题中移动商品顺序
     * @param mallID
     * @param subjectID
     * @param goodsID
     * @param up
     * @return
     */
    public boolean goodsMove(String mallID, String subjectID, String goodsID, boolean up) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Subject> map = mall.getSubjectList();
        Subject subject = map.get(subjectID);
        List<String> list = subject.getGoodsList();
        int index = list.indexOf(goodsID);
        String[] strings = list.toArray(new String[0]);
        if (up) {
            String over = strings[index - 1];
            strings[index - 1] = strings[index];
            strings[index] = over;
        } else {
            String next = strings[index + 1];
            strings[index + 1] = strings[index];
            strings[index] = next;
        }
        subject.setGoodsList(Arrays.asList(strings));
        mallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 将商品移出专题
     * @param mallID
     * @param subjectID
     * @param goodsID
     * @return
     */
    public boolean moveOut(String mallID, String subjectID, String goodsID) {
        Mall mall = mallInfoService.getMallByMallID(mallID);
        Map<String, Subject> map = mall.getSubjectList();
        Subject subject = map.get(subjectID);
        List<String> list = subject.getGoodsList();
        list.remove(goodsID);
        mallInfoService.updateMall(mall);
        return true;
    }

    /**
     * 根据是否在特定专题中获得商城商品信息
     * @param userID
     * @param mallID
     * @param subjectID
     * @param inSubject
     * @return
     */
    public List<JSONObject> getMallGoodsWithSubject(String userID, String mallID, String subjectID, boolean inSubject) {
        List<JSONObject> goodsList = goodsService.getGoodsByMallID(userID, mallID);
        List<JSONObject> ans = new ArrayList<>();
        Mall mall = mallInfoService.getMallByMallID(mallID);
        List<String> list = mall.getSubjectList().get(subjectID).getGoodsList();
        JSONObject[] temArray = new JSONObject[list.size()];
        int n = goodsList.size();
        for (int i = 0; i < n; i++) {
            JSONObject temp = goodsList.get(i);
            int index = list.indexOf(temp.get("id").toString());
            if (inSubject) {
                if (index >= 0) {
                    temArray[index] = temp;
                }
            } else {
                if (index >= 0) {
                    continue;
                }
                ans.add(temp);
            }
        }
        if (inSubject) {
            ans = Arrays.asList(temArray);
        }
        return ans;
    }

    /**
     * 在商品专题中筛选商品
     * @param data
     * @return
     */
    public List<JSONObject> filterGoodsInSubject(JSONObject data) {
        String userID = data.getString("userID");
        String mallID = data.getString("mallID");
        String subjectID = data.getString("subjectID");
        boolean inSubject = data.getBooleanValue("inSubject");
        String type = data.getString("goodsType");
        String name = data.getString("goodsName");
        String supplier = data.getString("supplier");
        List<JSONObject> goods = getMallGoodsWithSubject(userID, mallID, subjectID, inSubject);
        List<JSONObject> ans = new ArrayList<>();
        for (JSONObject i : goods) {
            if ((type.equals("全部") || type.equals(i.getString("goodsType"))) &&
                    (supplier.equals("全部") || supplier.equals(i.getString("supplier"))) &&
                    (i.getString("name").contains(name))) {
                ans.add(i);
            }
        }
        return ans;
    }
}
