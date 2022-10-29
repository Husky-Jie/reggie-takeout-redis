package com.husky.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/11
 * Time: 17:52
 */

/*
* 自定义元数据对象处理器
* 处理公共字段
* */
@Component
@Slf4j
public class MyObjectHandler implements MetaObjectHandler {

    // 公共字段填充insert
    /*
    * 重写方法中不能使用HttpSession获取用户id
    * 使用ThreadLocal封装工具类，保存用户和获取用户当前的id
    * 首先在过滤器的登录判断中用ThreadLocal封装工具类中设置用户id
    * 最后在公共字段处理器的重写方法中获取用户id*/
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充insert");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    // 公共字段填充update
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
