package com.djt.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.text.MessageFormat;

public final class EnhancedStringUtils extends StringUtils
{

  private static final int STRING_BUILDER_INIT_SIZE = 300;

  private EnhancedStringUtils()
  {

  }

  public static String toString(Object object)
  {
    return ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public static String formatMsg(String msg, Object... arguments)
  {
    return MessageFormat.format(msg, arguments);
  }

  /**
   * 构建缓存Key
   *
   * @param params 需要联合的所有值
   * @throws IllegalArgumentException if parameters are empty
   */
  public static String joinStringBySeperator(String seperator, Object... params)
  {
    if (params == null || params.length == 0) {
      throw new IllegalArgumentException("input parameters are null");
    }

    StringBuilder builder = new StringBuilder(STRING_BUILDER_INIT_SIZE);
    builder.append(params[0]);
    for (int i = 1; i < params.length; i++) {
      builder.append(seperator);
      builder.append(params[i]);
    }
    return builder.toString();
  }

  /**
   * 构建缓存Key
   *
   * @param params 需要联合的所有值
   * @throws IllegalArgumentException if parameters are empty
   */
  public static String joinString(Object... params)
  {
    if (params == null || params.length == 0) {
      throw new IllegalArgumentException("input parameters are null");
    }

    StringBuilder builder = new StringBuilder(STRING_BUILDER_INIT_SIZE);
    builder.append(params[0]);
    for (int i = 1; i < params.length; i++) {
      builder.append(params[i]);
    }
    return builder.toString();
  }
}
