package com.example.lazyclock.others;

/**
 * 异常处理
 * Created by Administrator on 2016/1/8.
 */
public class CommonException extends Exception {

    public CommonException() {
    }

    public CommonException(String detailMessage) {
        super(detailMessage);
    }

    public CommonException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CommonException(Throwable throwable) {
        super(throwable);
    }
}

