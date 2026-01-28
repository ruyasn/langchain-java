package dev.agentplatform.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        return Result.<T>builder().code(0).message("success").data(data).build();
    }

    public static <T> Result<T> fail(int code, String message) {
        return Result.<T>builder().code(code).message(message).data(null).build();
    }
}
