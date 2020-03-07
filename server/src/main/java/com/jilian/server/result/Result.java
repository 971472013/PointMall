package com.jilian.server.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: zhuanggangqing
 * @Description: 自定义返回数据格式
 * @Date: Create in 11:11 上午 2020/2/5
 */
@Data
@AllArgsConstructor
public class Result {
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应提示信息
     */
    private String message;
    /**
     * 响应结果对象
     */
    private Object data;
}
