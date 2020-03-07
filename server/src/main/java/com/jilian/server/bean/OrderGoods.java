package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:07 下午 2020/2/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderGoods implements Serializable {
    private static final long serialVersionUID = -6708519731176276788L;
    private String goodsID;
    private String name;
    private String describe;
    private String goodsType;
    //    private String supplierID;
    private String supplier;
    private JSONObject image;
    private double supplyPrice;
    private double marketPrice;
    private double pointPrice;
    private String pointType;
    private int num;

    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime payTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime outTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime receiveTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime doneTime;
    private String source;
    private String needDistribution;

    private String expressCompany;
    private String expressID;
    private OrderStatus orderStatus;

    private String backExpressCompany;
    private String backExpressID;

    private double express;
    private double pointExpress;
}