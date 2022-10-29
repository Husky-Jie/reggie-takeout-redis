package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.R;
import com.husky.entity.Category;
import com.husky.entity.Dish;
import com.husky.entity.Setmeal;
import com.husky.service.CategoryService;
import com.husky.service.DishService;
import com.husky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 11:15
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;
    // 新增菜品和套餐
    @PostMapping
    public R<String> addList(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增菜品和套餐成功");
    }

    // 菜单分页查询
    @GetMapping("/page")
    public R<Page<Category>> pageInfo(Integer page, Integer pageSize) {
        log.info("page = {},pageSize = {}",page,pageSize);
        // 分页构造器
        Page<Category> pageIn = new Page<>(page,pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageIn,lambdaQueryWrapper);
        return R.success(pageIn);
    }

    // 修改菜品和套餐
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改菜品和套餐成功");
    }

    // 删除菜品和套餐
    @DeleteMapping
    public R<String> deleteById(Long ids){
        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        // 按category_id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        // 统计条数
        int countDish = (int) dishService.count(dishLambdaQueryWrapper);
        int countSetMeal = (int) setmealService.count(setMealLambdaQueryWrapper);
        // 判断是否有关联
        if (countDish > 0 || countSetMeal > 0) {
            return R.error("该分类中有菜品关联");
        }else {
            categoryService.removeById(ids);
            return R.success("删除菜品和套餐成功");
        }
    }

    @GetMapping("/list")
    public R<List<Category>> listR(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
