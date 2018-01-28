package com.djt.result;

import com.djt.utils.EnhancedStringUtils;

public class BaseResult implements IResult
{

  private boolean success;

  private String code;

  private String msg;

  private Object data;

  private long timeStamp = System.currentTimeMillis();

  @Override
  public boolean isSuccess()
  {
    return success;
  }

  @Override
  public void setSuccess(boolean success)
  {
    this.success = success;
  }

  @Override
  public String getCode()
  {
    return code;
  }

  @Override
  public void setCode(String code)
  {
    this.code = code;
  }

  @Override
  public String getMsg()
  {
    return msg;
  }

  @Override
  public void setMsg(String msg)
  {
    this.msg = msg;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public Object getData()
  {
    return data;
  }

  public void setData(Object data)
  {
    this.data = data;
  }

  /**
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return EnhancedStringUtils.toString(this);
  }

}
