package com.husky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.common.CustomException;
import com.husky.common.R;
import com.husky.dto.DishDto;
import com.husky.entity.Dish;
import com.husky.entity.DishFlavor;
import com.husky.mapper.DishMapper;
import com.husky.service.DishFlavorService;
import com.husky.service.DishService;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 16:18
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    @Resource
    private DishFlavorService dishFlavorService;

    // 新增菜品
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.saveOrUpdate(dishDto);

        // 菜品id
        Long dishId = dishDto.getId();

        // 设置菜品口味表的dishId
        List<DishFlavor> list = dishDto.getFlavors();
        list = list.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(list);
    }

    // 回显菜品信息
    @Override
    public DishDto queryDishDto(Long id) {
        Dish byId = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId,dishDto);
        LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(dishDtoLambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    // 修改保存信息
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新基本dish信息
        this.updateById(dishDto);

        // 删除原有的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 新增修改口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 设置菜品口味表的dishId
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    // 删除菜品和口味信息
    @Transactional
    @Override
    public void deleteWithFlavor(List<Long> ids) {
        List<Dish> dishes = this.listByIds(ids);
        for (Dish dish :
                dishes) {
            if (dish.getStatus() == 0) {
                this.removeById(dish.getId());
            }else {
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
    }
}
