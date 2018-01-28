package com.djt.controller;

import com.djt.exception.ParameterException;
import com.djt.exception.BehaviorException;
import com.djt.result.IResult;
import com.djt.result.ResultConstants;
import com.djt.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class BaseController
{
  private final MyLogger LOG = MyLogger.getLogger(this.getClass());

  private static final String FIELD_BIND_ERROR_MSG = "{0}值非法:{1}";

  @Autowired(required = false)
  private HttpServletRequest httpServletRequest;

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public IResult dealBindException(HttpServletRequest request, BindException e)
  {
    LOG.error("binding exception, request uri:{}", e, getUrlAndParamString(request));

    //only record first field error
    FieldError fieldError = e.getBindingResult().getFieldError();
    String field = fieldError.getField();
    String message = EnhancedStringUtils.formatMsg(FIELD_BIND_ERROR_MSG, field, fieldError.getRejectedValue());

    return createErrorResult(ResultConstants.CODE_PARAMETER_ERROR, message, null);
  }

  @ExceptionHandler
  @ResponseBody
  public IResult dealException(HttpServletRequest request, Exception e)
  {
    String errorCode = ResultConstants.CODE_UNKNOWN_ERROR;
    String msg = null;
    if (e instanceof BehaviorException) {
      BehaviorException ex = (BehaviorException) e;

      if (ex.getErrorCode() != null)
      {
        LOG.warn("internal error. Msg:{},code:{}, Uri:{}", ex.getMessage(),
          ex.getErrorCode(),
          getUrlAndParamString(request));
      } else {
        LOG.error("internal error. Uri:" + getUrlAndParamString(request), e);
      }

      BehaviorException behaviorException = (BehaviorException) e;
      if (StringUtils.isNotBlank(behaviorException.getErrorCode())) {
        errorCode = behaviorException.getErrorCode();
      }
      if (StringUtils.isNotBlank(behaviorException.getMessage())) {
        msg = behaviorException.getMessage();
      }
    } else {
      LOG.error("internal error. Uri:" + getUrlAndParamString(request), e);
      if (e instanceof MethodArgumentNotValidException || e instanceof ServletRequestBindingException
        || e instanceof ParameterException || e instanceof TypeMismatchException)
      {
        errorCode = ResultConstants.CODE_PARAMETER_ERROR;
      }
    }

    if (StringUtils.isBlank(msg)) {
      msg = MessageUtil.getMessage(errorCode);
    }

    if (StringUtils.isBlank(msg)) {
      msg = e.getMessage();
    }

    return createErrorResult(errorCode, msg, null);
  }

  protected IResult createErrorResult(String code, String msg, Object data)
  {
    return ResultUtils.createErrorResult(code, msg, data);
  }

  protected IResult createResult(Object data)
  {
    return ResultUtils.createResult(data);
  }

  protected IResult createResult()
  {
    return ResultUtils.createResult(null);
  }

  protected Map<String, Object> create4Result(IResult result)
  {
    if (result.isSuccess()) {
      return ResultUtils.success(result.getData());
    } else {
      return ResultUtils.fail(result.getCode(), result.getMsg());
    }
  }

  private String getUrlAndParamString(HttpServletRequest request)
  {
    StringBuilder sb = new StringBuilder(request.getRequestURI());
    if (StringUtils.isNotBlank(request.getQueryString())) {
      sb.append("GET:").append(request.getQueryString());
    }

    return sb.toString();
  }

}
