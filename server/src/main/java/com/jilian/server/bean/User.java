package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 12:28 下午 2020/1/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 4589088342695953229L;
    @Id
    private String id;
    private String username;
    private String password;
    private String token;
    private UserInfo userInfo;
    private List<JSONObject> mallList;
}
