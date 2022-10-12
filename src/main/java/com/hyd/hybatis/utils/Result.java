package com.hyd.hybatis.utils;

public class Result<T> {

    public static <T> Result<T> success(T t) {
        return new Result<>(true, t);
    }

    public static <T> Result<T> success() {
        return new Result<>(true, null);
    }

    public static Result<String> fail(String message) {
        return new Result<>(false, message);
    }

    private final boolean success;

    private final T value;

    private Result(boolean success, T value) {
        this.success = success;
        this.value = value;
    }

    public boolean ok() {
        return success;
    }

    public T get() {
        return value;
    }

    public T getOrThrow() {
        if (this.success) {
            return this.value;
        } else {
            throw new RuntimeException(String.valueOf(this.value));
        }
    }
}
