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

import com.svoflee.spartacus.core.constants.GlobalConstants.Version;
import com.svoflee.spartacus.core.domain.DataPersistable;
import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.Pageable;
import com.svoflee.spartacus.core.domain.query.Sort;
import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.core.utils.exception.EUtils;
import com.svoflee.spartacus.lib.dal.IBaseRepository;

/**
 * BaseEntityRedisRepository 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public abstract class BaseEntityRedisRepository<E extends DataPersistable<PK>, PK extends Serializable> implements
        IBaseRepository<E, PK> {

    private static final Logger log = Logger.getLogger(BaseEntityRedisRepository.class.getName());

    public abstract EntityJedisTemplate<E, PK> getJedisTemplate();

    /**
     * {@inheritDoc}
     */
    @Override
    public E saveEntity(E entity) {
        return getJedisTemplate().saveEntity(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> saveEntities(Iterable<? extends E> entities) {
        return getJedisTemplate().saveEntities(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E selectByPrimaryKey(PK id) {
        return getJedisTemplate().selectByPrimaryKey(getRepositoryClass(), id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(PK id) {
        return getJedisTemplate().existsEntityKey(getRepositoryClass(), id);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll() {
        return getJedisTemplate().findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll(Sort sort) {
        return getJedisTemplate().findAll(sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return getJedisTemplate().count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEntity(PK id) {
        getJedisTemplate().deleteEntity(getRepositoryClass(), id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEntity(E entity) {
        this.deleteEntity(entity.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEntities(Iterable<? extends E> entities) {
        getJedisTemplate().deleteEntities(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        getJedisTemplate().deleteAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable findAll(CriteriaContainer aCriteriaContainer) {
        return getJedisTemplate().findAll(aCriteriaContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E findUniqueByEq(String propertyName, Object propertyValue) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findByEq(String propertyName, Object propertyValue) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        EUtils.throwNotSupportedException(Version.V1_0_0);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E saveAndFlush(E entity) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteInBatch(Iterable<E> entities) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

    }

}
