package com.mango.funflow.exception;

import com.mango.funflow.common.Code;
import com.mango.funflow.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各类异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义的业务逻辑异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [URI: {}]: {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @RequestBody + @Valid 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Controller 方法参数校验失败 [URI: {}]: {}", request.getRequestURI(), message);
        return Result.error(Code.VALIDATION_ERROR, message);
    }

    /**
     * 处理普通参数校验异常处理（JSR-303）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("Service 方法参数校验失败 [URI: {}]: {}", request.getRequestURI(), message);
        return Result.error(Code.VALIDATION_ERROR, message);
    }
    
    /**
     * 处理所有未捕获的异常
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 [URI: {}]: ", request.getRequestURI(), e);
        
        // 生产环境隐藏详细错误信息
        return Result.error(Code.SYSTEM_ERROR, "系统繁忙，请稍后重试！");
    }
}
