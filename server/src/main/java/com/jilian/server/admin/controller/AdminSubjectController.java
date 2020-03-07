package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminSubjectService;
import com.jilian.server.jwt.PASS;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 8:49 下午 2020/2/14
 */
@RestController
public class AdminSubjectController {
    @Autowired
    AdminSubjectService subjectService;

    /**
     * 新建专题
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/createSubject")
    public Result createSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .createSubject(jsonObject.getString("mallID"), jsonObject.getJSONObject("data")));
    }

    /**
     * 获取商城中的所有专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getSubjectByMallID")
    public Result getSubjectByMallID(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .getSubjectByMallID(jsonObject.getString("mallID")));
    }

    /**
     * 更新专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updateSubject")
    public Result updateSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .updateSubject(jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID"),
                        jsonObject.getJSONObject("data")));
    }

    /**
     * 删除专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/deleteSubject")
    public Result deleteSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .deleteSubject(jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID")));
    }

    /**
     * 添加商品至专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/addGoodsToSubject")
    public Result addGoodsToSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .addGoodsToSubject(jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID"),
                        jsonObject.getString("goodsID")));
    }

    /**
     * 在专题中移动商品顺序
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/goodsMove")
    public Result goodsMove(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .goodsMove(jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID"),
                        jsonObject.getString("goodsID"),
                        jsonObject.getBooleanValue("up")));
    }

    /**
     * 将商品移出专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/moveOut")
    public Result moveOut(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .moveOut(jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID"),
                        jsonObject.getString("goodsID")));
    }

    /**
     * 根据是否存在该专题中获取商品信息
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getMallGoodsWithSubject")
    public Result getMallGoodsWithSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .getMallGoodsWithSubject(jsonObject.getString("userID"),
                        jsonObject.getString("mallID"),
                        jsonObject.getString("subjectID"),
                        jsonObject.getBooleanValue("inSubject")));
    }

    /**
     * 根据条件筛选专题
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/filterSubject")
    public Result filterSubject(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .filterSubject(jsonObject));
    }

    /**
     * 在专题中根据条件筛选商品
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/filterGoodsInSubject")
    public Result filterGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(subjectService
                .filterGoodsInSubject(jsonObject));
    }
}