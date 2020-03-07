package com.jilian.server.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.jwt.PASS;
import com.jilian.server.mall.service.MallGoodsService;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:31 下午 2020/2/18
 */
@RestController
public class MallGoodsController {
    @Autowired
    MallGoodsService goodsService;

    /**
     * 获取商城中的所有商品信息
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/getAllGoodsByMallID")
    @PASS
    public Result getAllGoodsByMallID(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(goodsService.getAllGoodsByMallID(data.getString("mallID")));
    }

    /**
     * 获取特定商品的详细信息
     * @param data
     * @return
     */
    @PostMapping("/api/mall/getGoodsInfoByID")
    @PASS
    public Result getGoodsInfoByID(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(goodsService.getGoodsInfoByID(data.getString("id"), data.getString("mallID")));
    }

    /**
     * 搜索商品
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/search")
    @PASS
    public Result search(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(goodsService.search(data.getString("mallID"), data.getString("rxg")));
    }

    /**
     * 快捷入口下的商品
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/entranceGoods")
    @PASS
    public Result entranceGoods(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(goodsService.entranceGoods(data.getString("mallID"), data.getString("rxg")));
    }

    /**
     * 专题下的商品
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/subjectGoods")
    @PASS
    public Result subjectGoods(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(goodsService.subjectGoods(data.getString("mallID"), data.getString("subjectID")));
    }
}
