package com.husky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.husky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/25
 * Time: 10:29
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
