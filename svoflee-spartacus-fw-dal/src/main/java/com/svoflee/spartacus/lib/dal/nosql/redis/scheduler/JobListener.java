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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.svoflee.spartacus.core.utils.Threads;
import com.svoflee.spartacus.lib.dal.nosql.redis.JedisTemplate;
import com.svoflee.spartacus.lib.dal.nosql.redis.JedisTemplate.JedisActionNoResult;

/**
 * JobListener 是阻塞接收任务的Runnable.
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class JobListener implements Runnable {

    public static final int DEFAULT_POPUP_TIMEOUT = 5;

    private static Logger logger = LoggerFactory.getLogger(JobListener.class);

    private JedisTemplate jedisTemplate;

    private final JobHandler jobHandler;

    private String readyJobKey;

    public JobListener(String jobName, JedisPool jedisPool, JobHandler jobHandler) {
        jedisTemplate = new JedisTemplate(jedisPool);
        readyJobKey = Keys.getReadyJobKey(jobName);
        this.jobHandler = jobHandler;
    }

    /**
     * 循环调用
     */
    @Override
    public void run() {
        // 第一层大循环保证了如果redis服务连接异常，等待两秒后继续执行。
        while (true) {
            try {
                jedisTemplate.execute(new JedisActionNoResult() {

                    @Override
                    public void action(Jedis jedis) {
                        // 第二层循环发生在jedis action内，用同一个Jedis不断popup任务直到线程中断。
                        while (!Thread.currentThread().isInterrupted()) {
                            List<String> nameValuePair = jedis.brpop(DEFAULT_POPUP_TIMEOUT, readyJobKey);
                            if ((nameValuePair != null) && !nameValuePair.isEmpty()) {
                                String job = nameValuePair.get(1);
                                try {
                                    jobHandler.handleJob(jedis, job);
                                }
                                catch (Exception e) {
                                    // 记录流出的异常，然后毫不停顿的继续运行，做个坚强的Listener。
                                    logger.error("Handler exception for job " + job, e);
                                }
                            }
                        }

                    }
                });
                // 线程已中断，退出循环.
                break;
            }
            catch (JedisConnectionException e) {
                Threads.sleep(2000);
            }
        }
    }

    /**
     * 回调Handler接口定义.
     */
    public interface JobHandler {

        void handleJob(Jedis jedis, String job);
    }
}
