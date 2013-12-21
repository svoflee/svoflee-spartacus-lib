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

package com.svoflee.spartacus.lib.dal;

import java.io.Serializable;
import java.util.List;

import javax.management.Query;

/**
 * IBaseRepository 是所有的Repository对象
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 * @param <E>
 * @param <PK>
 */
public interface IBaseRepository<E, PK extends Serializable> extends IPagingAndSortingRepository<E, PK> {

    // @Override
    // List<E> findAll();
    //
    // @Override
    // List<E> findAll(Sort sort);

    /**
     * return unique entity by same property value named propertyName
     * 
     * @param propertyName
     * @param propertyValue
     * @return unique entity
     */
    public E findUniqueByEq(String propertyName, Object propertyValue);

    /**
     * return entities by same property value named propertyName
     * 
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public List<E> findByEq(String propertyName, Object propertyValue);

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // List<E> saveEntities(Iterable<? extends E> entities);

    /**
     * Flushes all pending changes to the database.
     */
    void flush();

    /**
     * Saves an entity and flushes changes instantly.
     * 
     * @param entity
     * @return the saved entity
     */
    E saveAndFlush(E entity);

    /**
     * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will
     * clear the {@link EntityManager} after the call.
     * 
     * @param entities
     */
    void deleteInBatch(Iterable<E> entities);

}
