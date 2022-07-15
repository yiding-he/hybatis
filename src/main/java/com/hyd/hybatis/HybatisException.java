package com.hyd.hybatis;

public class HybatisException extends RuntimeException {

    public HybatisException() {
    }

    public HybatisException(String message) {
        super(message);
    }

    public HybatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public HybatisException(Throwable cause) {
        super(cause);
    }
}
