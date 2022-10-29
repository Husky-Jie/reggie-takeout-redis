package com.husky.controller;

import com.husky.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/13
 * Time: 10:38
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /*
    * 参数名字要与前端的上传组件的name属性的值要一致
    * file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除*/
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取原始文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用uuid随机定义文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        // 判断路径是否存在，不存在则创建
        File file1 = new File(basePath);
        if (!file1.exists()){
            file1.mkdirs();
        }

        // 将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            // 输入流，通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(basePath+name);
            // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            // 设置响应格式
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while (-1 != (len = inputStream.read(bytes))) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            // 关闭资源
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
