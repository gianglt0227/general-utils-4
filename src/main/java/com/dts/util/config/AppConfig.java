/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.config;

import lombok.extern.slf4j.Slf4j;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author giang
 */
@Slf4j
public class AppConfig {

    private ConfigurationProvider configurationProvider;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        return AppConfigHolder.INSTANCE;
    }

    public final void init(File configFile) {
        try {
            Path path = Paths.get(configFile.getAbsolutePath());
            log.debug("ConfigFile: {}, Path: {}", configFile.getAbsolutePath(), path);
            ConfigFilesProvider configFilesProvider = () -> Arrays.asList(path);
            ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

            // Reload configuration every 5 seconds
            ReloadStrategy reloadStrategy = new PeriodicalReloadStrategy(5, TimeUnit.SECONDS);

            // Create provider
            configurationProvider = new ConfigurationProviderBuilder()
                    .withConfigurationSource(source)
                    .withReloadStrategy(reloadStrategy)
                    .build();
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    public ConfigurationProvider getConfiguration() {
        return configurationProvider;
    }

    private static class AppConfigHolder {

        private static final AppConfig INSTANCE = new AppConfig();
    }
}
