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

import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.Pageable;

/**
 * IPagingAndSortingRepository 是具有排序及分页的处理
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public interface IPagingAndSortingRepository<E, PK extends Serializable> extends ICrudRepository<E, PK> {

    /**
     * Returns a {@link Page} of entities meeting the paging restriction
     * provided in the {@code Pageable} object.
     * 
     * @param pageable
     * @return a page of entities
     */
    Pageable findAll(CriteriaContainer aCriteriaContainer);
}
