package com.search.manager.exception;

import org.apache.log4j.Logger;

public class BaseException extends Exception {
  
  private static final long serialVersionUID = -4196777337018634572L;
  private static Logger logger = Logger.getLogger(BaseException.class);

  /**
   * Constructor.
   */
  public BaseException() {
    
  }
  
  /**
   * Constructor.
   * 
   * @param s the error message.
   */
  public BaseException(String s) {
    super(s);
  }

  /**
   * Constructor.
   * 
   * @param s the error message.
   * @param throwable the exception.
   */
  public BaseException(String s, Throwable throwable) {
    super(s, throwable);
  }

  /**
   * Constructor.
   * 
   * @param throwable the exception.
   */
  public BaseException(Throwable throwable) {
    super(throwable);
  }
  
  /**
   * Returns the BaseException instance.
   * 
   * @param e the exception.
   * @return the BaseException instance.
   */
  public static BaseException getInstance(Exception e) {
    if (BaseException.class.isAssignableFrom(e.getClass())) {
      return (BaseException) e;
    } else {
    	try {
    		logger.info(e.getMessage());
    	} catch (NullPointerException npex) {
    		logger.info(npex.getMessage());
    	}
      return new BaseException(e);
    }
  }

}
