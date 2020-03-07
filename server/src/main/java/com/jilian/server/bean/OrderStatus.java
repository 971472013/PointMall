package com.jilian.server.bean;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 3:15 下午 2020/2/9
 */
public enum OrderStatus {
    //待支付
    待支付(1),
    //待发货
    待发货(2),
    待收货(3),
    已取消(4),
    已完成(5),
    订单失败(6),
    商户支付失败(7),
    支付成功通知失败(8),
    售后中(9),
    订单关闭(10),
    退款失败(11);
    int code;

    OrderStatus(int code) {
        this.code = code;
    }
}
