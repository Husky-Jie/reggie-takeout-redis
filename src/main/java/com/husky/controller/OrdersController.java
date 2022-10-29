package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.BaseContext;
import com.husky.common.R;
import com.husky.entity.OrderDetail;
import com.husky.entity.Orders;
import com.husky.service.OrderDetailService;
import com.husky.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/26
 * Time: 23:14
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.insertOrders(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(Integer page,Integer pageSize){
        Page<Orders> userPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(userPage,queryWrapper);
        return R.success(userPage);
    }

    @GetMapping("/page")
    public R<Page<Orders>> Page(Integer page, Integer pageSize,
                                Long number, @DateTimeFormat String beginTime, @DateTimeFormat String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null,Orders::getNumber,number);
        queryWrapper.ge(beginTime != null,Orders::getOrderTime,beginTime);
        queryWrapper.le(endTime != null,Orders::getOrderTime,endTime);
        ordersService.page(ordersPage,queryWrapper);
        return R.success(ordersPage);
    }

    @PutMapping
    public R<Orders> put(@RequestBody Orders orders) {
        Long id = orders.getId();
        Orders ordersServiceById = ordersService.getById(id);
        if (ordersServiceById != null) {
            orders.setStatus(3);
            ordersService.updateById(orders);
        }else {
            return R.error("该订单不存在");
        }
        return R.success(orders);
    }
}
