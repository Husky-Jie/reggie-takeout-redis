package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.husky.common.BaseContext;
import com.husky.common.R;
import com.husky.entity.ShoppingCart;
import com.husky.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/26
 * Time: 10:11
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;
    @GetMapping("/list")
    public R<List<ShoppingCart>> listR () {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    // 添加商品
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        // 设置user_id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 设置创建时间
        shoppingCart.setCreateTime(LocalDateTime.now());

        // 查询是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 判断菜品或套餐是否存在，已存在加1
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    // 删减商品
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null) {
            one.setNumber(one.getNumber()-1);
            Integer num = one.getNumber();
            if (num == 0) {
                shoppingCartService.removeById(one);
            }else {
                shoppingCartService.updateById(one);
            }
        }else {
            return R.error("无该对象");
        }
        return R.success(one);
    }

    // 清空购物车
    @DeleteMapping("/clean")
    public R<String> delete() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车已清空");
    }
}
