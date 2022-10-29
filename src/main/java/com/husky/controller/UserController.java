package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.husky.common.R;
import com.husky.entity.User;
import com.husky.service.UserService;
import com.husky.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/24
 * Time: 16:47
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号码
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成验证码
            String  code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 控制台打印验证码
            log.info("code={}",code);
            // 将验证码保存到session
            session.setAttribute(phone,code);

            return R.success("验证码生成成功");
        }

        return R.error("验证码生成失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map, HttpSession session){
        // 获取手机号
        String phone = map.get("phone");
        // 获取验证码
        String code = map.get("code");

        // 从session获取验证码进行比对
        String codeSession = (String) session.getAttribute(phone);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User one = userService.getOne(queryWrapper);
        if (codeSession != null && !codeSession.equals(code)) {
            return R.error("验证码错误");
        }else if (one != null) {
            if (0 == one.getStatus()) {
                return R.error("该账户已被禁用");
            }else {
                session.setAttribute("user",one.getId());
                return R.success(one);
            }
        }else {
            // 新用户自动注册
            one = new User();
            one.setPhone(phone);
            one.setStatus(1);
            userService.save(one);
        }
        session.setAttribute("user",one.getId());
        return R.success(one);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
