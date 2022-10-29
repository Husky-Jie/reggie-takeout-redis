package com.husky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.entity.User;
import com.husky.mapper.UserMapper;
import com.husky.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/24
 * Time: 16:46
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
