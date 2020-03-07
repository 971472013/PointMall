package com.jilian.server.bean;

/**
 * @Author: zhuanggangqing
 * @Description: 支付方式
 * @Date: Create in 2:57 下午 2020/2/9
 */
public enum PayMethod {
    //积分+钱
    POINT_CASH(1),

    //仅积分
    ONLY_POINT(2),

    //仅钱
    ONLY_CASH(3);


    private int code;

    PayMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PayMethod valueOf(int i) {
        switch (i) {
            case 1:
                return POINT_CASH;
            case 2:
                return ONLY_POINT;
            case 3:
                return ONLY_CASH;
            default:
                return null;
        }
    }
}
