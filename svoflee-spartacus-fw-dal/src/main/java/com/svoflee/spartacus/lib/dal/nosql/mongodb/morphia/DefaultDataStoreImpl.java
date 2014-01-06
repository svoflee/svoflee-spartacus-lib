/*
 * @(#)DefaultDataStoreImpl.java  
 *       
 * 系统名称：  
 * 版本号：      1.0.0
 *  
 * Copyright (c)  Victor Li
 * All rights reserved.
 * 
 * 作者: 	    Victor Li
 * 创建日期:    2012-2-10 下午06:16:55
 * 
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
