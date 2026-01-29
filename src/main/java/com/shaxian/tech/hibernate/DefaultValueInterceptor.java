package com.shaxian.tech.hibernate;

import jakarta.persistence.Column;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.Type;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 全局 Hibernate 拦截器：在创建数据库记录（insert）时，对非 null 约束字段的 null 值统一设置默认值。
 * <ul>
 *   <li>数字类型 → 0（Integer/Long/Short/Byte 为 0，BigDecimal 为 ZERO，Double/Float 为 0.0）</li>
 *   <li>字符串类型 → 空串 ""</li>
 *   <li>标记了 {@link JsonStorage} 的 JSON 字段：若允许为 null 不处理；不允许为 null 时设为注解上的 emptyValue（如 "[]" 或 "{}"）；未标记的不确定类型不设置</li>
 * </ul>
 */
public class DefaultValueInterceptor implements Interceptor {

    private static final Set<Class<?>> NUMERIC_TYPES = new HashSet<>(Arrays.asList(
            Integer.class, Long.class, Short.class, Byte.class,
            Double.class, Float.class, BigDecimal.class
    ));

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types)
            throws CallbackException {
        if (state == null || propertyNames == null || types == null) {
            return false;
        }
        Class<?> entityClass = entity.getClass();
        for (int i = 0; i < state.length; i++) {
            if (state[i] != null) {
                continue;
            }
            String propertyName = propertyNames[i];
            Type type = types[i];
            Field field = findField(entityClass, propertyName);
            if (field == null) {
                continue;
            }
            if (field.getAnnotation(TenantId.class) != null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            boolean nullable = column == null || column.nullable();
            if (nullable) {
                continue;
            }
            Class<?> returnedClass = type.getReturnedClass();
            if (NUMERIC_TYPES.contains(returnedClass)) {
                state[i] = defaultForNumeric(returnedClass);
            } else if (String.class.equals(returnedClass)) {
                JsonStorage jsonStorage = field.getAnnotation(JsonStorage.class);
                if (jsonStorage != null) {
                    state[i] = jsonStorage.emptyValue();
                } else {
                    state[i] = "";
                }
            }
            // 其他类型（Boolean、日期、枚举等）不确定则不设置
        }
        return false;
    }

    private static Object defaultForNumeric(Class<?> clazz) {
        if (Integer.class.equals(clazz)) {
            return 0;
        }
        if (Long.class.equals(clazz)) {
            return 0L;
        }
        if (Short.class.equals(clazz)) {
            return (short) 0;
        }
        if (Byte.class.equals(clazz)) {
            return (byte) 0;
        }
        if (Double.class.equals(clazz)) {
            return 0.0;
        }
        if (Float.class.equals(clazz)) {
            return 0.0f;
        }
        if (BigDecimal.class.equals(clazz)) {
            return BigDecimal.ZERO;
        }
        return 0;
    }

    private static Field findField(Class<?> clazz, String propertyName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field f = current.getDeclaredField(propertyName);
                return f;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
