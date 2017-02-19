	package com.github.adminfaces.template.exception;

/**
 * Created by rmpestano on 18/02/17.
 * A marker exception to redirect user to 403.xhtml. See web-fragment.xml
 */
@ApplicationException(rollback = true)
public class AccessDeniedException  extends RuntimeException implements Serializable {

	  public BusinessException() {
    	}

    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param msg exception message
     */
    public BusinessException(String msg) {
        super(msg);
    }
    
}
