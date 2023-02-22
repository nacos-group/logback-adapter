/*
 *
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.alibaba.nacos.logbackadpater;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import com.alibaba.nacos.common.log.NacosLogbackProperties;
import com.alibaba.nacos.common.spi.NacosServiceLoader;

import java.util.Collection;

/**
 * Logback model to support <nacosClientProperty/> tags.
 * for example:
 * <nacosClientProperty scope="context" name="logPath" source="system.log.path" defaultValue="/root" />
 *
 * @author hujun
 */
public class NacosClientPropertyModelHandler extends ModelHandlerBase {
    
    public NacosClientPropertyModelHandler(Context context) {
        super(context);
    }
    
    @Override
    public void handle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        NacosClientPropertyModel propertyModel = (NacosClientPropertyModel) model;
        ActionUtil.Scope scope = ActionUtil.stringToScope(propertyModel.getScope());
        String defaultValue = propertyModel.getDefaultValue();
        String source = propertyModel.getSource();
        if (OptionHelper.isNullOrEmpty(propertyModel.getName()) || OptionHelper.isNullOrEmpty(source)) {
            addError("The \"name\" and \"source\" attributes of <nacosClientProperty> must be set");
        }
        ModelUtil.setProperty(intercon, propertyModel.getName(), getValue(source, defaultValue), scope);
    }
    
    private String getValue(String source, String defaultValue) {
        Collection<NacosLogbackProperties> logbackClientProperties = NacosServiceLoader.load(
                NacosLogbackProperties.class);
        return logbackClientProperties.stream().findFirst().get().getValue(source, defaultValue);
    }
}
