package com.shaxian.tech.hibernate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记以 JSON 形式存储的字符串字段（如 TEXT 存 JSON 数组/对象）。
 * 在全局默认值拦截器中，当该字段为 null 且列不允许为 null 时，
 * 将使用 {@link #emptyValue()} 作为默认值（如 "[]" 或 "{}"）。
 * 若允许为 null，则不处理。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonStorage {

    /**
     * 当字段为 null 且列 nullable=false 时使用的默认值。
     * 数组形态的 JSON 使用 "[]"，对象形态使用 "{}"。
     */
    String emptyValue() default "{}";
}
