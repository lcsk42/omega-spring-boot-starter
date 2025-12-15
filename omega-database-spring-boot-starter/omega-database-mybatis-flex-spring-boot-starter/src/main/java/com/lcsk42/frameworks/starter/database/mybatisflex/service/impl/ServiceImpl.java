package com.lcsk42.frameworks.starter.database.mybatisflex.service.impl;

import com.lcsk42.frameworks.starter.database.mybatisflex.mapper.BaseMapper;
import com.lcsk42.frameworks.starter.database.mybatisflex.service.IService;

public class ServiceImpl<M extends BaseMapper<T>, T>
        extends com.mybatisflex.spring.service.impl.ServiceImpl<M, T>
        implements IService<T> {

}
