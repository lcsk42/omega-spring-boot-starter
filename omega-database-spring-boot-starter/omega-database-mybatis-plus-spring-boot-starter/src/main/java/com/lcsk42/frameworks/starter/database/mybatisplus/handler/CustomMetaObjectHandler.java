package com.lcsk42.frameworks.starter.database.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lcsk42.frameworks.starter.common.util.time.LocalDateTimeUtil;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class,
                LocalDateTimeUtil.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class,
                LocalDateTimeUtil.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class,
                LocalDateTimeUtil.now());
    }
}
