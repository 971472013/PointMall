package com.jilian.server.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.jwt.BLOCK;
import com.jilian.server.mall.service.MallCustomerService;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultCode;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 8:52 下午 2020/2/8
 */
@RestController
public class MallCustomerController {
    @Autowired
    MallCustomerService mallCustomerService;

    /**
     * 商城端加载用户信息
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/loadInfo")
    @BLOCK
    public Result loadInfo(@RequestBody JSONObject data) {
        JSONObject jsonObject = mallCustomerService.getInfo(data.getString("mallID"), data.getString("phone"));
        return ResultFactory.buildSuccessResult(jsonObject);
    }

    /**
     * 更新用户地址列表
     * @param data
     * @return
     */
    @PostMapping("/api/mall/updateAddress")
    @BLOCK
    public Result updateAddress(@RequestBody JSONObject data) {
        if (mallCustomerService.updateAddress(data)) {
            return ResultFactory.buildResult(ResultCode.SUCCESS, "修改成功", null);
        } else {
            return ResultFactory.buildFailResult("修改失败");
        }
    }

    /**
     * 更新用户购物车列表
     * @param data
     * @return
     */
    @PostMapping("/api/mall/updateCartGoods")
    @BLOCK
    public Result updateCartGoods(@RequestBody JSONObject data) {
        if (mallCustomerService.updateCartGoods(data)) {
            return ResultFactory.buildResult(ResultCode.SUCCESS, "修改成功", null);
        } else {
            return ResultFactory.buildFailResult("修改失败");
        }
    }
}
