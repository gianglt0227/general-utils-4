/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 * @param <K>
 * @param <V>
 */
public abstract class IdLoadingCache<K, V> {

    /**
     *
     */
    protected final LoadingCache<K, V> cache;

    /**
     *
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     */
    protected final CacheStats statistic;

    /**
     *
     */
    protected final String cacheName;

    /**
     *
     * @param cacheName
     */
    protected IdLoadingCache(String cacheName) {
        this.cache = buildCache();
        this.statistic = getCacheStats();
        this.cacheName = cacheName;
    }

    private synchronized LoadingCache<K, V> buildCache() {
        try {
            String cacheBuilderSpec = getCacheBuilderSpec();
            CacheBuilder cacheBuilder;
            if (Strings.emptyToNull(cacheBuilderSpec) == null) {
                cacheBuilder = CacheBuilder.newBuilder()
                        .expireAfterAccess(1, TimeUnit.MINUTES)
                        .expireAfterWrite(0, TimeUnit.DAYS);
            } else {
                cacheBuilder = CacheBuilder.from(cacheBuilderSpec);
            }
            CacheLoader<K, V> cacheLoader = getCacheLoader();
            RemovalListener<K, V> removalListener = getRemovalListener();
            LoadingCache<K, V> result = cacheBuilder
                    .removalListener(removalListener)
                    .recordStats()
                    .build(cacheLoader);
            scheduleCacheCleanUp();
            scheduleCacheStats();
            return result;
        } catch (Exception ex) {
            logger.error("", ex);
            return null;
        }

    }

    public V get(K key) throws ExecutionException {
        return cache.get(key);
    }

    private CacheStats getCacheStats() {
        return cache.stats();
    }

    /**
     * The string syntax is a series of comma-separated keys or key-value pairs, each corresponding to a CacheBuilder method.
     * 
     * concurrencyLevel=[integer]: sets CacheBuilder.concurrencyLevel.
     * initialCapacity=[integer]: sets CacheBuilder.initialCapacity.
     * maximumSize=[long]: sets CacheBuilder.maximumSize.
     * maximumWeight=[long]: sets CacheBuilder.maximumWeight.
     * expireAfterAccess=[duration]: sets CacheBuilder.expireAfterAccess(long, java.util.concurrent.TimeUnit).
     * expireAfterWrite=[duration]: sets CacheBuilder.expireAfterWrite(long, java.util.concurrent.TimeUnit).
     * refreshAfterWrite=[duration]: sets CacheBuilder.refreshAfterWrite(long, java.util.concurrent.TimeUnit).
     * weakKeys: sets CacheBuilder.weakKeys().
     * softValues: sets CacheBuilder.softValues().
     * weakValues: sets CacheBuilder.weakValues().
     * recordStats: sets CacheBuilder.recordStats().
     * 
     * The set of supported keys will grow as CacheBuilder evolves, but existing keys will never be removed.
     * Durations are represented by an integer, followed by one of "d", "h", "m", or "s", representing days, hours, minutes, or seconds respectively. (There is currently no syntax to request expiration in milliseconds, microseconds, or nanoseconds.)
     * Whitespace before and after commas and equal signs is ignored. Keys may not be repeated; it is also illegal to use the following pairs of keys in a single value:
     * maximumSize and maximumWeight
     * softValues and weakValues
     * @return
     */
    public abstract CacheLoader<K, V> getCacheLoader();

    /**
     * @return
     */
    public abstract String getCacheBuilderSpec();

    /**
     * Do nothing by default
     *
     * @return
     */
    protected RemovalListener<K, V> getRemovalListener() {
        return new RemovalListener<K, V>() {

            @Override
            public void onRemoval(RemovalNotification<K, V> notification) {

            }
        };
    }

    /**
     * By default, the clean up thread will run every seconds
     */
    protected void scheduleCacheCleanUp() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    cache.cleanUp();
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        };
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(cacheName + "-%d").build();
        Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * By default, the thread will log the Cache statistic every minute.
     */
    protected void scheduleCacheStats() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    logger.debug("Cache statistic {}: ", cacheName, statistic.toString());
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        };
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(cacheName + "-%d").build();
        Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(runnable, 1, 60, TimeUnit.SECONDS);
    }
;

}
