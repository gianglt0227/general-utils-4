/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.app;

/**
 *
 * @author GiangLT
 */
public interface IApplication {

    public void start(String[] args);

    public void stop(String[] args);

    public void reload(String[] args);

    public void restart(String[] args);

    public void initConfig(String logFolder, String configFolder, String log4jFileName, String configFileName);

    public void setupDatabaseAccess();
}
