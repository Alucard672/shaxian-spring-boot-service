package com.shaxian.tech.web;

import com.shaxian.biz.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统内各种异常，包装为合适的响应格式返回
 *
 * @author tech
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验异常（@Valid注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数验证失败: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        log.warn("表单绑定失败: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException e) {
        log.warn("非法状态: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        String message = "请提供必需参数: " + e.getParameterName();
        log.warn("缺少请求参数: {}", e.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String typeName = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知";
        String message = String.format("参数 %s 格式不正确，应为 %s 类型", e.getName(), typeName);
        log.warn("参数类型不匹配: {} 应为 {} 类型", e.getName(), typeName);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方式，请检查HTTP方法是否正确";
        log.warn("不支持的请求方法: {}", e.getMethod());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理HTTP媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        String message = "请求格式不正确，请使用正确的Content-Type";
        log.warn("不支持的媒体类型: {}", e.getContentType());
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理HTTP消息不可读异常（JSON格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        String message = "请求数据格式错误，请检查JSON格式是否正确";
        log.warn("请求体解析失败: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
        String message = "请求的接口不存在，请检查请求路径是否正确";
        log.warn("请求的资源不存在: {}", e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理数据完整性约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String causeMessage = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        String message = "数据操作失败";
        if (causeMessage != null) {
            // 提取用户友好的错误信息
            if (causeMessage.contains("Duplicate entry")) {
                message = "该数据已存在，请勿重复添加";
            } else if (causeMessage.contains("foreign key constraint")) {
                message = "该数据正在被使用，无法删除";
            } else if (causeMessage.contains("cannot be null")) {
                message = "必填信息不能为空，请完善后再提交";
            } else {
                message = "数据保存失败，请检查数据是否合法";
            }
        }
        log.warn("数据完整性约束错误: {}", causeMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理空结果数据访问异常
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmptyResult(EmptyResultDataAccessException e) {
        String message = "未找到相关数据";
        log.warn("查询的数据不存在");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(message));
    }

    /**
     * 处理其他数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccess(DataAccessException e) {
        log.error("数据访问异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("数据操作失败，请稍后重试"));
    }

    /**
     * 生成错误代码（用于日志追踪）
     */
    private String generateErrorCode() {
        return "ERR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointer(NullPointerException e) {
        String errorCode = generateErrorCode();
        log.error("空指针异常 [错误代码: {}]", errorCode, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("系统运行异常，错误代码：" + errorCode + "，请联系技术支持"));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        String errorCode = generateErrorCode();
        log.error("系统异常 [错误代码: {}]", errorCode, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("系统运行异常，错误代码：" + errorCode + "，请稍后重试或联系技术支持"));
    }
}
