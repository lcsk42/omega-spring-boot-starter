package com.lcsk42.frameworks.starter.database.mybatisflex.config;

import com.mybatisflex.core.dialect.DbType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(MybatisFlexProperties.PREFIX)
public class MybatisFlexProperties {
    public static final String PREFIX = "framework.database.mybatis-flex";

    public static final String MAPPER_PACKAGE = PREFIX + ".mapper-package";

    public static final String ID_GENERATOR_TYPE = PREFIX + ".id-generator.type";

    /**
     * Mapper 接口扫描包（配置时必须使用：mapper-package 键名）
     * <p>
     * e.g. com.example.**.mapper
     * </p>
     */
    private String mapperPackage;

    /**
     * 分页插件配置
     */
    private PaginationProperties pagination;

    /**
     * 分页插件配置属性
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class PaginationProperties {
        /**
         * 数据库类型
         */
        private DbType dbType;

        /**
         * 是否溢出处理
         */
        private boolean overflow = false;

        /**
         * 单页分页条数限制（默认：-1 表示无限制）
         */
        private Long maxLimit = -1L;
    }
}
