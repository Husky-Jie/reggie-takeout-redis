package com.husky.common;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/10/18
 * Time: 13:53
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
