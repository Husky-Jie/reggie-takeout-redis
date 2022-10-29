package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/26
 * Time: 23:10
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
