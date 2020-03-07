package com.jilian.server.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.jwt.PASS;
import com.jilian.server.mall.service.MallService;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:50 下午 2020/2/18
 */
@RestController
public class MallController {
    @Autowired
    MallService mallService;

    /**
     * 加载商城信息
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/getMallInfo")
    @PASS
    public Result getMallInfo(@RequestBody JSONObject data) {
        return ResultFactory.buildSuccessResult(mallService.getMallWithSubjectGoods(data.getString("mallID")));
    }
}
