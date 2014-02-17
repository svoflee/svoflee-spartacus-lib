
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
