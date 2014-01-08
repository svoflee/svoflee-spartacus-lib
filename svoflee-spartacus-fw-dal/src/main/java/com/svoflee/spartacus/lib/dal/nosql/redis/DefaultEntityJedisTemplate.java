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

import redis.clients.jedis.JedisPool;

import com.svoflee.spartacus.core.domain.DataPersistable;
import com.svoflee.spartacus.core.log.Logger;

/**
 * DefaultEntityJedisTemplate 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class DefaultEntityJedisTemplate<E extends DataPersistable<PK>, PK extends Serializable> extends
        EntityJedisTemplate<E, PK> {

    private static final Logger log = Logger.getLogger(DefaultEntityJedisTemplate.class.getName());

    /**
     * @param jedisPool
     */
    public DefaultEntityJedisTemplate(JedisPool jedisPool) {
        super(jedisPool);
    }

}
