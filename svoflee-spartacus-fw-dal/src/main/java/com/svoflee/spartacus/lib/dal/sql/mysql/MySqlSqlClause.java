
package com.svoflee.spartacus.lib.dal.sql.mysql;

import com.svoflee.spartacus.lib.dal.sql.SqlClause;

public class MySqlSqlClause extends SqlClause {

    /**************************** Fields ****************************/

    /**************************** Public Methods ****************************/

    /**
     * 设置select 的分页
     * 
     * @param first
     * @param max
     * @return
     */
    @Override
    public StringBuilder page(int first, int max) {
        return getSb().append(" LIMIT ").append(first).append(",").append(max);
    }

    /**************************** Private Methods ****************************/

}
