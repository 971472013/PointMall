package com.jilian.server.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 8:59 下午 2020/2/8
 */
@Data
@AllArgsConstructor
public class Address implements Serializable {
    private static final long serialVersionUID = 3870531219430226259L;
    private String eid;         //仅个人地址管理用id
    private String name;
    private String tel;
    private String province;
    private String city;
    private String county;
    private String addressDetail;
    private String address;
    private String areaCode;
    private boolean adefault;
    private String postalCode;


}