package com.husky.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/9/30
 * Time: 15:29
 */

/*@ControllerAdvice(annotations = {RestController.class, Controller.class})
*
* */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalException {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException sqlEx) {
        String[] split = sqlEx.getMessage().split(" ");
        if (sqlEx.getMessage().contains("Duplicate entry")){
            String msg = split[2]+"用户已存在";
            return R.error(msg);
        }
        return R.error("系统繁忙，请稍后重试。。。。");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> deleteFoodException(CustomException customException){
        return R.error(customException.getMessage());
    }
}
