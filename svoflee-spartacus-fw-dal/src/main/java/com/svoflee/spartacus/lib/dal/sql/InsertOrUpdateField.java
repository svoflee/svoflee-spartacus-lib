
package com.svoflee.spartacus.lib.dal.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * InsertOrUpdateField 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class InsertOrUpdateField extends Field {

    private List<Object> values = new ArrayList<Object>();;

    /**
     * @param name
     * @param alias
     */
    public InsertOrUpdateField(String name) {
        super(name, null);

    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }

    /**
     * @param value
     */
    public void addValue(Object value) {
        values.add(value);
    }

    /**
     * @param i
     * @return
     */
    public Object getValue(int i) {
        return getValues().get(i);
    }

    /**************************** Fields ****************************/

    /**************************** Public Methods ****************************/

    /**************************** Private Methods ****************************/

}
