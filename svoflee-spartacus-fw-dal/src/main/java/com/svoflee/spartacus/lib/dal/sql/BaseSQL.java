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

package com.svoflee.spartacus.lib.dal.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseSQL 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public abstract class BaseSQL implements SQL {

    private Type type;

    private List<SQLTable> sQLTables = new ArrayList<SQLTable>();

    private SQLTable sQLTable;

    @Override
    public Type getType() {
        return type;
    }

    public BaseSQL(Type type) {
        super();
        this.type = type;
    }

    public BaseSQL() {
        super();

    }

    public void setTables(List<SQLTable> sQLTables) {
        this.sQLTables = sQLTables;
    }

    @Override
    public List<SQLTable> getTables() {
        return sQLTables;
    }

    /**
     * @param tableName
     * @return
     */
    public SQLTable setTable(String tableName) {
        if (sQLTable == null) {
            sQLTable = new SQLTable(tableName);
        }
        else {
            sQLTable.setName(tableName);
        }
        sQLTables.add(sQLTable);
        return sQLTable;
    }

    public void setTable(SQLTable sQLTable) {
        sQLTables.add(sQLTable);

        this.sQLTable = sQLTable;
    }

    public SQLTable getTable() {
        return sQLTable;
    }

}
