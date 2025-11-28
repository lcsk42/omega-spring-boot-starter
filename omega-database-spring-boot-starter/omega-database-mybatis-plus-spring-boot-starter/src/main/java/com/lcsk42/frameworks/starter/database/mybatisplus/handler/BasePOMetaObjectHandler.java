package com.lcsk42.frameworks.starter.database.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lcsk42.frameworks.starter.database.mybatisplus.model.po.BasePO;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class BasePOMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(
                metaObject,
                BasePO.Fields.deleted,
                Boolean.class,
                false);
        this.strictInsertFill(
                metaObject,
                BasePO.Fields.createTime,
                LocalDateTime.class,
                LocalDateTime.now());
        this.strictInsertFill(
                metaObject,
                BasePO.Fields.updateTime,
                LocalDateTime.class,
                LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(
                metaObject,
                BasePO.Fields.updateTime,
                LocalDateTime.class,
                LocalDateTime.now());
    }
}
