/*
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
 */

package com.alibaba.nacos.logger.adapter.logback14;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import com.alibaba.nacos.common.logging.NacosLoggingProperties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * ensure that Nacos configuration does not affect user configuration savepoints and  scanning url.
 * <p>
 * Move from https://github.com/nacos-grorp/logback-adapter
 * </p>
 *
 * @author hujun
 * @author xiweng.yy
 * @since 2.4.0
 */
public class NacosLogbackConfiguratorAdapterV2 extends JoranConfigurator {
    
    private NacosLoggingProperties loggingProperties;
    
    public void setLoggingProperties(NacosLoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }
    
    @Override
    public void registerSafeConfiguration(Model top) {
    }
    
    @Override
    protected void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        defaultProcessor.addHandler(NacosClientPropertyModel.class, new NacosModelHandlerFactoryMethod(loggingProperties));
        super.addModelHandlerAssociations(defaultProcessor);
    }
    
    @Override
    public void addElementSelectorAndActionAssociations(RuleStore ruleStore) {
        ElementSelector elementSelector = new ElementSelector("configuration/nacosClientProperty");
        if (null == ruleStore.matchActions(elementSelector.duplicate())) {
            super.addElementSelectorAndActionAssociations(ruleStore);
            ruleStore.addRule(elementSelector, NacosClientPropertyModelAction::new);
        }
    }
    
    /**
     * ensure that Nacos configuration does not affect user configuration scanning url.
     *
     * @param url config url
     * @throws Exception e
     */
    public void configure(URL url) throws Exception {
        InputStream in = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            in = urlConnection.getInputStream();
            doConfigure(in, url.toExternalForm());
        } catch (IOException ioe) {
            String errMsg = "Could not open URL [" + url + "].";
            addError(errMsg, ioe);
            throw new JoranException(errMsg, ioe);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    String errMsg = "Could not close input stream";
                    addError(errMsg, ioe);
                    throw new JoranException(errMsg, ioe);
                }
            }
        }
    }
}
