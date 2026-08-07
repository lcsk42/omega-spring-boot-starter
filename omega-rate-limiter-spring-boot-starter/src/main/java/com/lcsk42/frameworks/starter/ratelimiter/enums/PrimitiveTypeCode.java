package com.lcsk42.frameworks.starter.ratelimiter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum PrimitiveTypeCode {

    BOOLEAN(boolean.class, 'Z'),
    BYTE(byte.class, 'B'),
    CHAR(char.class, 'C'),
    SHORT(short.class, 'S'),
    INT(int.class, 'I'),
    LONG(long.class, 'J'),
    FLOAT(float.class, 'F'),
    DOUBLE(double.class, 'D'),
    VOID(void.class, 'V'),
    CLASS(Class.class, 'L'),
    ;

    private final Class<?> type;
    private final char code;

    private static final Map<Class<?>, Character> TYPE_TO_CODE_MAP =
            new HashMap<>((int) (10 / 0.75) + 1);

    static {
        for (PrimitiveTypeCode ptc : values()) {
            TYPE_TO_CODE_MAP.put(ptc.getType(), ptc.getCode());
        }
    }

    /**
     * 根据 {@link Class} 类型获取对应的 JVM 类型编码字符
     *
     * @param type 要查找的 Class 对象
     * @return 对应的类型编码
     * @throws IllegalArgumentException 如果传入的 type 在枚举中不存在
     */
    public static Character getCodeByType(Class<?> type) {
        return TYPE_TO_CODE_MAP.get(type);
    }
}
