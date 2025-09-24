package com.lcsk42.frameworks.starter.database.mybatisplus.service;

import com.lcsk42.frameworks.starter.database.mybatisplus.model.po.BasePO;

public interface IService<T extends BasePO>
        extends com.baomidou.mybatisplus.extension.service.IService<T> {
}
