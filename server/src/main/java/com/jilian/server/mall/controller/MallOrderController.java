package com.jilian.server.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.jwt.BLOCK;
import com.jilian.server.mall.service.MallOrderService;
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
 * @Date: Create in 7:00 下午 2020/2/9
 */
@RestController
public class MallOrderController {
    @Autowired
    MallOrderService mallOrderService;

    /**
     * 更新订单状态
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/updateOrderStatus")
    @BLOCK
    public Result updateOrderStatus(@RequestBody JSONObject data) {
        boolean mark = mallOrderService.updateOrderStatus(data);
        if (mark) {
            return ResultFactory.buildResult(ResultCode.SUCCESS, "更新成功", null);
        } else {
            return ResultFactory.buildFailResult("更新失败");
        }
    }

    /**
     * 对订单申请退款
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/backOrderGoods")
    @BLOCK
    public Result backOrderGoods(@RequestBody JSONObject data) {
        boolean mark = mallOrderService.backOrderGoods(data);
        if (mark) {
            return ResultFactory.buildResult(ResultCode.SUCCESS, "更新成功", null);
        } else {
            return ResultFactory.buildFailResult("更新失败");
        }
    }

    /**
     * 生成新订单
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/createOneOrder")
    @BLOCK
    public Result createOneOrder(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(mallOrderService.createOneOrder(data));
    }

    /**
     * 获取该用户的所有订单
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/getAllOrderByCustomerID")
    @BLOCK
    public Result getAllOrderByCustomerID(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(mallOrderService.getAllOrderByCustomerID(data.getString("customerID")));
    }
}
