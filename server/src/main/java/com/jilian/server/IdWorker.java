package com.jilian.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zhuanggangqing
 * @Description: ID生成器，用于生成独一无二的的订单id和商品id
 * @Date: Create in 4:05 下午 2020/2/21
 */
public class IdWorker {
    private static final AtomicRangeInteger atomicRangeInteger = new AtomicRangeInteger(0, 1 << 31 - 1);

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static String getGoodsID() {
        final long currentTimeMillis = System.currentTimeMillis();
        return "" + (currentTimeMillis << 30 | atomicRangeInteger.incrAndGet());
    }

    public static String getOrderID() {
        final long currentTimeMillis = System.currentTimeMillis();
        Date a = new Date(currentTimeMillis);
        return simpleDateFormat.format(a) + (long) (currentTimeMillis << 2 | atomicRangeInteger.incrAndGet());
    }

    static class AtomicRangeInteger {
        private final int maxValue;
        private final AtomicInteger atomicInteger;

        public AtomicRangeInteger(final int minValue, final int maxValue) {
            this.atomicInteger = new AtomicInteger(minValue);
            this.maxValue = maxValue;
        }

        public Integer incrAndGet() {
            final int value = this.atomicInteger.incrementAndGet();
            if (value > maxValue) {
                return null;
            }

            return value;
        }
    }
}