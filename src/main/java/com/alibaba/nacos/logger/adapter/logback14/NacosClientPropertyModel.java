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

package com.alibaba.nacos.logger.adapter.logback14;

import ch.qos.logback.core.model.NamedModel;

/**
 * Logback model to support <nacosClientProperty/> tags.
 * <p>
 * Move from https://github.com/nacos-grorp/logback-adapter
 * </p>
 *
 * @author hujun
 * @author xiweng.yy
 * @since 2.4.0
 */
public class NacosClientPropertyModel extends NamedModel {
    
    private String scope;
    
    private String defaultValue;
    
    private String source;
    
    String getScope() {
        return this.scope;
    }
    
    void setScope(String scope) {
        this.scope = scope;
    }
    
    String getDefaultValue() {
        return this.defaultValue;
    }
    
    void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    String getSource() {
        return this.source;
    }
    
    void setSource(String source) {
        this.source = source;
    }
}
