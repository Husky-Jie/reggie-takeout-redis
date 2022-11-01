package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.CustomException;
import com.husky.common.R;
import com.husky.dto.SetmealDto;
import com.husky.entity.Setmeal;
import com.husky.entity.SetmealDish;
import com.husky.service.CategoryService;
import com.husky.service.SetmealDishService;
import com.husky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/19
 * Time: 10:02
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SetmealDishService setmealDishService;
    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    // 套餐新增
    // @CacheEvict 多条数据从缓存中删除
    @CacheEvict(value = "setMeal", allEntries = true)
    @PostMapping
    public R<String> insertSet(@RequestBody SetmealDto setmealDto){
        setmealService.addSet(setmealDto);
        return R.success("新增套餐成功");
    }

    // 套餐分页显示
    @GetMapping("/page")
    public R<Page<SetmealDto>> pageR (Integer page, Integer pageSize, String name){
        Page<Setmeal> Page = new Page<>(page ,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(Page, dtoPage, "records");

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(Page, queryWrapper);

        List<Setmeal> records = Page.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryService.getById(setmealDto.getCategoryId()).getName());
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    // 套餐回显
    @GetMapping("/{id}")
    public R<SetmealDto> queryData(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.setMealDto(id);
        return R.success(setmealDto);
    }

    // 套餐修改保存
    // @CacheEvict 多条数据从缓存中删除
    @CacheEvict(value = "setMeal", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSet(setmealDto);
        return R.success("套餐修改成功");
    }

    // 起售停售和批量起售停售
    // @CacheEvict 多条数据从缓存中删除
    @CacheEvict(value = "setMeal", allEntries = true)
    @PostMapping("/status/{sta}")
    public R<String> Status(@PathVariable Integer sta, @RequestParam List<Long> ids){
        for (Long id :
                ids) {
            Setmeal byId = setmealService.getById(id);
            byId.setStatus(sta);
            setmealService.updateById(byId);
        }
        return R.success("套餐状态修改成功");
    }

    // 删除套餐
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids){
        for (Long id :
                ids) {
            Setmeal byId = setmealService.getById(id);
            if (0 == byId.getStatus()) {
                setmealService.removeById(id);
            }else {
                throw new CustomException("删除的商品中有未停售套餐");
            }
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
        return R.success("套餐删除成功");
    }

    // 查询套餐信息
    // 使用spring Cache的@Cacheable注解完成缓存的存放功能
    @Cacheable(value = "setMeal", key = "#p0.categoryId + 'setMeal_1'")
    @GetMapping("/list")
    public R<List<Setmeal>> listR(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId())
                    .eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
