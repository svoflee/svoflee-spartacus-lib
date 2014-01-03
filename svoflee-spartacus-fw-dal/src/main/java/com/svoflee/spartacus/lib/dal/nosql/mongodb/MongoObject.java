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

import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

/**
 * MongoObject 是系统中需要使用MongoDb持久化的对象，继承之该类
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class MongoObject extends BasicDBObject {

    private static final long serialVersionUID = 1011460769126195740L;

    public static final String MONGO_ID_PROPERTY_NAME = "_id";

    public static final String ID_PROPERTY_NAME = "id";

    private String id;

    public MongoObject() {
        super();
        this.setId();
    }

    private void setId() {
        this.setId(new ObjectId().toString());
    }

    public MongoObject(int size) {
        super(size);
        this.setId();

    }

    public MongoObject(Map m) {
        super(m);
        this.setId();
    }

    public MongoObject(String key, Object value) {
        super(key, value);
        this.setId();
    }

    public void setId(String id) {
        this.id = id;
        put(MONGO_ID_PROPERTY_NAME, id);

    }

    public String getId() {
        return (String) this.get(MONGO_ID_PROPERTY_NAME);
    }

    // public void set_id(String _id) {
    // this.id = _id;
    // this.put(MONGO_ID_PROPERTY_NAME, _id);
    // }
    //
    // public String get_id() {
    // return this.get(MONGO_ID_PROPERTY_NAME).toString();
    // }

}
