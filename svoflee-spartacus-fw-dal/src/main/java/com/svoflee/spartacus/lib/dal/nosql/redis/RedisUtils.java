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
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.svoflee.spartacus.core.utils.serialization.JsonMapper;

/**
 * RedisUtils 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class RedisUtils {

    private static final String COLON = ":";

    public static final String DEFAULT_HOST = "localhost";

    public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;

    public static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

    private static final String OK_CODE = "OK";

    private static final String OK_MULTI_CODE = "+OK";

    private static final JsonMapper jsonMapper = JsonMapper.buildNonEmptyMapper();

    /**
     * 判断 是 OK 或 +OK.
     */
    public static boolean isStatusOk(String status) {
        return (status != null) && (OK_CODE.equals(status) || OK_MULTI_CODE.equals(status));
    }

    /**
     * 快速设置JedisPoolConfig, 不执行idle checking。
     */
    public static JedisPoolConfig createPoolConfig(int maxIdle, int maxActive) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxActive(maxActive);
        poolConfig.setTimeBetweenEvictionRunsMillis(-1);
        return poolConfig;
    }

    /**
     * 快速设置JedisPoolConfig, 设置执行idle checking的间隔和idle时间.
     */
    public static JedisPoolConfig createPoolConfig(int maxIdle, int maxActive, int checkingIntervalSecs,
            int idleTimeSecs) {
        JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxActive);
        poolConfig.setTimeBetweenEvictionRunsMillis(checkingIntervalSecs * 1000);
        poolConfig.setMinEvictableIdleTimeMillis(idleTimeSecs * 1000);
        return poolConfig;
    }

    /**
     * 退出然后关闭Jedis。
     */
    public static void closeJedis(Jedis jedis) {
        if (jedis.isConnected()) {
            try {
                try {
                    jedis.quit();
                }
                catch (Exception e) {
                }
                jedis.disconnect();
            }
            catch (Exception e) {

            }
        }
    }

    /**
     * 通过class的simpleName 生产放入redis的key，通常为：
     * className:[id],e.g.ApiSerivce:08793274982
     */
    public static String getKeyByClass(Class clazz, String id) {
        return key(clazz.getSimpleName(), id);
    }

    public static String getKeyByEntity(Object entity, String id) {
        return getKeyByClass(entity.getClass(), id);
    }

    public static String key(String prefix, String keyValue) {
        StringBuilder sb = new StringBuilder();
        return sb.append(prefix).append(COLON).append(keyValue).toString();
    }

    // public static String entityAttrKey(Object entity, String idName, String attrValue) {
    // StringBuilder sb = new StringBuilder();
    // String className = entity.getClass().getSimpleName();
    // return sb.append(className).append(COLON).append(attribuiteName).append(COLON).append(attrValue).toString();
    // }

    public static String entityAttrKey(Object entity, String attribuiteName, String attrValue) {
        StringBuilder sb = new StringBuilder();
        String className = entity.getClass().getSimpleName();
        return sb.append(className).append(COLON).append(attribuiteName).append(COLON).append(attrValue).toString();
    }

    /**
     * @param jsonObj
     * @return
     */
    public static Object convertJsonToObject(Class clazz, String jsonObj) {
        return jsonMapper.fromJson(jsonObj, clazz);
    }

    public static String convertObjectToJson(Object entity) {
        String result = null;
        result = jsonMapper.toJson(entity);
        return result;
    }

}
