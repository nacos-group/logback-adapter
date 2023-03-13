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

package com.alibaba.nacos.logbackadapter;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import com.alibaba.nacos.common.log.NacosLogbackConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * ensure that Nacos configuration does not affect user configuration savepoints and  scanning url.
 *
 * @author hujun
 */
public class NacosLogbackConfiguratorAdapterV2 extends JoranConfigurator implements NacosLogbackConfigurator {
    
    /**
     * ensure that Nacos configuration does not affect user configuration savepoints.
     *
     * @param top safe data
     */
    @Override
    public void registerSafeConfiguration(Model top) {
    }
    
    @Override
    protected void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        defaultProcessor.addHandler(NacosClientPropertyModel.class,
                (handlerContext, handlerMic) -> new NacosClientPropertyModelHandler(this.context));
        super.addModelHandlerAssociations(defaultProcessor);
    }
    
    @Override
    public void addElementSelectorAndActionAssociations(RuleStore ruleStore) {
        super.addElementSelectorAndActionAssociations(ruleStore);
        ruleStore.addRule(new ElementSelector("configuration/nacosClientProperty"),
                NacosClientPropertyModelAction::new);
    }
    
    @Override
    public int getVersion() {
        return 2;
    }
    
    @Override
    public void setContext(Object loggerContext) {
        super.setContext((Context) loggerContext);
    }
    
    @Override
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
