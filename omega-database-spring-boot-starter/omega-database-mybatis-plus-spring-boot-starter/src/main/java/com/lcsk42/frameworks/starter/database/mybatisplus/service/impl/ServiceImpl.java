package com.lcsk42.frameworks.starter.database.mybatisplus.service.impl;

import com.lcsk42.frameworks.starter.database.mybatisplus.mapper.BaseMapper;
import com.lcsk42.frameworks.starter.database.mybatisplus.model.po.BasePO;
import com.lcsk42.frameworks.starter.database.mybatisplus.service.IService;

public class ServiceImpl<M extends BaseMapper<T>, T extends BasePO>
        extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T>
        implements IService<T> {
}