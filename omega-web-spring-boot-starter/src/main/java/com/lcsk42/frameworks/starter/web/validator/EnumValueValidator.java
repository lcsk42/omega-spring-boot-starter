package com.lcsk42.frameworks.starter.web.validator;

import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import com.lcsk42.frameworks.starter.web.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 枚举校验器
 */
@SuppressWarnings("rawtypes")
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private static final Logger log = LoggerFactory.getLogger(EnumValueValidator.class);
    private Class<? extends Enum> enumClass;
    private String[] enumValues;
    private String enumMethod;

    @Override
    public void initialize(EnumValue enumValue) {
        this.enumClass = enumValue.value();
        this.enumValues = enumValue.enumValues();
        this.enumMethod = enumValue.method();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // 处理数组场景
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            for (Object element : array) {
                if (!isValidElement(element)) {
                    return false;
                }
            }
            return true;
        }

        // 处理集合场景
        if (value instanceof Iterable<?> iterable) {
            for (Object element : iterable) {
                if (!isValidElement(element)) {
                    return false;
                }
            }
            return true;
        }

        // 处理单个值场景
        return isValidElement(value);
    }

    /**
     * 校验单个元素是否有效
     *
     * @param value 待校验的值
     * @return 是否有效
     */
    private boolean isValidElement(Object value) {
        // 优先校验 enumValues
        if (enumValues.length > 0) {
            return List.of(enumValues).contains(String.valueOf(value));
        }

        Enum[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants.length == 0) {
            return false;
        }

        if (StringUtils.isBlank(enumMethod)) {
            return findEnumValue(enumConstants, String.valueOf(value));
        }

        try {
            // 枚举类指定了方法名，则调用指定方法获取枚举值
            Method method = enumClass.getMethod(enumMethod);
            for (Enum enumConstant : enumConstants) {
                if (String.valueOf(method.invoke(enumConstant)).equals(String.valueOf(value))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while validating the enum value, please check the @EnumValue parameter configuration.", e);
        }
        return false;
    }

    /**
     * 遍历枚举类，判断是否包含指定值
     *
     * @param enumConstants 枚举类数组
     * @param value         待校验的值
     * @return 是否包含指定值
     */
    private boolean findEnumValue(Enum[] enumConstants, Object value) {
        for (Enum enumConstant : enumConstants) {
            if (enumConstant instanceof BaseEnum<?> baseEnum) {
                if (baseEnum.getValue().toString().equals(value)) {
                    return true;
                }
            } else if (enumConstant.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}