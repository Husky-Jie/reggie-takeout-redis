package com.husky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.husky.entity.Orders;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/26
 * Time: 23:11
 */
public interface OrdersService extends IService<Orders> {
    void insertOrders(Orders orders);
}
