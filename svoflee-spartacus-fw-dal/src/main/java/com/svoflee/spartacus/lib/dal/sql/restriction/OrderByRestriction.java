
package com.svoflee.spartacus.lib.dal.sql.restriction;

import com.svoflee.spartacus.lib.dal.sql.Field;

/**
 * OrderByRestriction 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class OrderByRestriction {

    /** 排序类型. */
    public enum OrderType {
        ASC, DESC;
    }

    private OrderType orderType;

    private Field field;

    /**
     * @param fieldName
     * @param orderType
     */
    public OrderByRestriction(String fieldName, OrderType orderType) {
        Field aField = new Field(fieldName, fieldName);
        field = aField;
        this.orderType = orderType;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    /**************************** Fields ****************************/

    /**************************** Public Methods ****************************/

    /**************************** Private Methods ****************************/

}
