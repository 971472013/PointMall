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
import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 5:51 下午 2020/2/3
 */
@Data
@AllArgsConstructor
public class Customer implements Serializable {
    private static final long serialVersionUID = -1266555002158656670L;

    @Id
    private String id;

    private String phone;

    private String password;
    private String sms;

    @DateTimeFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime smsDDL;

    private String token;
    private List<Address> addressList;
    private List<Pair<String, Integer>> goodsList;       //购物车
    private List<String> orderList;

    private double remain;
    private String belongMall;
    private LocalDateTime lastLogin;

}
