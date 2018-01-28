package com.djt.result;

public interface IResult
{

  boolean isSuccess();

  void setSuccess(boolean success);

  String getCode();

  void setCode(String code);

  String getMsg();

  void setMsg(String msg);

  Object getData();

  void setData(Object data);

}
