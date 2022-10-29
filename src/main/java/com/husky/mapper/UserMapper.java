package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/24
 * Time: 16:45
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
