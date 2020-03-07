package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminPointService;
import com.jilian.server.bean.Point;
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
 * @Date: Create in 9:27 下午 2020/2/11
 */
@RestController
public class AdminPointController {
    @Autowired
    AdminPointService adminPointService;

    /**
     * 获取积分信息
     *
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getPointInfo")
    public Result getPointInfo(@RequestBody JSONObject object) {
        Point data = adminPointService.getPointInfoByMallID(object.getString("mallID"));
        return ResultFactory.buildSuccessResult(data);
    }

    /**
     * 更新积分
     *
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updatePoint")
    public Result updatePoint(@RequestBody JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        String mallID = object.getString("mallID");
        System.out.println(mallID);
        if (adminPointService.updatePointByMallID(mallID, data)) {
            return ResultFactory.buildSuccessResult(data);
        } else {
            return ResultFactory.buildFailResult("更新失败");
        }
    }

    /**
     * 删除积分
     *
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/deletePoint")
    public Result deletePoint(@RequestBody JSONObject object) {
        String mallID = object.getString("mallID");
        if (adminPointService.deletePointByMallID(mallID)) {
            return ResultFactory.buildResult(200, "删除成功", null);
        } else {
            return ResultFactory.buildFailResult("删除失败");
        }
    }

    /**
     * 发放积分
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/distributePointOne")
    public Result distributePointOne(@RequestBody JSONObject object) {
        if (adminPointService.distributePointOne(object)) {
            return ResultFactory.buildResult(200, "发放成功",
                    adminPointService.loadDistributeRecord(object.getString("mallID")));
        } else {
            return ResultFactory.buildSuccessResult(null);
        }
    }

    /**
     * 新建用户并发放积分
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/newCustomerDistributePointOne")
    public Result newCustomerDistributePointOne(@RequestBody JSONObject object) {
        if (adminPointService.newCustomerDistributePointOne(object)) {
            return ResultFactory.buildResult(200, "发放成功",
                    adminPointService.loadDistributeRecord(object.getString("mallID")));
        } else {
            return ResultFactory.buildSuccessResult(null);
        }
    }

    /**
     * 获取积分历史发放记录
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/loadDistributeRecord")
    public Result loadDistributeRecord(@RequestBody JSONObject object) {
        return ResultFactory.buildSuccessResult(adminPointService.loadDistributeRecord(object.getString("mallID")));
    }

    /**
     * 查询用户积分
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/queryPoint")
    public Result queryPoint(@RequestBody JSONObject object) {
        return ResultFactory.buildSuccessResult(adminPointService.queryPointByPhone(object.getString("mallID"), object.getString("phone")));
    }

    /**
     * 查询所有用户积分
     *
     * @param object
     * @return
     */
    @PASS
    @PostMapping("/api/admin/queryAllPoint")
    public Result queryAllPoint(@RequestBody JSONObject object) {
        return ResultFactory.buildSuccessResult(adminPointService.queryAllPoint(object.getString("mallID")));
    }
}