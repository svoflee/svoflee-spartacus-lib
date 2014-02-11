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

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

/**
 * DefaultDataStoreImpl 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class DefaultDataStoreImpl extends DatastoreImpl implements Serializable {

    private static final long serialVersionUID = -7410637967910532182L;

    public DefaultDataStoreImpl(Morphia morphia, Mongo mongo, String dbName, String username, char[] password) {
        super(morphia, mongo, dbName, username, password);

    }

    public DefaultDataStoreImpl(Morphia morphia, Mongo mongo, String dbName) {
        super(morphia, mongo, dbName);
    }

    public DefaultDataStoreImpl(Morphia morphia, Mongo mongo) {
        super(morphia, mongo);

    }

}
