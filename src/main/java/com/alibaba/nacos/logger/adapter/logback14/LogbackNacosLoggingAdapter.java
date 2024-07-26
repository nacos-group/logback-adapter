/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.nacos.common.logging.NacosLoggingAdapter;
import com.alibaba.nacos.common.logging.NacosLoggingProperties;
import com.alibaba.nacos.common.utils.ResourceUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Iterator;

/**
 * Support for Logback version 1.3.0 to current latest 1.4.X.
 *
 * @author <a href="mailto:huangxiaoyu1018@gmail.com">hxy1991</a>
 * @author <a href="mailto:hujun3@xiaomi.com">hujun</a>
 * @author xiweng.yy
 * @since 2.4.0
 */
public class LogbackNacosLoggingAdapter implements NacosLoggingAdapter {
    
    private static final String NACOS_LOGBACK_LOCATION = "classpath:nacos-logback14.xml";
    
    private static final String LOGBACK_CLASSES = "ch.qos.logback.classic.Logger";
    
    private NacosLogbackConfiguratorAdapterV2 configurator;
    
    public LogbackNacosLoggingAdapter() {
    }
    
    @Override
    public boolean isAdaptedLogger(Class<?> loggerClass) {
        Class<?> expectedLoggerClass = getExpectedLoggerClass();
        if (null == expectedLoggerClass || !expectedLoggerClass.isAssignableFrom(loggerClass)) {
            return false;
        }
        return isUpperLogback13();
    }
    
    private Class<?> getExpectedLoggerClass() {
        try {
            return Class.forName(LOGBACK_CLASSES);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * logback use 'ch.qos.logback.core.model.Model' since 1.3.0, set logback version during initialization.
     */
    private boolean isUpperLogback13() {
        try {
            Class.forName("ch.qos.logback.core.model.Model");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    @Override
    public boolean isNeedReloadConfiguration() {
        return false;
    }
    
    @Override
    public String getDefaultConfigLocation() {
        return NACOS_LOGBACK_LOCATION;
    }
    
    @Override
    public void loadConfiguration(NacosLoggingProperties loggingProperties) {
        checkConfiguratorExist();
        String location = loggingProperties.getLocation();
        configurator.setLoggingProperties(loggingProperties);
        LoggerContext loggerContext = loadConfigurationOnStart(location);
        addNacosLogFilterIfNecessary(loggerContext);
        if (hasNoListener(loggerContext)) {
            addListener(loggerContext, location);
        }
    }
    
    private void addNacosLogFilterIfNecessary(LoggerContext loggerContext) {
        //first is NacosLogTurboFilter
        if (!loggerContext.getTurboFilterList().isEmpty() && loggerContext.getTurboFilterList()
                .get(0) instanceof NacosLogTurboFilter) {
            return;
        }
        // not empty and first not NacosLogTurboFilter, remove NacosLogTurboFilter
        if (!loggerContext.getTurboFilterList().isEmpty() && !(loggerContext.getTurboFilterList()
                .get(0) instanceof NacosLogTurboFilter)) {
            Iterator<TurboFilter> iterator = loggerContext.getTurboFilterList().iterator();
            while (iterator.hasNext()) {
                if (iterator.next() instanceof NacosLogTurboFilter) {
                    iterator.remove();
                }
            }
        }
        //add NacosLogTurboFilter to head.
        loggerContext.getTurboFilterList().add(0, new NacosLogTurboFilter());
    }
    
    class NacosLogTurboFilter extends TurboFilter {
        
        @Override
        public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects,
                Throwable throwable) {
            if (logger.getName().startsWith("com.alibaba.nacos")
                    && logger.getEffectiveLevel().levelInt <= level.levelInt) { // only filter nacos log
                return FilterReply.ACCEPT;
            }
            return FilterReply.NEUTRAL;
        }
    }
    
    private synchronized void checkConfiguratorExist() {
        if (null == configurator) {
            configurator = new NacosLogbackConfiguratorAdapterV2();
        }
    }
    
    private boolean hasNoListener(LoggerContext loggerContext) {
        for (LoggerContextListener loggerContextListener : loggerContext.getCopyOfListenerList()) {
            if (loggerContextListener instanceof NacosLoggerContextListener) {
                return false;
            }
        }
        return true;
    }
    
    private LoggerContext loadConfigurationOnStart(final String location) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configurator.setContext(loggerContext);
        if (StringUtils.isNotBlank(location)) {
            try {
                configurator.configure(ResourceUtils.getResourceUrl(location));
            } catch (Exception e) {
                throw new IllegalStateException("Could not initialize Logback Nacos logging from " + location, e);
            }
        }
        return loggerContext;
    }
    
    class NacosLoggerContextListener implements LoggerContextListener {
        
        private final String location;
        
        NacosLoggerContextListener(String location) {
            this.location = location;
        }
        
        @Override
        public boolean isResetResistant() {
            return true;
        }
        
        @Override
        public void onReset(LoggerContext context) {
            loadConfigurationOnStart(location);
        }
        
        @Override
        public void onStart(LoggerContext context) {
        }
        
        @Override
        public void onStop(LoggerContext context) {
        }
        
        @Override
        public void onLevelChange(Logger logger, Level level) {
        }
    }
    
    private void addListener(LoggerContext loggerContext, String location) {
        loggerContext.addListener(new NacosLoggerContextListener(location));
    }
    
}
