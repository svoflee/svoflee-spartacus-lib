
package com.svoflee.spartacus.lib.dal.sql;

/**
 * DeleteSQL 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class DeleteSQL extends BaseSQL {

    // private List<Table> tables = new ArrayList<Table>();;

    public DeleteSQL() {
        super(Type.DELETE);
    }

    public DeleteSQL(String tableName) {
        this();
        this.setTable(tableName);
    }

}
