package com.djt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public final class MyLogger
{
  public static final String COMMON_ERROR = "COMMON-ERROR";

  private static final Logger COMMON_ERROR_LOG = LoggerFactory.getLogger(COMMON_ERROR);

  private final Logger logger;

  private MyLogger(String name)
  {
    logger = LoggerFactory.getLogger(name);
  }

  private MyLogger(Class<?> clz)
  {
    logger = LoggerFactory.getLogger(clz);
  }

  public static MyLogger getLogger(Class<?> clz)
  {
    return new MyLogger(clz);
  }

  public static MyLogger getLogger(String loggerName)
  {
    return new MyLogger(loggerName);
  }

  public void trace(String format, Object... arguments)
  {
    if (logger.isTraceEnabled()) {
      logger.trace(argumentMessage(format, arguments));
    }
  }

  public void trace(String format, Throwable throwable, Object... arguments)
  {
    if (logger.isTraceEnabled()) {
      logger.trace(argumentMessage(format, arguments), throwable);
    }
  }

  public void debug(String format, Object... arguments)
  {
    if (logger.isDebugEnabled()) {
      logger.debug(argumentMessage(format, arguments));
    }
  }

  public void debug(String format, Throwable throwable, Object... arguments)
  {
    if (logger.isDebugEnabled()) {
      logger.debug(argumentMessage(format, arguments), throwable);
    }
  }

  public void info(String format, Object... arguments)
  {
    if (logger.isInfoEnabled()) {
      logger.info(argumentMessage(format, arguments));
    }
  }

  public void info(String format, Throwable throwable, Object... arguments)
  {
    if (logger.isInfoEnabled()) {
      logger.info(argumentMessage(format, arguments), throwable);
    }
  }

  public void warn(String format, Object... arguments)
  {
    if (logger.isWarnEnabled()) {
      logger.warn(argumentMessage(format, arguments));
    }
  }

  public void warn(String format, Throwable throwable, Object... arguments)
  {
    if (logger.isWarnEnabled()) {
      logger.warn(argumentMessage(format, arguments), throwable);
    }
  }

  public void error(String format, Throwable throwable, Object... arguments)
  {
    COMMON_ERROR_LOG.error(argumentMessage(format, arguments), throwable);
  }

  public void error(String format, Object... arguments)
  {
    logger.error(argumentMessage(format, arguments));
  }

  public void logErrorAndWarnMsg(String format, Throwable throwable, Object... arguments)
  {
    String msg = argumentMessage(format, arguments);
    COMMON_ERROR_LOG.error(msg, throwable);
    warn(msg);
  }

  private String argumentMessage(String format, Object... arguments)
  {
    if (arguments != null && arguments.length > 0) {
      FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
      return ft.getMessage();
    } else {
      return format;
    }
  }
}
