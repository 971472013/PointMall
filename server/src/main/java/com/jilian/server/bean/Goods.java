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
 * @Date: Create in 9:36 下午 2020/2/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {
    private static final long serialVersionUID = 1380064676640140047L;
    @Id
    private String id;

    private String name;
    private String describe;
    private String goodsType;
    private double marketPrice;

    private double supplyPrice;

    private String source;
    private int stockCount;

    private String supplier;

    private List<JSONObject> imageList;

    private String belongUser;

    private String needDistribution;

}
//        skuid:'',

//                supplier:''