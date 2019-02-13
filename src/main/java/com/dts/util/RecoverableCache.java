/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.io.File;

/**
 *
 * @author giang
 */
public interface RecoverableCache {

    public void dump(File fileToDump);

    public void recover(File dumpFile);
}
