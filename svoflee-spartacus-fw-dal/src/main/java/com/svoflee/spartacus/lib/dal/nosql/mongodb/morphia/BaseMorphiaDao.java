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

package com.svoflee.spartacus.lib.dal.nosql.mongodb.morphia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.svoflee.spartacus.core.constants.GlobalConstants.Version;
import com.svoflee.spartacus.core.domain.DataPersistable;
import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.Pageable;
import com.svoflee.spartacus.core.domain.query.Sort;
import com.svoflee.spartacus.core.utils.exception.EUtils;
import com.svoflee.spartacus.lib.dal.IPagingAndSortingRepository;

/**
 * BaseMorphiaDao 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 * @param <E>
 * @param <PK>
 */
public abstract class BaseMorphiaDao<E extends DataPersistable<PK>, PK extends Serializable> extends BasicDAO<E, PK>
        implements IPagingAndSortingRepository<E, PK> {

    private static final long serialVersionUID = 382865530820262252L;

    public BaseMorphiaDao(Datastore ds) {
        super(ds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E selectByPrimaryKey(PK id) {
        return get(id);
    }

    @Override
    public boolean exists(PK id) {
        EUtils.throwNotSupportedException(Version.V1_0_0);
        // return null;
        return false;
    }

    // public Map<PK, E> getMap(Collection<PK> ids) {
    // EUtils.throwNotSupportedException(Version.V1_0_0);
    // return null;
    // }

    // public List<E> getAll() {
    // QueryResults<E> r = super.find();
    // return r.asList();
    // }

    public long getTotalEntityCount() {
        return super.count();
    }

    // @Override
    // public void saveOrUpdate(E entity) throws DataAccessException {
    // throw new RuntimeException("not supported exception");
    // }

    @Override
    public E saveEntity(E entity) {
        if (entity.getId() == null) {
            entity.setId((PK) new ObjectId().toString());// XXX:Cast to PK
        }

        super.save(entity);
        return entity;
    }

    // public void saveEntities(Collection<E> entities) {
    // // TODO:@@优化处理
    // for (E entity : entities) {
    // this.saveEntity(entity);
    // }
    // }

    @Override
    public List<E> saveEntities(Iterable<? extends E> entities) {
        // TODO:单条处理的方式 @@优化处理
        List<E> result = new ArrayList<E>();

        if (entities == null) {
            return result;
        }

        for (E entity : entities) {
            result.add(this.saveEntity(entity));
        }

        return result;
        // return null;
    }

    // public void updateEntities(Collection<E> entities) throws DataAccessException {
    // // TODO:2优化处理
    // for (E entity : entities) {
    // this.updateEntity(entity);
    // }
    // }

    // public void updateEntity(E entity) throws DataAccessException {
    // if (entity instanceof BaseMongoEntity) {
    // Date updateDate = new Date();
    // ((BaseMongoEntity) entity).setUpdateTime(updateDate);
    // }
    // super.save(entity);
    // }

    public void deleteEntityByPK(PK id) throws DataAccessException {
        super.deleteById(id);
    }

    @Override
    public void deleteEntity(E entity) {
        super.delete(entity);
    }

    public Collection<E> findByPage(Pageable aPage) {
        Query<E> q = createQuery();
        q.limit(aPage.getPageSize()).offset((aPage.getPageNo() - 1) * aPage.getPageSize());
        aPage.setTotalCount(q.countAll());
        return q.asList();
    }

    @Override
    public List<E> findAll() {
        QueryResults<E> r = super.find();
        return r.asList();
    }

    @Override
    public void deleteEntity(PK id) {
        // mogodb的api与jpa的api不太吻合，需要修改

        super.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEntities(Iterable<? extends E> entities) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        EUtils.throwNotSupportedException(Version.V1_0_0);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll(Sort sort) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable findAll(CriteriaContainer aCriteriaContainer) {
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public org.springframework.data.domain.Page<E> findAll(CriteriaContainer pageable) {
    // EUtils.throwNotSupportedException(Version.V1_0_0);
    // return null;
    // }

}
