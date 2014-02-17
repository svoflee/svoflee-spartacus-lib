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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;

import com.svoflee.spartacus.lib.dal.nosql.redis.JedisTemplate;

/**
 * JobManager 是任务管理，支持任务的安排与取消
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class JobManager {

    private static Logger logger = LoggerFactory.getLogger(JobManager.class);

    private JedisTemplate jedisTemplate;

    private String sleepingJobKey;

    public JobManager(String jobName, JedisPool jedisPool) {
        jedisTemplate = new JedisTemplate(jedisPool);
        sleepingJobKey = Keys.getSleepingJobKey(jobName);
    }

    /**
     * 安排任务.
     */
    public void scheduleJob(final String job, final long delay, final TimeUnit timeUnit) {
        final long delayTimeMillis = System.currentTimeMillis() + timeUnit.toMillis(delay);
        jedisTemplate.zadd(sleepingJobKey, job, delayTimeMillis);
    }

    /**
     * 取消任务, 如果任务不存在或已被触发返回false, 否则返回true.
     */
    public boolean cancelJob(final String job) {
        boolean removed = jedisTemplate.zrem(sleepingJobKey, job);

        if (!removed) {
            logger.warn("Can't cancel job by value {}", job);
        }

        return removed;
    }
}
