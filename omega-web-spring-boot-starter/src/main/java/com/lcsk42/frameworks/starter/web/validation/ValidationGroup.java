package com.lcsk42.frameworks.starter.web.validation;

import jakarta.validation.groups.Default;

/**
 * 分组校验
 */
public interface ValidationGroup extends Default {
    /**
     * CRUD 分组校验-创建
     */
    interface Create extends ValidationGroup {
    }

    /**
     * CRUD 分组校验-修改
     */
    interface Update extends ValidationGroup {
    }
}
