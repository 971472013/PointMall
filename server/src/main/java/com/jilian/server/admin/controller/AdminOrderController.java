package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminOrderService;
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
 * @Date: Create in 5:13 下午 2020/2/15
 */
@RestController
public class AdminOrderController {
    @Autowired
    AdminOrderService orderService;

    /**
     * 获得商城中的所有订单
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getAllOrderByMallID")
    public Result getAllOrderByMallID(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .getAllOrderByMallID(jsonObject.getString("mallID")));
    }

    /**
     * 更新订单信息
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updateOrder")
    public Result updateOrder(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .updateOrder(jsonObject));
    }

    /**
     * 获得近一个月内的订单
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getNearMonthOrder")
    public Result getNearMonthOrder(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .getNearMonthOrder(jsonObject.getString("mallID")));
    }

    /**
     * 同意退款申请
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/backConfirm")
    public Result backConfirm(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .backConfirm(jsonObject));
    }

    /**
     * 驳回退款申请
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/rejectBack")
    public Result rejectBack(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .rejectBack(jsonObject));
    }

    /**
     * 按条件筛选订单
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/filterOrder")
    public Result filterOrder(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(orderService
                .filterOrder(jsonObject));
    }
}
