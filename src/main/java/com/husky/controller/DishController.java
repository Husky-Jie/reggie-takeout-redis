package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.CustomException;
import com.husky.common.R;
import com.husky.dto.DishDto;
import com.husky.entity.Category;
import com.husky.entity.Dish;
import com.husky.entity.DishFlavor;
import com.husky.service.CategoryService;
import com.husky.service.DishFlavorService;
import com.husky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
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

    // 新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
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
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        // 查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapperDto = new LambdaQueryWrapper<>();
            queryWrapperDto.eq(DishFlavor::getDishId,dishDto.getId());
            List<DishFlavor> listDto = dishFlavorService.list(queryWrapperDto);
            dishDto.setFlavors(listDto);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }
}
