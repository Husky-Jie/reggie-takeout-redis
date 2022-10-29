package com.husky.common;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 10:34
 */

/*
* 基于ThreadLocal封装工具类，保存用户和获取用户当前的id*/
public class BaseContext {
    // 每个线程单独保存一个副本
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 设置用户id
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    // 获取用户id
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
