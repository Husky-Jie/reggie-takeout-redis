package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.R;
import com.husky.dto.DishDto;
import com.husky.entity.Category;
import com.husky.entity.Dish;
import com.husky.entity.DishFlavor;
import com.husky.service.CategoryService;
import com.husky.service.DishFlavorService;
import com.husky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/16
 * Time: 15:27
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    // 新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        // 清理菜品所有缓存数据,保持数据的一致性
        // Set<Object> keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理某指定菜品的缓存
        String keys = "dish_" +  dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

    // 菜品分页
    @GetMapping("/page")
    public R<Page<DishDto>> pageDish(Integer page, Integer pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getCreateTime);

        dishService.page(dishPage,dishLambdaQueryWrapper);
        // 拷贝属性，第一个参数为被拷贝对象，第二个参数为目标对象，第三个参数为排除被拷贝的属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    // 修改菜品回显数据
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id) {
        DishDto dishDto = dishService.queryDishDto(id);
        if (dishDto != null){
            return R.success(dishDto);
        }
        return R.error("菜品不存在");
    }

    // 修改保存菜品
    @PutMapping
    public R<String> put(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        // 清理菜品所有缓存数据,保持数据的一致性
        // Set<Object> keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理某指定菜品的缓存
        String keys = "dish_" +  dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("修改成功");
    }

    // 售卖状态更改   起售停售---批量起售停售
    // http://localhost:8080/dish/status/1?ids=1581993019861692417
    @PostMapping("/status/{sta}")
    public R<String> status(@PathVariable Integer sta,@RequestParam List<Long> ids) {

        for (Long id :
                ids) {
            Dish byId = dishService.getById(id);
            byId.setStatus(sta);
            dishService.updateById(byId);

            // 删除商品的菜品分类缓存
            String key = "dish_" + byId.getCategoryId() + "_1";
            redisTemplate.delete(key);
        }
        return R.success("售卖状态更改成功");
    }

    // 添加方法实现删除和批量删除
    @DeleteMapping
    public R<String> deleteDishDto(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    // 直接实现删除和批量删除
    /*@DeleteMapping
    @Transactional
    public R<String> deleteDishDto(@RequestParam List<Long> ids){
        for (Long id :
                ids) {
            if (dishService.getById(id).getStatus() == 0) {
                dishService.removeById(id);
            }else {
                throw new CustomException("菜品中有为停售商品，无法删除");
            }
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("删除成功");
    }*/

    // 菜品查询
    @GetMapping("/list")
    public R<List<DishDto>> listR(Dish dish) {
        // 从redis的缓存中查询菜品数据
        List<DishDto> dtoList = null;

        // key改造
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 若redis的缓存中已存在菜品数据，则返回
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dtoList != null) {
            return R.success(dtoList);
        }

        // 若redis的缓存不存在数据，则查询数据库，并保存一份至缓存中
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        // 查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);

        dtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapperDto = new LambdaQueryWrapper<>();
            queryWrapperDto.eq(DishFlavor::getDishId,dishDto.getId());
            List<DishFlavor> listDto = dishFlavorService.list(queryWrapperDto);
            dishDto.setFlavors(listDto);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dtoList,60,TimeUnit.MINUTES);

        return R.success(dtoList);
    }
}
