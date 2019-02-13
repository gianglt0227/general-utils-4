/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.config;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author giang
 */
public class AppConfig {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ReloadingFileBasedConfigurationBuilder<XMLConfiguration> builder;
    private XMLConfiguration defaultConfiguration;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        return AppConfigHolder.INSTANCE;
    }

    public final void init(File configFile) {
        try {
            Parameters params = new Parameters();
            builder = new ReloadingFileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(params.fileBased().setFile(configFile));
            PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 1, TimeUnit.MINUTES);
            trigger.start();

            builder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<ConfigurationBuilderEvent>() {

                public void onEvent(ConfigurationBuilderEvent event) {
                    logger.trace("Configuration reset: {}", event);
                }
            });

            defaultConfiguration = builder.getConfiguration();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public XMLConfiguration getConfiguration() {
        try {
            return builder.getConfiguration();
        } catch (Exception ex) {
            logger.error("", ex);
            return defaultConfiguration;
        }
    }

    private static class AppConfigHolder {

        private static final AppConfig INSTANCE = new AppConfig();
    }
}
