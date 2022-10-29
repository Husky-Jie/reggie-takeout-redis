package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/9/27
 * Time: 15:30
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
