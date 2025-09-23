package com.lcsk42.frameworks.starter.database.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.lcsk42.frameworks.starter.database.mybatisplus.enums.MyBatisPlusIdGeneratorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(MybatisPlusProperties.PREFIX)
public class MybatisPlusProperties {

    public static final String PREFIX = "framework.database.mybatis-plus";

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
     * ID 生成器
     */
    @NestedConfigurationProperty
    private IdGenerator idGenerator;

    /**
     * 分页插件配置
     */
    private Pagination pagination;

    /**
     * 启用乐观锁插件
     */
    private boolean optimisticLockerEnabled = false;

    /**
     * 启用防全表更新与删除插件
     */
    private boolean blockAttackPluginEnabled = true;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class IdGenerator {
        /**
         * ID 生成器类型
         */
        private MyBatisPlusIdGeneratorType type = MyBatisPlusIdGeneratorType.DEFAULT;
    }

    /**
     * 分页插件配置属性
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Pagination {
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
