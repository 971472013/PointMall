package com.jilian.server.jwt;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.jilian.server.admin.service.AdminUserService;
import com.jilian.server.bean.Customer;
import com.jilian.server.bean.User;
import com.jilian.server.mall.service.MallCustomerService;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Author: zhuanggangqing
 * @Description: token认证
 * @Date: Create in 7:54 下午 2020/2/4
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    MallCustomerService mallCustomerService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PASS.class)) {
            PASS pass = method.getAnnotation(PASS.class);
            if (pass.required()) {
                return true;
            }
        }


        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(BLOCK.class)) {
            BLOCK block = method.getAnnotation(BLOCK.class);
            if (block.required()) {
                // 执行认证
                if (token == null) {
                    resetResp(httpServletResponse, "无token，请重新登录");
                    return false;
//                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 账号
                String type;
                try {
                    type = JWT.decode(token).getClaim("type").asString();
                    if (type.equals("user")) {
                        String username = JWT.decode(token).getClaim("username").asString();
                        User user = adminUserService.findUserByUsername(username);
                        if (user == null) {
                            resetResp(httpServletResponse, "用户不存在！");
                            return false;
                        }
                        Boolean verify = JwtUtil.isUserVerify(token, user.getPassword());
                        if (!verify) {
                            resetResp(httpServletResponse, "非法访问！");
                            return false;
                        }
                        return true;
                    } else {
                        String mallID = JWT.decode(token).getClaim("mallID").asString();
                        String phone = JWT.decode(token).getClaim("phone").asString();
                        Customer customer = mallCustomerService.findCustomerByPhoneAndMall(mallID, phone);
                        if (customer == null) {
                            resetResp(httpServletResponse, "用户不存在！");
                            return false;
                        }
                        Boolean verify = JwtUtil.isCustomerVerify(token, customer);
                        if (!verify) {
                            resetResp(httpServletResponse, "非法访问！");
                            return false;
                        }
                        return true;
                    }

                } catch (JWTDecodeException j) {
                    resetResp(httpServletResponse, "访问异常！");
                    return false;
//                    throw new RuntimeException("访问异常！");
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void resetResp(HttpServletResponse httpServletResponse, String message) throws IOException {
        httpServletResponse.reset();
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.write(JSON.toJSONString(ResultFactory.buildFailResult(message)));
        out.flush();
        out.close();
    }
}
