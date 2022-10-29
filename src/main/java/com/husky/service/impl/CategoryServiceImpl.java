package com.husky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.entity.Category;
import com.husky.mapper.CategoryMapper;
import com.husky.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 11:13
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
