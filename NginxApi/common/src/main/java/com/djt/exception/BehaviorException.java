package com.djt.exception;

public class BehaviorException extends RuntimeException
{

  /**  */
  private static final long serialVersionUID = 5039004110590768666L;
  private String errorCode;

  public BehaviorException()
  {
    super();
  }

  public BehaviorException(String msg)
  {
    super(msg);
  }

  public BehaviorException(Throwable t, String msg)
  {
    super(msg, t);
    if (t instanceof BehaviorException) {
      this.errorCode = ((BehaviorException) t).getErrorCode();
    }
  }

  public BehaviorException(String errorCode, Throwable t)
  {
    this(errorCode, t, null);
  }

  public BehaviorException(String errorCode, Throwable t, String msg)
  {
    super(msg, t);
    this.errorCode = errorCode;
  }

  /**
   * Getter method for property <tt>errorCode</tt>.
   *
   * @return property value of errorCode
   */
  public String getErrorCode()
  {
    return errorCode;
  }

  /**
   * Setter method for property <tt>errorCode</tt>.
   *
   * @param errorCode value to be assigned to property errorCode
   */
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }

}
