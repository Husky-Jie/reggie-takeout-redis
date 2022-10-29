package com.husky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.husky.dto.SetmealDto;
import com.husky.entity.Setmeal;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 16:20
 */
public interface SetmealService extends IService<Setmeal> {

    void addSet(SetmealDto setmealDto);

    SetmealDto setMealDto(Long id);

    void updateSet(SetmealDto setmealDto);
}
