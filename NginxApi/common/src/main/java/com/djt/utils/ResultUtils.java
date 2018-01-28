package com.djt.utils;

import com.djt.result.BaseResult;
import com.djt.result.IResult;

import java.util.HashMap;
import java.util.Map;

public final class ResultUtils
{
  private ResultUtils()
  {
  }

  private static final String SUCCESS = "success";

  private static final String CODE = "code";

  private static final String MSG = "msg";

  private static final String DATA = "data";

  public static IResult createErrorResult(String code, String msg, Object data)
  {
    BaseResult result = new BaseResult();
    result.setSuccess(false);
    result.setMsg(msg);
    result.setCode(code);
    result.setData(data);
    return result;
  }

  public static IResult createResult(Object data)
  {
    BaseResult result = new BaseResult();
    result.setSuccess(true);
    result.setData(data);
    return result;
  }

  public static Map<String, Object> fail(String code, String msg)
  {
    return fail(code, msg, null);
  }

  public static Map<String, Object> fail(String code, String msg, Object data)
  {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put(SUCCESS, Boolean.FALSE);
    result.put(CODE, code);
    result.put(MSG, msg);
    result.put(DATA, data);

    return result;
  }

  public static Map<String, Object> success(Object data)
  {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put(SUCCESS, Boolean.TRUE);
    result.put(DATA, data);
    result.put(CODE, null);
    result.put(MSG, null);

    return result;
  }

}
