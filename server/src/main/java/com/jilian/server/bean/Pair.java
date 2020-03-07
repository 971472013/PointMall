package com.jilian.server.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 11:58 下午 2020/2/8
 */
@Data
@AllArgsConstructor
public class Pair<K, V> implements Serializable {
    private static final long serialVersionUID = -3826183030300924393L;

    private K key;
    private V value;
}
