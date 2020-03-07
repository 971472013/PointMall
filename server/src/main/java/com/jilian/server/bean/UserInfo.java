package com.jilian.server.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 12:32 下午 2020/1/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 5632867650619985370L;
    private List<String> roles;
    private String introduction;
    private String avatar;
    private String name;
}