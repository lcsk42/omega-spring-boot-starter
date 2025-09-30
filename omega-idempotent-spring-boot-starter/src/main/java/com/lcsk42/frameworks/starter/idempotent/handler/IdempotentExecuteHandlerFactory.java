package com.lcsk42.frameworks.starter.idempotent.handler;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentSceneEnum;
import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentTypeEnum;
import com.lcsk42.frameworks.starter.idempotent.impl.param.IdempotentParamService;
import com.lcsk42.frameworks.starter.idempotent.impl.spel.IdempotentSpELByMQExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.token.IdempotentTokenService;

/**
 * 幂等执行处理器工厂
 */
public final class IdempotentExecuteHandlerFactory {

    /**
     * 获取幂等执行处理器
     *
     * @param scene 指定幂等验证场景类型
     * @param type  指定幂等处理类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI -> {
                switch (type) {
                    case PARAM -> result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                    case TOKEN -> result = ApplicationContextHolder.getBean(IdempotentTokenService.class);
                    case SPEL -> result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                    default -> {
                    }
                }
            }
            case MQ -> result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
            default -> {
            }
        }
        return result;
    }
}
