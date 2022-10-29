package com.husky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.husky.entity.AddressBook;
import com.husky.mapper.AddressBookMapper;
import com.husky.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/25
 * Time: 10:34
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
