package com.jilian.server.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 2:53 下午 2020/2/9
 */
@Data
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 6758910769042216348L;
    @Id
    private String id;

    private String customerPhone;
    private String customerID;
    private String userID;
    private String mallID;

    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime createTime;
    private PayMethod payMethod;


    private Map<String, OrderGoods> orderGoodsList;

    private double totalMarketPrice;
    private double totalPointPrice;

    private int totalNum;
    private String pointType;

    private Address address;

    private OrderGoods orderGoods;            //将order按orderGoodsList拆分为单个，分为多个子订单

    private double express;
    private double pointExpress;
}