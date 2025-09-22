package com.lcsk42.frameworks.starter.json.jackson.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.json.core.exception.JSONException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Jackson 工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonUtil {
  private static final ObjectMapper OBJECT_MAPPER =
      ApplicationContextHolder.getBean(ObjectMapper.class);

  /**
   * 获取 Jackson 对象映射器
   *
   * @return {@link ObjectMapper} Jackson 对象映射器
   */
  public static ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }


  /**
   * 将对象序列化为 JSON 字符串
   *
   * @param object 要序列化的对象
   * @return JSON 字符串，如果输入为 null 则返回 null
   * @throws JSONException 当序列化失败时抛出
   */
  public static <T> String toJSON(T object) {
    if (Objects.isNull(object)) {
      return null;
    }
    try {
      if (object instanceof String string) {
        return string;
      } else {
        return OBJECT_MAPPER.writeValueAsString(object);
      }
    } catch (JsonProcessingException e) {
      throw new JSONException(e.toString());
    }
  }

  /**
   * 将 JSON 字符串反序列化为指定类的对象
   *
   * @param json 要反序列化的 JSON 字符串
   * @param clazz 目标类
   * @return 反序列化后的对象，如果输入为空则返回 null
   * @throws JSONException 当反序列化失败时抛出
   */
  public static <T> T fromJson(String json, Class<T> clazz) {
    if (StringUtils.isBlank(json) || Objects.isNull(clazz)) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new JSONException(e.toString());
    }
  }

  /**
   * 使用 TypeReference 将 JSON 字符串反序列化为复杂类型对象
   *
   * @param json 要反序列化的 JSON 字符串
   * @param typeReference 目标类型的类型引用
   * @return 反序列化后的对象，如果输入为空则返回 null
   * @throws JSONException 当反序列化失败时抛出
   */
  public static <T> T fromJson(String json, TypeReference<T> typeReference) {
    if (StringUtils.isBlank(json) || Objects.isNull(typeReference)) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(json, typeReference);
    } catch (JsonProcessingException e) {
      throw new JSONException(e.toString());
    }
  }

  /**
   * 检查字符串是否为有效的 JSON
   *
   * @param json 要验证的字符串
   * @return 如果是有效的 JSON 则返回 true，否则返回 false
   */
  public static boolean isJson(String json) {
    if (StringUtils.isBlank(json)) {
      return false;
    }
    try {
      OBJECT_MAPPER.readTree(json);
      return true;
    } catch (JsonProcessingException ignore) {
      // 忽略异常
      return false;
    }
  }

  /**
   * 将对象序列化为格式化的 JSON 字符串
   *
   * @param object 要序列化的对象
   * @return 格式化后的 JSON 字符串，如果输入为 null 则返回 null
   * @throws JSONException 当序列化失败时抛出
   */
  public static <T> String toPrettyJson(T object) {
    if (Objects.isNull(object)) {
      return null;
    }
    try {
      if (object instanceof String) {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
            .writeValueAsString(OBJECT_MAPPER.readTree(object.toString()));
      } else {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      }
    } catch (JsonProcessingException e) {
      throw new JSONException(e.toString());
    }
  }

  /**
   * 在 JSON 中查找给定字段名的所有文本值
   *
   * @param json 要搜索的 JSON 字符串
   * @param fieldName 要查找的字段名
   * @return 文本值列表，如果未找到则返回空列表
   * @throws JSONException 当 JSON 处理失败时抛出
   */
  public static List<String> findText(String json, String fieldName) {
    if (StringUtils.isBlank(json) || StringUtils.isBlank(fieldName)) {
      return Collections.emptyList();
    }
    try {
      return OBJECT_MAPPER.readTree(json).findValuesAsText(fieldName);
    } catch (JsonProcessingException e) {
      throw new JSONException(e.toString());
    }
  }

  /**
   * 在 JSON 中查找给定字段名的第一个文本值
   *
   * @param json 要搜索的 JSON 字符串
   * @param fieldName 要查找的字段名
   * @return 第一个文本值，如果未找到则返回 null
   */
  public static String find(String json, String fieldName) {
    return findText(json, fieldName).stream().findFirst().orElse(null);
  }

  /**
   * 将 JSON 字符串解析为 JsonNode 树结构
   *
   * @param json 要解析的 JSON 字符串
   * @return JsonNode 树，如果输入为空则返回 null
   * @throws JSONException 当 JSON 处理失败时抛出
   */
  public static JsonNode toTree(String json) {
    if (StringUtils.isNoneBlank(json)) {
      try {
        return OBJECT_MAPPER.readTree(json);
      } catch (JsonProcessingException e) {
        throw new JSONException(e.toString());
      }
    }
    return null;
  }

  /**
   * 使用 Jackson 转换功能将对象转换为另一种类型
   *
   * @param object 要转换的对象
   * @param clazz 目标类
   * @return 转换后的对象，如果输入为 null 则返回 null
   */
  public static <T> T convert(Object object, Class<T> clazz) {
    if (Objects.isNull(object) || Objects.isNull(clazz)) {
      return null;
    }
    return OBJECT_MAPPER.convertValue(object, clazz);
  }

  /**
   * 使用 TypeReference 将对象转换为另一种类型
   *
   * @param object 要转换的对象
   * @param typeReference 目标类型的类型引用
   * @return 转换后的对象，如果输入为 null 则返回 null
   */
  public static <T> T convert(Object object, TypeReference<T> typeReference) {
    if (Objects.isNull(object) || Objects.isNull(typeReference)) {
      return null;
    }
    return OBJECT_MAPPER.convertValue(object, typeReference);
  }
}
