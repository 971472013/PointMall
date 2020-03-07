package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminGoodsService;
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
 * @Date: Create in 7:55 下午 2020/2/13
 */
@RestController
public class AdminGoodsController {
    @Autowired
    AdminGoodsService adminGoodsService;

    /**
     * 获得属于某个mallID的所有商品
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getMallGoods")
    public Result getMallGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .getGoodsByMallID(jsonObject.getString("userID"), jsonObject.getString("mallID")));
    }

    /**
     * 获得属于某个user的所有商品
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getAllGoods")
    public Result getAllGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .getAllGoods(jsonObject.getString("userID"), jsonObject.getString("mallID")));
    }

    /**
     * 更新商品上架状态
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/toggleGoodsStatus")
    public Result toggleGoodsStatus(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .toggleGoodsStatus(jsonObject.getString("mallID"),
                        jsonObject.getString("goodsID"), jsonObject.getString("to")));
    }

    /**
     * 删除商城中的商品
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/deleteMallGoods")
    public Result deleteMallGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .deleteMallGoods(jsonObject.getString("mallID"),
                        jsonObject.getString("goodsID")));
    }

    /**
     * 更新商品价格
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updatePrice")
    public Result updatePrice(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .updatePrice(jsonObject));
    }

    /**
     * 将商品加入商城中
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/addToMall")
    public Result addToMall(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .addToMall(jsonObject));
    }

    /**
     * 更新商品信息
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updateGoods")
    public Result updateGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .updateGoods(jsonObject));
    }

    /**
     * 按条件过滤商品
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/filterGoods")
    public Result filterGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .filterGoods(jsonObject));
    }

    /**
     * 新建商品
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/createGoods")
    public Result createGoods(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(adminGoodsService
                .createGoods(jsonObject));
    }
}
