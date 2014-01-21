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

package com.svoflee.spartacus.lib.dal.nosql.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * JedisPoolFactory 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class JedisPoolFactory {

    public static JedisPool createJedisPool(String defaultHost, int defaultPort, int defaultTimeout, int threadCount) {
        // 合并命令行传入的系统变量与默认值
        String host = System.getProperty("benchmark.host", defaultHost);
        String port = System.getProperty("benchmark.port", String.valueOf(defaultPort));
        String timeout = System.getProperty("benchmark.timeout", String.valueOf(defaultTimeout));

        // 设置Pool大小，设为与线程数等大，并屏蔽掉idle checking
        JedisPoolConfig poolConfig = RedisUtils.createPoolConfig(threadCount, threadCount);

        // create jedis pool
        return new JedisPool(poolConfig, host, Integer.valueOf(port), Integer.valueOf(timeout));
    }
}
