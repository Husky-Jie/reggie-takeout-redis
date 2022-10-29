package com.husky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.entity.Employee;
import com.husky.mapper.EmployeeMapper;
import com.husky.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/9/27
 * Time: 15:32
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
