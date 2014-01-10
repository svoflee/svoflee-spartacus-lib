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

import java.io.Serializable;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.collect.Lists;
import com.svoflee.spartacus.core.constants.GlobalConstants.Version;
import com.svoflee.spartacus.core.domain.DataPersistable;
import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.Pageable;
import com.svoflee.spartacus.core.domain.query.Sort;
import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.core.utils.IdUtils;
import com.svoflee.spartacus.core.utils.exception.EUtils;

/**
 * EntityJedisTemplate 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public abstract class EntityJedisTemplate<E extends DataPersistable<PK>, PK extends Serializable> extends JedisTemplate {

    private static final Logger log = Logger.getLogger(EntityJedisTemplate.class.getName());

    public EntityJedisTemplate(JedisPool jedisPool) {
        super(jedisPool);
    }

    public E saveEntity(E entity) {
        String id = null;

        if (entity.isNew()) {
            id = IdUtils.getGlobleUniqueId();// 通过UUID方式进行存储
            entity.setId(typeStringToPK(id));
        }
        else {
            id = typePKToString(entity.getId());
        }

        final String value = RedisUtils.convertObjectToJson(entity);
        final String key = RedisUtils.getKeyByEntity(entity, id);
        String r = this.execute(new JedisAction<String>() {

            @Override
            public String action(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
        if (log.isDebugEnabled()) {
            log.debug("saveEntity:{}", r);
        }

        return entity;
    }

    public List<E> saveEntities(Iterable<? extends E> entities) {
        List<E> result = Lists.newArrayList();
        for (E name : entities) {
            result.add(this.saveEntity(name));
        }
        return result;
    }

    public E selectByPrimaryKey(Class<E> clazz, PK id) {
        final String key = RedisUtils.getKeyByClass(clazz, typePKToString(id));
        String jsonObj = this.execute(new JedisAction<String>() {

            @Override
            public String action(Jedis jedis) {
                String jsonObj = jedis.get(key);
                return jsonObj;

            }
        });
        E result = (E) RedisUtils.convertJsonToObject(clazz, jsonObj);
        return result;
    }

    public boolean existsEntityKey(Class<E> clazz, final PK id) {
        boolean result = false;

        final String key = RedisUtils.getKeyByClass(clazz, this.typePKToString(id));

        result = this.execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.exists(key);

            }
        });
        return result;
    }

    public List<E> findAll() {
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    public List<E> findAll(Sort sort) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    public long count() {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return 0;
    }

    public void deleteEntity(Class<E> clazz, PK id) {
        final String key = RedisUtils.getKeyByClass(clazz, typePKToString(id));
        Long result = this.execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.del(key);
            }
        });
        log.debug("deleteEntity:{}", result);
    }

    public void deleteEntities(Iterable<? extends E> entities) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
    }

    public void deleteAll() {
        EUtils.throwNotSupportedException(Version.V1_0_0);
    }

    public E findUniqueByEq(Class<E> clazz, String propertyName, Object propertyValue) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;

        // List<E> rs = JOhm.find(clazz, propertyName, propertyValue);
        // if (rs.size() > 1) {
        // throw new RepositoryException("属性不唯一:[" + propertyName + ":" + propertyValue + "]");
        // }
        // else if (rs.size() == 1) {
        // return rs.get(0);
        // }
        // else {
        // return null;
        // }
    }

    public Pageable findAll(CriteriaContainer aCriteriaContainer) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    public List<E> findByEq(String propertyName, Object propertyValue) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    public void flush() {
        EUtils.throwNotSupportedException(Version.V1_0_0);
    }

    public E saveAndFlush(E entity) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    public void deleteInBatch(Iterable<E> entities) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
    }

    private String typePKToString(PK id) {
        return (String) id;
    }

    private PK typeStringToPK(String id) {
        return (PK) id;
    }
}
