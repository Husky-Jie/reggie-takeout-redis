package com.husky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.entity.DishFlavor;
import com.husky.mapper.DishFlavorMapper;
import com.husky.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/16
 * Time: 15:25
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
