package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.admin.service.AdminUserService;
import com.jilian.server.jwt.PASS;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 9:32 下午 2020/2/10
 */
@RestController
public class AdminUserController {
    @Autowired
    AdminUserService adminUserService;

    /**
     * admin端登录
     *
     * @param data
     * @return
     */
    @PostMapping("/api/admin/login")
    @PASS
    public Result login(@RequestBody JSONObject data) {
        return adminUserService.login(data);
    }

    /**
     * admin端登出
     * @return
     */
    @PostMapping("/api/admin/logout")
    @PASS
    public Result logout() {
        return ResultFactory.buildSuccessResult(null);
    }

    /**
     * 加载user的信息
     * @param token
     * @return
     */
    @GetMapping("/api/admin/loadInfo")
    @PASS
    public Result loadInfo(@RequestHeader("token") String token) {
        return adminUserService.loadInfo(token);
    }
}
