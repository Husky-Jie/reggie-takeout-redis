package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.husky.common.R;
import com.husky.entity.Employee;
import com.husky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/9/27
 * Time: 15:46
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;


    // 登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);
        String msg = "用户不存在";
        if (one == null) {
            // 3、如果没查询到则返回登录失败结果
            return R.error(msg);
        }else if (!password.equals(one.getPassword())){
            // 4、密码比对，如果不一致则返回登录失败结果
            msg = "密码错误";
            return R.error(msg);
        }else if (0 == one.getStatus()){
            // 5、查看员工状态，如果为已禁用状态，返回员工已禁用结果
            msg = "该员工已被锁定";
            return R.error(msg);
        }
        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    // 退出
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    // 新增员工
    @PostMapping
    public R<String> addEmployee(@RequestBody Employee employee,HttpSession session) {

        // 设置初始密码为123456，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/
        // 获取当前用户id
        /*Long creatUser = (Long) session.getAttribute("employee");
        employee.setCreateUser(creatUser);
        employee.setUpdateUser(creatUser);*/

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    // 员工信息分页查询
    @GetMapping("/page")
     public R<Page<Employee>> pageInfo (Integer page,Integer pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        // 分页构造器
        Page<Employee> pageMP = new Page<>(page,pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        // 添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行分页查询
        employeeService.page(pageMP,lambdaQueryWrapper);
        return R.success(pageMP);
     }

     // 根据id修改员工信息
    // 编辑和启用共用一个修改方法
     @PutMapping
     public R<String> UpdateStatus(@RequestBody Employee employee) {
         /*Long id = (Long) session.getAttribute("employee");
         employee.setUpdateTime(LocalDateTime.now());
         employee.setUpdateUser(id);*/
         employeeService.updateById(employee);
        return R.success("员工状态修改成功");
     }

     // 根据员工id查询信息,数据回显
    @GetMapping("/{id}")
    public R<Employee> getById (@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("未查询到员工信息");
    }
}
