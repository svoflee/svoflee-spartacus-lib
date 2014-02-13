/*
 * Copyright (c) http://www.svoflee.com All rights reserved.
 **************************************************************************
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
 **************************************************************************      
 */

package com.svoflee.spartacus.lib.dal.nosql.redis.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import redis.clients.jedis.JedisPool;

import com.google.common.collect.Lists;
import com.svoflee.spartacus.core.utils.Threads;
import com.svoflee.spartacus.core.utils.Threads.WrapExceptionRunnable;
import com.svoflee.spartacus.lib.dal.nosql.redis.JedisScriptExecutor;
import com.svoflee.spartacus.lib.dal.nosql.redis.JedisTemplate;

/**
 * 定时分发任务。 启动线程定时从sleeping job sorted set 中取出到期的任务放入ready job list.
 * 线程池可自行创建，也可以从外部传入共用。 *
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class JobDispatcher implements Runnable {

    public static final String DEFAULT_DISPATCH_LUA_FILE = "classpath:/redis/dispatch.lua";

    public static final long DEFAULT_INTERVAL_MILLIS = 1000;

    private static Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

    private static AtomicInteger poolNumber = new AtomicInteger(1);

    private ScheduledExecutorService internalScheduledThreadPool;

    private ScheduledFuture dispatchJob;

    private long intervalMillis = DEFAULT_INTERVAL_MILLIS;

    private JedisTemplate jedisTemplate;

    private JedisScriptExecutor scriptExecutor;

    private String scriptPath = DEFAULT_DISPATCH_LUA_FILE;

    private String scriptHash;

    private List<String> keys;

    private String sleepingJobKey;

    private String readyJobKey;

    private String dispatchCounterKey;

    public JobDispatcher(String jobName, JedisPool jedisPool) {
        sleepingJobKey = Keys.getSleepingJobKey(jobName);
        readyJobKey = Keys.getReadyJobKey(jobName);
        dispatchCounterKey = Keys.getDispatchCounterKey(jobName);
        keys = Lists.newArrayList(sleepingJobKey, readyJobKey, dispatchCounterKey);

        jedisTemplate = new JedisTemplate(jedisPool);
        scriptExecutor = new JedisScriptExecutor(jedisPool);
    }

    /**
     * 启动分发线程, 自行创建scheduler线程池.
     */
    public void start() {
        internalScheduledThreadPool = Executors.newScheduledThreadPool(1,
                Threads.buildJobFactory("Job-Dispatcher-" + poolNumber.getAndIncrement() + "-%d"));

        start(internalScheduledThreadPool);
    }

    /**
     * 启动分发线程, 使用传入的scheduler线程池.
     */
    public void start(ScheduledExecutorService scheduledThreadPool) {
        loadLuaScript();

        dispatchJob = scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, intervalMillis,
                TimeUnit.MILLISECONDS);
    }

    private void loadLuaScript() {
        String script;
        try {
            Resource resource = new DefaultResourceLoader().getResource(scriptPath);
            script = FileUtils.readFileToString(resource.getFile());
        }
        catch (IOException e) {
            throw new IllegalArgumentException(scriptPath + " is not exist.", e);
        }

        scriptHash = scriptExecutor.load(script);
    }

    /**
     * 停止分发任务，如果是自行创建的threadPool则自行销毁。
     */
    public void stop() {
        dispatchJob.cancel(false);

        if (internalScheduledThreadPool != null) {
            Threads.normalShutdown(internalScheduledThreadPool, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 以当前时间为参数执行Lua Script分发任务。
     */
    @Override
    public void run() {
        long currTime = System.currentTimeMillis();
        List<String> args = Lists.newArrayList(String.valueOf(currTime));
        Long count = (Long) scriptExecutor.execute(scriptHash, keys, args);
        logger.debug("{} Job dispatched", count != null ? count : 0);
    }

    /**
     * 获取未达到触发条件进行分发的Job数量.
     */
    public long getSleepingJobNumber() {
        return jedisTemplate.zcard(sleepingJobKey);
    }

    /**
     * 获取已分发但未被执行的Job数量.
     */
    public long getReadyJobNumber() {
        return jedisTemplate.llen(readyJobKey);
    }

    /**
     * 获取已分发的Job数量。
     */
    public long getDispatchNumber() {
        return jedisTemplate.getAsLong(dispatchCounterKey);
    }

    /**
     * 重置已分发的Job数量计数器.
     */
    public void restDispatchNumber() {
        jedisTemplate.set(dispatchCounterKey, "0");
    }

    /**
     * 设置非默认的script path, 格式为spring的Resource路径风格。
     */
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    /**
     * 设置非默认1秒的分发间隔.
     */
    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}
