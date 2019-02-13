/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class CdrExporter {

    private final static Logger logger = LoggerFactory.getLogger(CdrExporter.class);

    public static void writeLine(char separator, Object... fields) {
        String line = Joiner.on(separator).useForNull("").join(fields);
        logger.info(line);
    }

    public static void writeLine(String line) {
        logger.info(line);
    }
}
