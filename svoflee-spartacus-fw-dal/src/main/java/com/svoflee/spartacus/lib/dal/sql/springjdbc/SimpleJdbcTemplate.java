
package com.svoflee.spartacus.lib.dal.sql.springjdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.svoflee.spartacus.lib.dal.sql.SqlBuilder;

public class SimpleJdbcTemplate extends JdbcTemplate {

    private SqlBuilder sqlBuilder;

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);

    }

    public void setSqlBuilder(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }
}
