package org.seckill.exception;

/*
* 重复秒杀异常
* */
public class RepeatkillException extends RuntimeException{

    public RepeatkillException(String message) {
        super(message);
    }

    public RepeatkillException(String message, Throwable cause) {
        super(message, cause);
    }
}
