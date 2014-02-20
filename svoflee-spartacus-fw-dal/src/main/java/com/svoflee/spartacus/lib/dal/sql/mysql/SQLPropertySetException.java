
package com.svoflee.spartacus.lib.dal.sql.mysql;

import com.svoflee.spartacus.core.utils.exception.AppException;

/**
 * SQLPropertySetException 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class SQLPropertySetException extends AppException {

    private static final long serialVersionUID = -460817201010623070L;

    public SQLPropertySetException() {
        super();

    }

    public SQLPropertySetException(String message, Throwable cause) {
        super(message, cause);

    }

    public SQLPropertySetException(String message) {
        super(message);

    }

    public SQLPropertySetException(Throwable cause) {
        super(cause);

    }

}
