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

import com.svoflee.spartacus.core.utils.exception.AppException;

/**
 * RepositoryException 是Repository层处理异常的基类
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class RepositoryException extends AppException {

    private static final long serialVersionUID = -3146718806638027542L;

    public RepositoryException() {
        super();
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);

    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

}
