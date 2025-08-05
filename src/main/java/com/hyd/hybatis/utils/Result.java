package com.hyd.hybatis.utils;

/**
 * Generic result class for internal use.
 */
public class Result<T> {

    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(true, t, null);
    }

    public static <T> Result<T> success(T t, String message) {
        return new Result<>(true, t, message);
    }

    public static Result<String> fail(String message) {
        return new Result<>(false, null, message);
    }

    ////////////////////////////////////////

    private final boolean success;

    private final T value;

    private final String message;

    private Result(boolean success, T value, String message) {
        this.success = success;
        this.value = value;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
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
