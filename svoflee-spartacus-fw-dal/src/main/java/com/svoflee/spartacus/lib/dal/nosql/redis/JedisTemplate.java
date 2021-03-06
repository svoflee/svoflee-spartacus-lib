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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import com.svoflee.spartacus.core.log.Logger;

/**
 * JedisTemplate 提供了一个template方法，负责对Jedis连接的获取与归还。
 * JedisAction<T> 和 JedisActionNoResult两种回调接口，适用于有无返回值两种情况。
 * 同时提供一些最常用函数的封装, 如get/set/zadd等。 *
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class JedisTemplate {

    private static final Logger logger = Logger.getLogger(JedisTemplate.class.getName());

    private JedisPool jedisPool;

    public JedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 执行有返回结果的action。
     */
    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        }
        catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        }
        finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 执行无返回结果的action。
     */
    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        }
        catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        }
        finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 根据连接是否已中断的标志，分别调用returnBrokenResource或returnResource。
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            try {
                if (connectionBroken) {
                    jedisPool.returnBrokenResource(jedis);
                }
                else {
                    jedisPool.returnResource(jedis);
                }
            }
            catch (Exception e) {
                logger.error("Error happen when return jedis to pool, try to close it directly.", e);
                RedisUtils.closeJedis(jedis);
            }
        }
    }

    /**
     * 获取内部的pool做进一步的动作。
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * 获取内部的pool做进一步的动作。
     */
    public Jedis getJedis() {
        return getJedisPool().getResource();
    }

    /**
     * 有返回结果的回调接口定义。
     */
    public interface JedisAction<T> {

        T action(Jedis jedis);
    }

    /**
     * 无返回结果的回调接口定义。
     */
    public interface JedisActionNoResult {

        void action(Jedis jedis);
    }

    /**
     * 删除key, 如果key存在返回true, 否则返回false。
     */
    public boolean del(final String key) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.del(key) == 1 ? true : false;
            }
        });
    }

    public void flushDB() {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.flushDB();
            }
        });
    }

    // ////////////// 关于String ///////////////////////////
    /**
     * 如果key不存在, 返回null.
     */
    public String get(final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Long getAsLong(final String key) {
        String result = get(key);
        return result != null ? Long.valueOf(result) : 0;
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Integer getAsInt(final String key) {
        String result = get(key);
        return result != null ? Integer.valueOf(result) : 0;
    }

    /**
     * Set the string value as value of the key. The string can't be longer than
     * 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     * 
     * @param key
     * @param value
     */
    public void set(final String key, final String value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.set(key, value);
            }
        });
    }

    public void setex(final String key, final String value, final int seconds) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.setex(key, seconds, value);
            }
        });
    }

    /**
     * 如果key还不存在则进行设置，返回true，否则返回false.
     */
    public boolean setnx(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.setnx(key, value) == 1 ? true : false;
            }
        });
    }

    public long incr(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    public long decr(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    // ////////////// 关于List ///////////////////////////
    public void lpush(final String key, final String value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.lpush(key, value);
            }
        });
    }

    /**
     * 返回List长度, key不存在时返回0，key类型不是list时抛出异常.
     */
    public long llen(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    // ////////////// 关于Sorted Set ///////////////////////////
    /**
     * 加入Sorted set, 如果member在Set里已存在，只更新score并返回false,否则返回true.
     */
    public boolean zadd(final String key, final String member, final double score) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.zadd(key, score, member) == 1 ? true : false;
            }
        });
    }

    /**
     * 删除sorted set中的元素，成功删除返回true，key或member不存在返回false。
     */
    public boolean zrem(final String key, final String member) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.zrem(key, member) == 1 ? true : false;
            }
        });
    }

    /**
     * 返回List长度, key不存在时返回0，key类型不是sorted set时抛出异常.
     */
    public long zcard(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    /**
     * @param key
     * @param field
     * @return
     */
    public String getStringValueByField(final String key, final String field) {
        String result = this.execute(new JedisAction<String>() {

            @Override
            public String action(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
        return result;
    }

}
