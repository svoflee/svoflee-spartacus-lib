
package com.svoflee.spartacus.lib.dal;

import com.svoflee.spartacus.core.utils.exception.AppException;

/**
 * RepositoryException 是Repository层处理异常的基类
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class RepositoryException extends AppException {

    private static final long serialVersionUID = -3146718806638027542L;

    public RepositoryException() {
        super();
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);

    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

}
