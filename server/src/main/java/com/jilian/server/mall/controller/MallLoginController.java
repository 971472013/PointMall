package com.jilian.server.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.bean.Mall;
import com.jilian.server.jwt.PASS;
import com.jilian.server.mall.service.MallLoginService;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 11:18 上午 2020/2/5
 */
@RestController
public class MallLoginController {
    @Autowired
    private MallLoginService mallLoginService;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 登录
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/login")
    @PASS
    public Result login(@RequestBody JSONObject data){
        return mallLoginService.loginWithSMS(data.getString("mallID"),
                data.getString("phone"), data.getString("sms"));
    }

    /**
     * 发送验证码
     *
     * @param data
     * @return
     */
    @PostMapping("/api/mall/sms")
    @PASS
    public Result sendSMS(@RequestBody JSONObject data) {
        if (mallLoginService.sendSMS(data)) {
            return ResultFactory.buildSuccessResult("");
        } else {
            return ResultFactory.buildFailResult("失败");
        }
    }

    /**
     * 访问此链接生成测试数据
     * @return
     */
    @GetMapping("/generateData")
    @PASS
    public String generateData() {
        Query query = new Query();
        mallLoginService.generateData();
        Mall mall = mongoTemplate.findOne(query, Mall.class, "mall");
        return "数据清理完毕！测试数据生成成功！\n 商城地址：http://120.77.183.132:9529/mall/#/index?mallID=" + mall.getId();
    }
}
