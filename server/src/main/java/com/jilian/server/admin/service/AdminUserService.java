package com.jilian.server.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.bean.User;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:54 下午 2020/2/5
 */
@Service
public class AdminUserService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据用户名查询user
     *
     * @param username
     * @return
     */
    public User findUserByUsername(String username){
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query,User.class,"admin_user");
    }

    /**
     * 根据token查询user
     * @param token
     * @return
     */
    public User findUserByToken(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("token").is(token));
        return mongoTemplate.findOne(query, User.class, "admin_user");
    }

    /**
     * admin端登录
     * @param object
     * @return
     */
    public Result login(JSONObject object) {
        String username = object.getString("username");
        String password = object.getString("password");
        User user = findUserByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        } else {
            if (!user.getPassword().equals(password)) {
                return ResultFactory.buildFailResult("密码不正确");
            } else {
                JSONObject data = new JSONObject();
                data.put("token", user.getToken());
                return ResultFactory.buildSuccessResult(data);
            }
        }
    }

    /**
     * 加载user信息
     * @param token
     * @return
     */
    public Result loadInfo(String token) {
        User user = findUserByToken(token);
        JSONObject object = new JSONObject();
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        } else {
            object.put("userInfo", user.getUserInfo());
            object.put("mallList", user.getMallList());
            object.put("id", user.getId());
            return ResultFactory.buildSuccessResult(object);
        }
    }
}
