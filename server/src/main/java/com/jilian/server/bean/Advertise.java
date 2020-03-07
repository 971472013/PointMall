package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 2:51 下午 2020/2/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Advertise {
    private String eid;

    private String name;
    private JSONObject image;
    private String link;
}
