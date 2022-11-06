package com.husky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.husky.common.BaseContext;
import com.husky.common.R;
import com.husky.entity.AddressBook;
import com.husky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/25
 * Time: 10:35
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
@Api(tags = "地址簿相关接口")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;

    // 显示地址
    @ApiOperation("显示所有地址列表")
    @GetMapping("/list")
    public R<List<AddressBook>> listR() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    // 添加地址
    @ApiOperation("添加地址")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",value = "地址簿信息",dataType = "AddressBook")
    })
    public R<String> addAddress(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        log.info("添加地址");
        return R.success("添加地址成功");
    }

    // 设置默认地址
    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",value = "地址簿信息",dataType = "AddressBook")
    })
    public R<AddressBook> defaultAddress(@RequestBody AddressBook addressBook) {
        // 将该用户的所有地址都设为不是默认状态
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);

        // 设定用户的指定一个地址为默认状态
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    // 编辑收获地址回显
    @ApiOperation("编辑收获地址回显")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "地址id",dataType = "Long")
    })
    public R<AddressBook> getAddress(@PathVariable Long id){
        AddressBook byId = addressBookService.getById(id);
        if (byId != null) {
            return R.success(byId);
        }else {
            return R.error("没有找到该对象");
        }
    }

    // 修改保存地址信息
    @ApiOperation("修改保存地址信息")
    @PutMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",value = "地址簿信息",dataType = "AddressBook")
    })
    public R<String> put(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    // 删除地址信息
    @ApiOperation("删除地址信息")
    @DeleteMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "地址ids",dataType = "Long")
    })
    public R<String> delete(@RequestParam Long ids) {
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    // 获取默认地址
    @ApiOperation("获取默认地址")
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        return R.success(one);
    }
}
