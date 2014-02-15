
package com.svoflee.spartacus.lib.dal.sql.mysql;

import java.util.List;

import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.lib.dal.sql.DeleteSQL;
import com.svoflee.spartacus.lib.dal.sql.Field;
import com.svoflee.spartacus.lib.dal.sql.InsertOrUpdateField;
import com.svoflee.spartacus.lib.dal.sql.InsertOrUpdateSQL;
import com.svoflee.spartacus.lib.dal.sql.RestrictionField;
import com.svoflee.spartacus.lib.dal.sql.SQL;
import com.svoflee.spartacus.lib.dal.sql.SQL.Type;
import com.svoflee.spartacus.lib.dal.sql.SQLTable;
import com.svoflee.spartacus.lib.dal.sql.SelectSQL;
import com.svoflee.spartacus.lib.dal.sql.SqlBuilder;
import com.svoflee.spartacus.lib.dal.sql.SqlClause;
import com.svoflee.spartacus.lib.dal.sql.restriction.MatchRestriction;
import com.svoflee.spartacus.lib.dal.sql.restriction.MatchRestriction.MatchType;
import com.svoflee.spartacus.lib.dal.sql.restriction.OrderByRestriction;
import com.svoflee.spartacus.lib.dal.sql.restriction.OrderByRestriction.OrderType;

/**
 * MysqlSqlBuilderImpl 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class MysqlSqlBuilderImpl implements SqlBuilder {

    private static final Logger logger = Logger.getLogger(MysqlSqlBuilderImpl.class);

    @Override
    public String buildSqlString(SQL sql) {
        // List<String> result=new ArrayList();
        MySqlSqlClause clause = new MySqlSqlClause();

        switch (sql.getType()) {
            case SELECT: {
                MySqlSqlClause fieldsClause = new MySqlSqlClause();
                MySqlSqlClause fromClause = new MySqlSqlClause();
                MySqlSqlClause whereClause = null;
                MySqlSqlClause orderClause = new MySqlSqlClause();

                SelectSQL selectSql = (SelectSQL) sql;
                List<SQLTable> sQLTables = selectSql.getTables();
                if (sQLTables.size() < 1) {
                    logger.error("select 语句没有设置需要选择的表 [{}]", selectSql.getTables().size());
                    throw new SQLPropertySetException("select 语句没有设置需要选择的表");
                }
                int tableCount = sQLTables.size();
                // fieldsClause
                for (int k = 0; k < sQLTables.size(); k++) {
                    SQLTable sQLTable = sQLTables.get(k);
                    boolean lastTable = (k == tableCount - 1);
                    setFieldsClauseByTable(fieldsClause, lastTable, sQLTable);
                    // fromClause
                    setFromClauseByTable(fromClause, lastTable, sQLTable);
                    // whereClause
                    whereClause = (MySqlSqlClause) getWhereClauseByTable(lastTable, sQLTable);

                    setOrderClauseByTable(orderClause, lastTable, sQLTable);
                }
                clause.select();// select...
                clause.addFielsClause(fieldsClause);// f1,f2...
                clause.from(fromClause);// from...
                clause.where(whereClause);// where...
                clause.orderBy(orderClause);// order by
                if (selectSql.isPage()) {
                    clause.page(selectSql.getFirstResult(), selectSql.getMaxResult());//
                }
                break;
            }
            case INSERT: {
                MySqlSqlClause fieldsClause = new MySqlSqlClause();

                InsertOrUpdateSQL insertSql = (InsertOrUpdateSQL) sql;
                SQLTable sQLTable = insertSql.getTable();
                String tableName = sQLTable.getName();
                List<InsertOrUpdateField> fields = sQLTable.getinsertOrUpdateFields();
                int fieldsCount = fields.size();
                int i = 0;
                for (InsertOrUpdateField f : fields) {
                    i++;
                    String fieldName = f.getName();
                    fieldsClause.fieldName(fieldName);
                    if (i != fieldsCount) {
                        fieldsClause.sep();
                    }
                }
                clause.insert(tableName);// insert into tableName
                clause.append("(");
                clause.addFielsClause(fieldsClause);// f1,f2,....
                clause.append(")");

                clause.values(fieldsCount);// values(?,?,?)
                break;
            }
            case UPDATE: {
                MySqlSqlClause fieldsClause = new MySqlSqlClause();
                InsertOrUpdateSQL insertSql = (InsertOrUpdateSQL) sql;
                SQLTable sQLTable = insertSql.getTable();
                String tableName = sQLTable.getName();
                List<InsertOrUpdateField> fields = sQLTable.getinsertOrUpdateFields();
                int fieldsCount = fields.size();
                int i = 0;
                for (InsertOrUpdateField f : fields) {
                    i++;
                    String fieldName = f.getName();
                    fieldsClause.updateFieldName(fieldName);
                    if (i != fieldsCount) {
                        fieldsClause.sep();
                    }
                }
                MySqlSqlClause whereClause = (MySqlSqlClause) getWhereClauseByTable(sQLTable);

                clause.update(tableName);// update tableName set
                clause.addFielsClause(fieldsClause);// f1=?,f2=?,....
                clause.where(whereClause);// where...
                break;
            }
            case DELETE: {
                DeleteSQL deleteSql = (DeleteSQL) sql;
                SQLTable sQLTable = deleteSql.getTable();
                String tableName = sQLTable.getName();
                MySqlSqlClause whereClause = (MySqlSqlClause) getWhereClauseByTable(sQLTable);

                clause.delete(tableName);// delete from..
                clause.where(whereClause);// where...
                break;
            }
            default:
                break;
        }
        String result = clause.toString();
        logger.debug(result);

        return result;
    }

    /**
     * @param sQLTable
     * @return
     */
    private SqlClause getWhereClauseByTable(SQLTable sQLTable) {
        return this.getWhereClauseByTable(true, sQLTable);
    }

    /**
     * @param orderClause
     * @param lastTable
     * @param sQLTable
     */
    private void setOrderClauseByTable(SqlClause orderClause, boolean lastTable, SQLTable sQLTable) {
        List<OrderByRestriction> orders = sQLTable.getOrderByRestrictions();
        for (int i = 0; i < orders.size(); i++) {
            OrderByRestriction order = orders.get(i);
            Field f = order.getField();
            String fieldName = f.getName();
            String tableAlias = f.getTable().getAlias();
            OrderType ot = order.getOrderType();
            if (ot == OrderType.ASC) {
                orderClause.asc(tableAlias, fieldName);
            }
            else {
                orderClause.desc(tableAlias, fieldName);
            }
            if (!lastTable || (i != orders.size() - 1)) {
                orderClause.sep();
            }
        }
    }

    /**
     * @param whereClause
     * @param lastTable
     * @param sQLTable
     * @return
     */
    private SqlClause getWhereClauseByTable(boolean lastTable, SQLTable sQLTable) {
        MySqlSqlClause whereClause = new MySqlSqlClause();
        List<MatchRestriction> matchs = sQLTable.getMatchs();
        for (int i = 0; i < matchs.size(); i++) {
            MatchRestriction matchRestriction = matchs.get(i);
            MatchType matchType = matchRestriction.getMatchType();

            RestrictionField field = matchRestriction.getField();
            String fName = field.getName();
            SQLTable fTable = field.getTable();
            int paramCount = field.getValues().size();
            String tableAlias = fTable.getAlias();

            switch (matchType) {
                case EQ:
                    whereClause.eq(tableAlias, fName);
                    break;
                case LIKE_EXACT:
                    whereClause.like(tableAlias, fName);
                    break;
                case LIKE_ANYWHERE:
                    whereClause.like(tableAlias, fName);
                    break;
                case LIKE_START:
                    whereClause.like(tableAlias, fName);
                    break;
                case LIKE_END:
                    whereClause.like(tableAlias, fName);
                    break;
                case LT:
                    whereClause.lt(tableAlias, fName);
                    break;
                case GT:
                    whereClause.gt(tableAlias, fName);
                    break;
                case LE:
                    whereClause.le(tableAlias, fName);
                    break;
                case GE:
                    whereClause.ge(tableAlias, fName);
                    break;
                case IN:
                    whereClause.in(tableAlias, fName, paramCount);
                    break;
                case BETWEEN:
                    whereClause.between(tableAlias, fName);
                    break;
                case ISNULL:
                    whereClause.isnull(tableAlias, fName);
                    break;
                case ISNOTNULL:
                    whereClause.isnotnull(tableAlias, fName);
                    break;
                default:
                    break;
                // whereClaus.between(tableAlias,fieldName);
            }

            if (!lastTable) {
                whereClause.and();
            }
            else {
                if (i != matchs.size() - 1) {
                    whereClause.and();
                }
            }

        }
        return whereClause;
    }

    /**
     * @param fromClause
     * @param tables
     * @param k
     * @param sQLTable
     */
    private void setFromClauseByTable(SqlClause fromClause, boolean lastTable, SQLTable sQLTable) {
        String alias = sQLTable.getAlias();

        String name = sQLTable.getName();
        fromClause.tableName(name);
        fromClause.as(alias);
        if (!lastTable) {
            fromClause.sep();
        }
    }

    /**
     * @param fieldsClause
     * @param tables
     * @param k
     * @param sQLTable
     * @return
     */
    private void setFieldsClauseByTable(SqlClause fieldsClause, boolean lastTable, SQLTable sQLTable) {
        String tableAlias = sQLTable.getAlias();
        if (sQLTable.isCount()) {
            fieldsClause.count(tableAlias);
            if (!lastTable) {
                fieldsClause.sep();
            }
            return;
        }
        if (sQLTable.isSelectAll()) {
            fieldsClause.selectAll(tableAlias);
        }
        else {
            // Table table = tables.get(k);
            List<Field> fields = sQLTable.getSelectFields();
            if (fields.size() == 0) {
                logger.debug("没有设置要select的fields：{},采用select * 进行选择", fields.size());
                fieldsClause.selectAll(tableAlias);
            }
            else {
                for (int i = 0; i < fields.size(); i++) {
                    Field f = fields.get(i);
                    String f_alias = f.getAlias();
                    String f_name = f.getName();
                    SQLTable t = f.getTable();
                    String tn = t.getName();
                    // String tableAlias = t.getAlias();

                    fieldsClause.tableNameDot(tableAlias);
                    fieldsClause.fieldName(f_name);
                    fieldsClause.as(tableAlias + "_" + f_alias);
                    if (!lastTable) {
                        fieldsClause.sep();

                    }
                    else {
                        if (i != fields.size() - 1) {
                            fieldsClause.sep();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        MysqlSqlBuilderImpl aMysqlSqlBuilderImpl = new MysqlSqlBuilderImpl();
        testSelect(aMysqlSqlBuilderImpl);
        // InsertSQL sql = testInsert();
        // DeleteSQL sql = testDelete();
        // InsertOrUpdateSQL sql = testInsertOrUpdateSQL();

        // System.out.print(aMysqlSqlBuilderImpl.buildSqlString(sql));

    }

    /**
     * @return
     */
    private static InsertOrUpdateSQL testInsertOrUpdateSQL() {
        InsertOrUpdateSQL aInsertOrUpdateSQL = new InsertOrUpdateSQL(Type.UPDATE, "tablename");
        aInsertOrUpdateSQL.getTable().insertOrUpdate("c1", "c1");
        aInsertOrUpdateSQL.getTable().insertOrUpdate("c2", "c2");
        aInsertOrUpdateSQL.getTable().eq("c3", "c3");
        return aInsertOrUpdateSQL;
    }

    /**
     * @return
     */
    private static DeleteSQL testDelete() {
        DeleteSQL aDeleteSQL = new DeleteSQL("tablename");
        SQLTable t = aDeleteSQL.getTable();
        t.addMatchRestriction("fn1", "fnv1", MatchType.EQ);
        return aDeleteSQL;
    }

    /**
     * @return
     */
    private static InsertOrUpdateSQL testInsert() {
        InsertOrUpdateSQL sql = new InsertOrUpdateSQL(Type.UPDATE);
        SQLTable sQLTable = sql.setTable("tablename");
        sQLTable.insertOrUpdate("f1", "v1");
        sQLTable.insertOrUpdate("f1", "v1");
        sQLTable.insertOrUpdate("bbb", "v1");
        sQLTable.insertOrUpdate("aaa", "v1");
        return sql;
    }

    /**
     * @param aMysqlSqlBuilderImpl
     */
    private static void testSelect(MysqlSqlBuilderImpl aMysqlSqlBuilderImpl) {
        SelectSQL selectSQL = new SelectSQL();
        SQLTable sQLTable = new SQLTable("tname1");
        sQLTable.isNull("col1");
        selectSQL.page(0, 1);
        // sQLTable.setCount(true);
        // sQLTable.addSelectField("n1", "a1");
        // sQLTable.addSelectField("n2", "a2");
        //
        // SQLTable table2 = new SQLTable("tname2", "ta2");
        // table2.addSelectField("t_n2", "t_a2");
        //
        // sQLTable.addMatchRestriction("n1", "value1", MatchType.EQ);
        // sQLTable.addMatchRestriction("n2", "value2", MatchType.LIKE_EXACT);
        // table2.addMatchRestriction("t_n2", "value2", MatchType.LIKE_EXACT);
        //
        // //Table table3 = selectSQL.addTable("tname1", "ta1");
        selectSQL.addTable(sQLTable);
        // selectSQL.addTable(table2);

        System.out.print(aMysqlSqlBuilderImpl.buildSqlString(selectSQL));
    }

}
