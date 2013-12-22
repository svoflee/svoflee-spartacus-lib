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

import org.springframework.data.repository.NoRepositoryBean;

import com.svoflee.spartacus.core.domain.query.Sort;

/**
 * ICrudRepository 是 Interface for generic CRUD operations
 * on a repository for a specific type.
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */

@NoRepositoryBean
public interface ICrudRepository<E, PK extends Serializable> extends IRepository<E, PK> {

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the entity instance completely.
     * 
     * @param entity
     * @return the saved entity
     */
    E saveEntity(E entity);

    /**
     * Saves all given entities.
     * 
     * @param entities
     * @return
     */
    List<E> saveEntities(Iterable<? extends E> entities);

    /**
     * Retrives an entity by its primary key.
     * 
     * @param id
     * @return the entity with the given primary key or {@code null} if none
     *         found
     * @throws IllegalArgumentException if primaryKey is {@code null}
     */
    E selectByPrimaryKey(PK id);

    /**
     * Returns whether an entity with the given id exists.
     * 
     * @param id
     * @return true if an entity with the given id exists, alse otherwise
     * @throws IllegalArgumentException if primaryKey is {@code null}
     */
    boolean exists(PK id);

    /**
     * Returns all instances of the type.
     * 
     * @return all entities
     */
    List<E> findAll();

    /**
     * Returns all entities sorted by the given options.
     * 
     * @param sort
     * @return all entities sorted by the given options
     */
    List<E> findAll(Sort sort);

    /**
     * Returns the number of entities available.
     * 
     * @return the number of entities
     */
    long count();

    /**
     * Deletes the entity with the given id.
     * 
     * @param id
     */
    void deleteEntity(PK id);

    /**
     * Deletes a given entity.
     * 
     * @param entity
     */
    void deleteEntity(E entity);

    /**
     * Deletes the given entities.
     * 
     * @param entities
     */
    void deleteEntities(Iterable<? extends E> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();
}
