
package com.svoflee.spartacus.lib.dal.sql.springjdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchUpdateUtils;
import org.springframework.util.Assert;

import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.lib.dal.sql.InsertOrUpdateField;
import com.svoflee.spartacus.lib.dal.sql.InsertOrUpdateSQL;
import com.svoflee.spartacus.lib.dal.sql.SQL;
import com.svoflee.spartacus.lib.dal.sql.SQLTable;
import com.svoflee.spartacus.lib.dal.sql.SQLUtils;
import com.svoflee.spartacus.lib.dal.sql.SelectSQL;
import com.svoflee.spartacus.lib.dal.sql.SqlBuilder;

/**
 * @param <E>
 * @param <PK>
 * @since 2005-8-25 14:15:04
 */
// @Repository
public abstract class BaseJdbcDao {

    protected Logger logger = Logger.getLogger(this.getClass());

    private SimpleJdbcTemplate jdbcTemplate;

    /**
     * @return
     * @throws SQLException
     */
    public DatabaseMetaData getDBMetaData() throws SQLException {
        DatabaseMetaData gtarscdbMetaData = getJdbcTemplate().getDataSource().getConnection().getMetaData();
        return gtarscdbMetaData;
    }

    public void deletaDatas(String tableName) {
        String sql = "DELETE " + tableName;
        this.executeSql(sql);

    }

    public int queryTableTotalRecordCount(String tableName) {
        String sql = "select Count(*) from " + tableName;

        // ResultSetWrappingSqlRowSet aResultSetWrappingSqlRowSet = this.queryForRowSet(sql);
        // aResultSetWrappingSqlRowSet.next();
        return getJdbcTemplate().queryForInt(sql);
    }

    /**
     * 返回成功执行的sql在List中的index
     * 
     * @param sqls
     * @return
     */
    public int[] executeInsert(List<String> sqls) {
        logger.debug("batchInsert......sql amount: " + sqls.size());
        // int[] r = this.getJdbcTemplate().batchUpdate(sqls.toArray(new String[0]));
        // return r;

        List<Integer> exeResults = new ArrayList();
        int i = 0;
        String sql = "";
        for (; i < sqls.size(); i++) {
            try {
                sql = sqls.get(i);
                int exeResult = getJdbcTemplate().update(sql);
                // this.executeSql(sql);
                exeResults.add(exeResult);
            }
            catch (Exception e) {
                // logger.error("wrong SQL --> no." + i + "  SQL:  " + sql);
                // exeResults.add(i);
            }
        }
        int[] result = new int[exeResults.size()];
        for (int j = 0; j < result.length; j++) {
            result[j] = exeResults.get(j);
        }
        return result;
    }

    public int[] executeBatchInsert(List<String> sqls) {
        return getJdbcTemplate().batchUpdate(sqls.toArray(new String[0]));
    }

    /**
     * @param aUpdateSQL
     * @return
     */
    public int[] singleUpdate(SQL sql) {
        // XXX:这里的API需要优化一下结构，目前暂时使用这种实现方式，需要修改SQLUtils.getBatchInsertOrUpdateValues方法
        return batchInsertOrUpdateSQL(sql);
    }

    /**
     * Issue multiple SQL updates on a single JDBC Statement using batching.
     * <p>
     * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates.
     * 
     * @return an array of the number of rows affected by each statement
     * @throws DataAccessException if there is any problem executing the batch
     */
    public int[] batchInsertOrUpdateSQL(SQL sql) {
        String sqlString = getSqlString(sql);
        int[] result = BatchUpdateUtils.executeBatchUpdate(sqlString, SQLUtils.getBatchInsertOrUpdateValues(sql), null,
                getJdbcTemplate());
        return result;
    }

    /**
     * @param sql
     * @return
     */
    private String getSqlString(SQL sql) {
        SqlBuilder sqlbuider = getJdbcTemplate().getSqlBuilder();
        String sqlString = sqlbuider.buildSqlString(sql);
        return sqlString;
    }

    public int[] insertSQLOneByOne(SQL sql) {
        Assert.isTrue(sql instanceof InsertOrUpdateSQL, "类型不符合");
        String sqlString = getSqlString(sql);
        InsertOrUpdateSQL insertSql = (InsertOrUpdateSQL) sql;
        SQLTable sQLTable = insertSql.getTable();
        List<InsertOrUpdateField> insertFs = sQLTable.getinsertOrUpdateFields();
        int columnCount = insertFs.size();
        List<Integer> resultIntList = new ArrayList<Integer>();
        int rowcount = insertFs.get(0).getValues().size();// XXX:取出第一列的所有需要插入的值作为总的插入行数，
        // 这里有点问题,是否存在其他列值的数量多于第一列的情况，需要考虑
        List<Object> rowValues = new ArrayList<Object>();
        for (int i = 0; i < rowcount; i++) {
            rowValues.clear();
            for (int j = 0; j < columnCount; j++) {
                InsertOrUpdateField f = insertFs.get(j);
                Object value = f.getValue(i);
                rowValues.add(value);
            }
            Object[] args = rowValues.toArray();
            try {
                int r = getJdbcTemplate().update(sqlString, args);
                resultIntList.add(r);
            }
            catch (org.springframework.dao.DataAccessException e) {
                // 抓取异常，保证不抛出异常
                // logger.error(e.getMessage());
            }
        }
        int[] result = new int[resultIntList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = resultIntList.get(i);
        }
        return result;
    }

    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code>
     * or <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     * 
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
     * @exception SQLException if a database access error occurs;
     *                this method is called on a closed <code>PreparedStatement</code> or the SQL
     *                statement returns a <code>ResultSet</code> object
     */
    public int executeUpdate(SQL sql) {
        String sqlString = getSqlString(sql);
        Object[] args = SQLUtils.getRestrictArgsFromSql(sql);
        return getJdbcTemplate().update(sqlString, args);
    }

    /**
     * Issue a single SQL execute, typically a DDL statement.
     * 
     * @param sql static SQL to execute
     * @throws DataAccessException if there is any problem
     */
    public void executeSql(String sql) {
        getJdbcTemplate().execute(sql);
    }

    public void executeSql(List<String> sqls) {
        for (String sql : sqls) {
            getJdbcTemplate().execute(sql);
        }
    }

    /**
     * @param sql
     * @return
     */
    public long queryCountByConditions(SelectSQL sql) {
        String sqlString = getJdbcTemplate().getSqlBuilder().buildSqlString(sql);
        Object[] args = SQLUtils.getRestrictArgsFromSql(sql);

        long result = getJdbcTemplate().queryForLong(sqlString, args);
        return result;
    }

    public List<Map<String, Object>> queryForList(SQL sql) {
        Assert.isTrue(sql instanceof SelectSQL, "sql错误:sql必须为SelectSQL类型");
        String sqlString = getJdbcTemplate().getSqlBuilder().buildSqlString(sql);
        Object[] args = SQLUtils.getRestrictArgsFromSql(sql);
        List<Map<String, Object>> result = getJdbcTemplate().queryForList(sqlString, args);
        return result;
    }

    /**
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryForList(String sql) {
        return getJdbcTemplate().queryForList(sql);
    }

    protected void printDbMetaInfo() {
        DatabaseMetaData md;
        try {
            md = jdbcTemplate.getDataSource().getConnection().getMetaData();

            // log.debug(md.getDatabaseMajorVersion());
            // log.debug(md.getDatabaseMinorVersion());
            // log.debug(md.getDatabaseProductName());
            // log.debug(md.getDatabaseProductVersion());
            // log.debug(md.getDefaultTransactionIsolation());
            // log.debug(md.getDriverMajorVersion());
            // log.debug(md.getDriverMinorVersion());
            // log.debug(md.getDriverName());
            // log.debug(md.getDriverVersion());
            // log.debug(md.getExtraNameCharacters());
            // log.debug(md.getIdentifierQuoteString());
            //
            // log.debug(md.getJDBCMajorVersion());
            // log.debug(md.getJDBCMinorVersion());
            // log.debug(md.getMaxBinaryLiteralLength());
            // log.debug(md.getMaxCatalogNameLength());
            // log.debug(md.getMaxColumnsInGroupBy());
            // log.debug(md.getMaxColumnsInIndex());
            // log.debug(md.getMaxColumnsInOrderBy());
            // log.debug(md.getMaxColumnsInSelect());
            // log.debug(md.getMaxColumnsInTable());
            // log.debug(md.getMaxConnections());
            // log.debug(md.getMaxCursorNameLength());
            // log.debug(md.getMaxIndexLength());
            // log.debug(md.getMaxProcedureNameLength());
            // log.debug(md.getMaxRowSize());
            // log.debug(md.getMaxSchemaNameLength());
            // log.debug(md.getMaxStatementLength());
            // log.debug(md.getMaxStatements());
            // log.debug(md.getMaxTableNameLength());
            // log.debug(md.getMaxUserNameLength());
            // log.debug(md.getNumericFunctions());
            // log.debug(md.getProcedureTerm());
            //
            // // log.debug(md.getResultSetHoldability());
            // log.debug(md.getSchemaTerm());
            // log.debug(md.getSearchStringEscape());
            // log.debug(md.getSQLKeywords());
            //
            // // log.debug(md.getSQLStateType());
            // log.debug(md.getStringFunctions());
            // log.debug(md.getSystemFunctions());
            // log.debug(md.getTimeDateFunctions());
            // log.debug(md.getURL());
            // log.debug(md.getUserName());
            // log.debug(DatabaseMetaData.importedKeyCascade);
            // log.debug(md.isCatalogAtStart());
            // log.debug(md.isReadOnly());
            // String[] types = { "TABLE" };
            // ResultSet tableRet = md.getTables("", "%", "%", types);
            // while (tableRet.next())
            // System.out.println(tableRet.getString("TABLE_NAME"));

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 如果要区分数据源，则需要自行override该方法，将@Resources进行重新制定
     * 
     * @param jdbcTemplate the jdbcTemplate to set
     */
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @return the jdbcTemplate
     */
    public SimpleJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
