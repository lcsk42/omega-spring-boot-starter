package com.lcsk42.frameworks.starter.idempotent.generator;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * 默认幂等名称生成器
 */
public class DefaultIdempotentNameGenerator implements IdempotentNameGenerator {

    @Override
    public String generate(Object target, Method method, Object... args) {
        StringBuilder nameSb = new StringBuilder();
        // 添加类名
        nameSb.append(ClassUtils.getName(target));
        nameSb.append(StringConstant.COLON);
        // 添加方法名
        nameSb.append(method.getName());
        // 添加参数信息的哈希值（如果有参数）
        if (args != null && args.length > 0) {
            nameSb.append(StringConstant.COLON);
            // 使用 JacksonUtil 序列化参数，然后计算哈希值以确保唯一性
            String argsJson = JacksonUtil.toJSON(args);
            if (argsJson != null) {
                nameSb.append(
                        DigestUtils.md5DigestAsHex(argsJson.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return nameSb.toString();
    }
}
