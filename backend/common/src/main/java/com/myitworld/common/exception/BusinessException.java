package com.myitworld.common.exception;

import com.myitworld.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 在 Service 层抛出，由全局异常处理器 {@link GlobalExceptionHandler} 捕获并转换为统一 Result 响应。
 * 区别于系统异常，业务异常代表可预期的错误（如用户不存在、参数校验失败）。
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码，对应 ResultCode 或自定义 code */
    private final int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
