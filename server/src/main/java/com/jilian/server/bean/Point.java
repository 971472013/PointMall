package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 5:56 下午 2020/2/11
 */
@AllArgsConstructor
@Data
public class Point implements Serializable {
    private static final long serialVersionUID = -8057490101904556203L;

    private String name;
    private double exchangeRate;    //积分/rmb == 1
    private boolean fixedRate;
    private JSONObject logo;

    private String ddl;             //"无"为永久
    private String total;           //"无"为无上限
    private String publisher;
    private JSONObject image;
}
