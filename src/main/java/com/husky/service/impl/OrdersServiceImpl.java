package com.husky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.common.BaseContext;
import com.husky.common.CustomException;
import com.husky.entity.*;
import com.husky.mapper.OrdersMapper;
import com.husky.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/26
 * Time: 23:12
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ShoppingCartService shoppingCartService;
    @Resource
    private AddressBookService addressBookService;
    @Resource
    private UserService userService;

    @Transactional
    @Override
    public void insertOrders(Orders orders) {
        // 获取用户id
        Long userId = BaseContext.getCurrentId();

        // 设置随机订单号
        long number = IdWorker.getId();

        // 查询购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> listShopping = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (listShopping.size()==0) {
            throw new CustomException("购物车为空，不能下单");
        }

        // 查询用户地址
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getId,orders.getAddressBookId());
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        User user = userService.getById(userId);

        AtomicInteger amount = new AtomicInteger(0);

        // 添加下单信息至order_Detail  多条数据
        List<OrderDetail> orderDetailList = listShopping.stream().map((item)->{
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(item,detail,"id","userId","createTime");
            detail.setOrderId(number);
            amount.getAndAdd(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return detail;
        }).collect(Collectors.toList());

        // 添加订单信息   一条数据，总金额
        orders.setNumber(String.valueOf(number));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());

        // amount.get()   <--  amount.getAndAdd(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "":addressBook.getProvinceName())
                        + (addressBook.getCityName()== null ? "":addressBook.getCityName())
                        + (addressBook.getDistrictName()== null ? "":addressBook.getDistrictName())
                        + (addressBook.getDetail()== null ? "":addressBook.getDetail()));

        this.save(orders);

        orderDetailService.saveBatch(orderDetailList);

        // 清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
