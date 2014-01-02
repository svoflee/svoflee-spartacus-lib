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

package com.svoflee.spartacus.lib.dal.nosql.mongodb;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.MongoURI;
import com.mongodb.ServerAddress;

/**
 * MongoFactory 是对mongo数据库的封装
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class MongoFactory extends Mongo {

    private String defaultDbName;

    public MongoFactory() throws UnknownHostException, MongoException {
        super();
    }

    public MongoFactory(List<ServerAddress> replicaSetSeeds, MongoOptions options) throws MongoException {
        super(replicaSetSeeds, options);

    }

    public MongoFactory(List<ServerAddress> replicaSetSeeds) throws MongoException {
        super(replicaSetSeeds);

    }

    public MongoFactory(MongoURI uri) throws MongoException, UnknownHostException {
        super(uri);

    }

    public MongoFactory(ServerAddress addr, MongoOptions options) throws MongoException {
        super(addr, options);

    }

    public MongoFactory(ServerAddress addr) throws MongoException {
        super(addr);

    }

    public MongoFactory(String host, int port) throws UnknownHostException, MongoException {
        super(host, port);

    }

    public MongoFactory(String host, MongoOptions options) throws UnknownHostException, MongoException {
        super(host, options);

    }

    public MongoFactory(String host) throws UnknownHostException, MongoException {
        super(host);

    }

    public void setDefaultDbName(String defaultDbName) {
        this.defaultDbName = defaultDbName;
    }

    public String getDefaultDbName() {
        return defaultDbName;
    }

    public DB getDefaultDB() {
        return getDB(getDefaultDbName());
    }

}
