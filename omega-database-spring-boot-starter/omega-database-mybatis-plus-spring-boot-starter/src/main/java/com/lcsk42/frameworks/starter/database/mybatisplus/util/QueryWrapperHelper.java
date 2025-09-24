package com.lcsk42.frameworks.starter.database.mybatisplus.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lcsk42.frameworks.starter.database.core.annotation.Query;
import com.lcsk42.frameworks.starter.database.core.annotation.QueryIgnore;
import com.lcsk42.frameworks.starter.database.core.enums.LogicalRelation;
import com.lcsk42.frameworks.starter.database.core.enums.QueryType;
import com.lcsk42.frameworks.starter.database.core.util.SqlInjectionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * QueryWrapper 助手
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryWrapperHelper {

    /**
     * 设置排序
     *
     * @param queryWrapper 查询条件封装对象
     * @param sort 排序条件
     * @since 2.9.0
     */
    public static <T> void sort(QueryWrapper<T> queryWrapper, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            queryWrapper.orderBy(true, order.isAscending(), toUnderlineCase(property));
        }
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @param <Q> 查询条件数据类型
     * @param <R> 查询数据类型
     * @return QueryWrapper
     */
    public static <Q, R> QueryWrapper<R> build(Q query) {
        return build(query, Sort.unsorted());
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @param sort 排序条件
     * @param <Q> 查询条件数据类型
     * @param <R> 查询数据类型
     * @return QueryWrapper
     * @since 2.5.2
     */
    public static <Q, R> QueryWrapper<R> build(Q query, Sort sort) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        // 没有查询条件，直接返回
        if (query == null) {
            return queryWrapper;
        }
        // 设置排序条件
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String field = toUnderlineCase(order.getProperty());
                Validate.validState(!SqlInjectionUtils.check(field), "排序字段包含无效字符");
                queryWrapper.orderBy(true, order.isAscending(), field);
            }
        }
        // 获取查询条件中所有的字段
        List<Field> fieldList = FieldUtils.getAllFieldsList(query.getClass());
        return build(query, fieldList, queryWrapper);
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @param fields 查询条件字段列表
     * @param queryWrapper QueryWrapper
     * @param <Q> 查询条件数据类型
     * @param <R> 查询数据类型
     * @return QueryWrapper
     */
    public static <Q, R> QueryWrapper<R> build(Q query, List<Field> fields,
            QueryWrapper<R> queryWrapper) {
        // 没有查询条件，直接返回
        if (query == null) {
            return queryWrapper;
        }
        // 解析并拼接查询条件
        for (Field field : fields) {
            List<Consumer<QueryWrapper<R>>> consumers = buildWrapperConsumer(query, field);
            queryWrapper.and(CollectionUtils.isNotEmpty(consumers), q -> consumers.forEach(q::or));
        }
        return queryWrapper;
    }

    /**
     * 构建 QueryWrapper Consumer
     *
     * @param query 查询条件
     * @param field 查询条件字段
     * @param <Q> 查询条件数据类型
     * @param <R> 查询数据类型
     * @return QueryWrapper Consumer
     */
    private static <Q, R> List<Consumer<QueryWrapper<R>>> buildWrapperConsumer(Q query,
            Field field) {
        try {
            // 如果字段值为空，直接返回
            Object fieldValue = FieldUtils.readField(field, query);
            if (ObjectUtils.isEmpty(fieldValue)) {
                return List.of();
            }
            // 设置了 @QueryIgnore 注解，直接忽略
            QueryIgnore queryIgnoreAnnotation =
                    AnnotationUtils.getAnnotation(field, QueryIgnore.class);
            if (queryIgnoreAnnotation != null) {
                return List.of();
            }
            // 建议：数据库表列建议采用下划线连接法命名，程序变量建议采用驼峰法命名
            String fieldName = field.getName();
            // 没有 @Query 注解，默认等值查询
            Query queryAnnotation = AnnotationUtils.getAnnotation(field, Query.class);
            if (queryAnnotation == null) {
                return Collections.singletonList(q -> q.eq(toUnderlineCase(fieldName), fieldValue));
            }
            // 解析单列查询
            QueryType queryType = queryAnnotation.type();
            String[] columns = queryAnnotation.columns();
            final int columnLength = ArrayUtils.getLength(columns);
            List<Consumer<QueryWrapper<R>>> consumers = new ArrayList<>(columnLength);
            if (columnLength <= 1) {
                String columnName = columnLength == 1 ? columns[0] : toUnderlineCase(fieldName);
                parse(queryType, columnName, fieldValue, consumers);
                return consumers;
            }
            // 解析多列查询
            LogicalRelation logicalRelation = queryAnnotation.logicalRelation();
            List<Consumer<QueryWrapper<R>>> columnConsumers = new ArrayList<>();
            for (String column : columns) {
                parse(queryType, column, fieldValue, columnConsumers);
            }

            if (logicalRelation == LogicalRelation.AND) {
                if (!columnConsumers.isEmpty()) {
                    consumers.add(q -> {
                        columnConsumers.getFirst().accept(q);
                        columnConsumers.subList(1, columnConsumers.size()).forEach(q::and);
                    });
                }
            } else {
                consumers.addAll(columnConsumers);
            }
            return consumers;
        } catch (Exception e) {
            log.error("Build query wrapper occurred an error: {}. Query: {}, Field: {}.",
                    e.getMessage(), query, field,
                    e);
        }
        return List.of();
    }

    /**
     * 解析查询条件
     *
     * @param queryType  查询类型
     * @param columnName 列名
     * @param fieldValue 字段值
     * @param <R>        查询数据类型
     */
    @SuppressWarnings("unchecked")
    private static <R> void parse(QueryType queryType,
                                  String columnName,
                                  Object fieldValue,
                                  List<Consumer<QueryWrapper<R>>> consumers) {
        switch (queryType) {
            case EQ -> consumers.add(q -> q.eq(columnName, fieldValue));
            case NE -> consumers.add(q -> q.ne(columnName, fieldValue));
            case GT -> consumers.add(q -> q.gt(columnName, fieldValue));
            case GE -> consumers.add(q -> q.ge(columnName, fieldValue));
            case LT -> consumers.add(q -> q.lt(columnName, fieldValue));
            case LE -> consumers.add(q -> q.le(columnName, fieldValue));
            case BETWEEN -> {
                // 数组转集合
                List<Object> between = new ArrayList<>(isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (List<Object>)fieldValue);
                Validate.isTrue(between.size() == 2, "[{}] 必须是一个范围", columnName);
                consumers.add(q -> q.between(columnName, between.getFirst(), between.get(1)));
            }
            case LIKE -> consumers.add(q -> q.like(columnName, fieldValue));
            case LIKE_LEFT -> consumers.add(q -> q.likeLeft(columnName, fieldValue));
            case LIKE_RIGHT -> consumers.add(q -> q.likeRight(columnName, fieldValue));
            case IN -> {
                Validate.isTrue(!ObjectUtils.isEmpty(fieldValue), "[{}] 不能为空", columnName);
                consumers.add(q -> q.in(columnName, isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (Collection<Object>)fieldValue));
            }
            case NOT_IN -> {
                Validate.isTrue(!ObjectUtils.isEmpty(fieldValue), "[{}] 不能为空", columnName);
                consumers.add(q -> q.notIn(columnName, isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (Collection<Object>)fieldValue));
            }
            case IS_NULL -> consumers.add(q -> q.isNull(columnName));
            case IS_NOT_NULL -> consumers.add(q -> q.isNotNull(columnName));
            default -> throw new IllegalArgumentException("暂不支持 [%s] 查询类型".formatted(queryType));
        }
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static String toUnderlineCase(String param) {
        return StringUtils.camelToUnderline(param);
    }
}
