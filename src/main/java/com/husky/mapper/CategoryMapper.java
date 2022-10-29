package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/12
 * Time: 11:11
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
