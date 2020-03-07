package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminMallInfoService;
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
 * @Date: Create in 3:45 下午 2020/2/17
 */
@RestController
public class AdminMallController {
    @Autowired
    AdminMallInfoService mallInfoService;

    /**
     * 获取商城信息
     *
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getMallInfo")
    public Result getMallInfo(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(mallInfoService
                .getMallWithSubjectGoods(jsonObject.getString("mallID")));
    }

    /**
     * 更新商城装修
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updateMallFitment")
    public Result updateMallFitment(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(mallInfoService
                .updateMallFitment(jsonObject));
    }

    /**
     * 更新商城基本信息
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/updateMallBasic")
    public Result updateMallBasic(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(mallInfoService
                .updateMallBasic(jsonObject));
    }

    /**
     * 获取今天登陆的用户数量
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/getTodayCustomerNum")
    public Result getTodayCustomer(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(mallInfoService
                .getTodayCustomerNum(jsonObject.getString("mallID")));
    }

    /**
     * 新建商城
     * @param jsonObject
     * @return
     */
    @PASS
    @PostMapping("/api/admin/createMall")
    public Result createMall(@RequestBody JSONObject jsonObject) {
        return ResultFactory.buildSuccessResult(mallInfoService
                .createMall(jsonObject));
    }
}
