package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/25
 * Time: 16:40
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
