package com.alibaba.nacos.logger.adapter.logback14;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.ModelHandlerFactoryMethod;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import com.alibaba.nacos.common.logging.NacosLoggingProperties;

/**
 * Nacos ModelHandlerFactoryMethod for logback.
 *
 * @author xiweng.yy
 */
public class NacosModelHandlerFactoryMethod implements ModelHandlerFactoryMethod {
    
    private final NacosLoggingProperties loggingProperties;
    
    public NacosModelHandlerFactoryMethod(NacosLoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }
    
    @Override
    public ModelHandlerBase make(Context context, ModelInterpretationContext modelInterpretationContext) {
        return new NacosClientPropertyModelHandler(context, loggingProperties);
    }
}
