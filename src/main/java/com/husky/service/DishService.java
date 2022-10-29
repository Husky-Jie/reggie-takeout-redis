package com.husky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.husky.dto.DishDto;
import com.husky.entity.Dish;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 16:18
 */
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto queryDishDto(Long id);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavor(List<Long> ids);
}
