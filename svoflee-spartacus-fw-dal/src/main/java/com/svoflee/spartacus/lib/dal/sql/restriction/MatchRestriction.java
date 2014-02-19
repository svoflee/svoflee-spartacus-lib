
package com.svoflee.spartacus.lib.dal.sql.restriction;

import java.util.List;

import com.svoflee.spartacus.lib.dal.sql.RestrictionField;

/**
 * MatchRestriction 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class MatchRestriction {

    /** 属性比较类型. */
    public enum MatchType {
        EQ, LT, GT, LE, GE, IN, BETWEEN, LIKE_EXACT, LIKE_ANYWHERE, LIKE_START, LIKE_END, ISNULL, ISNOTNULL;
    }

    /**
     * 匹配类型
     */
    private MatchType matchType;

    /**
     * 约束字段
     */
    private RestrictionField field;

    /**
     * @param field
     * @param matchType
     */
    public MatchRestriction(RestrictionField field, MatchType matchType) {
        this.matchType = matchType;
        this.field = field;
    }

    public MatchRestriction(String fieldName, List<Object> values, MatchType matchType) {
        RestrictionField aRestrictionField = new RestrictionField(fieldName, values);
        this.matchType = matchType;
        field = aRestrictionField;

    }

    /**
     * @param name
     * @param matchType2
     */
    public MatchRestriction(String fieldName, MatchType matchType) {
        RestrictionField aRestrictionField = new RestrictionField(fieldName);
        field = aRestrictionField;
        this.matchType = matchType;

    }

    public void setField(RestrictionField field) {
        this.field = field;
    }

    public RestrictionField getField() {
        return field;
    }

    public void setMatchRestrictionType(MatchType matchType) {
        this.matchType = matchType;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    /**************************** Fields ****************************/

    /**************************** Public Methods ****************************/

    /**************************** Private Methods ****************************/

}
