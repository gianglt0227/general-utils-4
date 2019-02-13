/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import com.dts.util.config.AppConfig;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class GeneratorSeq {

    private AtomicInteger seq = new AtomicInteger(0);
    private final Logger logger = LoggerFactory.getLogger(GeneratorSeq.class);
    private final Lock lock = new ReentrantLock(true);
    private File sequenceFile;

    private GeneratorSeq() {
        init();
    }

    private void init() {
        lock.lock();
        try {
            Configuration config = AppConfig.getInstance().getConfiguration();
            String sequenceFilePath = config.getString("general.sequenceFilePath", System.getProperty("user.home") + "/config/sequence.txt");
            sequenceFile = new File(sequenceFilePath);
            if (!sequenceFile.exists()) {
                sequenceFile.createNewFile();
                FileUtils.writeStringToFile(sequenceFile, String.valueOf(seq.getAndIncrement()), false);
            } else {
                seq = new AtomicInteger(Integer.parseInt(FileUtils.readFileToString(sequenceFile).trim()));
            }
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            lock.unlock();
        }
    }

    public int getNextSequence() throws Exception {
//        lock.lock();
        try {
            if (seq.get() >= Integer.MAX_VALUE) {
                seq.set(1);
            }
//            FileUtils.writeStringToFile(sequenceFile, String.valueOf(seq.incrementAndGet()), false);
            return seq.incrementAndGet();
        } catch (Exception ex) {
            logger.error("", ex);
            throw ex;
        } finally {
//            lock.unlock();
        }
    }

    public synchronized static GeneratorSeq getInstance() {
        return GeneratorSeqHolder.INSTANCE;
    }

    private static class GeneratorSeqHolder {

        private static final GeneratorSeq INSTANCE = new GeneratorSeq();
    }

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int nextSequence = GeneratorSeq.getInstance().getNextSequence();
                    System.out.println("Seq: " + nextSequence);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(GeneratorSeq.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 1, 100, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 2, 200, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 3, 90, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 4, 130, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 5, 100, TimeUnit.MILLISECONDS);

    }
}
