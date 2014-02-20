
package com.svoflee.spartacus.lib.dal.sql.restriction;

/**
 * ResultRestriction 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class ResultRestriction {

    private int firstResult;

    private int maxResult;

    /**
     * 最大的查询记录条数
     */
    public static final int MAX_QUERY_RESULT_COUNT = 1000;

    /**
     * 起始记录编号
     */
    public static final int DEFAULT_FIRST_RESULT_NUM = 0;

    public ResultRestriction(int maxResult) {
        super();
        this.maxResult = maxResult;
    }

    public ResultRestriction() {
        super();
        firstResult = DEFAULT_FIRST_RESULT_NUM;
        maxResult = MAX_QUERY_RESULT_COUNT;
    }

    public ResultRestriction(int firstResult, int maxResult) {
        super();
        this.firstResult = firstResult;
        this.maxResult = maxResult;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public int getMaxResult() {
        return maxResult;
    }

}
