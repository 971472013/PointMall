package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:19 下午 2020/2/17
 */
@Data
@AllArgsConstructor
public class Entrance {
    private String eid;

    private String name;
    private JSONObject image;
    private String goodsType;
//    private String link;
}
