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

/**
 * Field 是字段
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class Field {

    private SQLTable sQLTable;

    private String name;

    private String alias;

    // private String[] stringValues;

    private int type;

    public Field(String name, String alias) {
        this.alias = alias;
        this.name = name;
    }

    /**
     * @return the table
     */
    public SQLTable getTable() {
        return sQLTable;
    }

    /**
     * @param sQLTable
     *            the table to set
     */

    public void setTable(SQLTable sQLTable) {
        this.sQLTable = sQLTable;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias
     *            the alias to set
     */

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**************************** Fields ****************************/

    /**************************** Public Methods ****************************/

    /**************************** Private Methods ****************************/

}
