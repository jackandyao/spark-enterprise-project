package com.djt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public final class MessageUtil
{

  private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

  private static Map<String, String> codeToMsg;
  public static final String NOT_EXIST_CODE = "NO-CODE-FOUND";
  private static final String ERROR_CODE = "Server-Error-Code-Mapping";

  private MessageUtil()
  {

  }

  /*
   * 通过code映射消息
   *
   */
  public static String getMessage(String code)
  {
    try {
      //第一次被动加载
      if (codeToMsg == null) {
        loadData();
      }

      //通过code返回msg
      String msg = codeToMsg.get(code);
      if (msg != null) {
        return msg;
      }

      //不存在code返回共同提示
      return NOT_EXIST_CODE;
    } catch (Exception e) {
      //ExceptionUtil.caught(e, "getMessage exception for the code=" + code);
    }

    return ERROR_CODE;
  }

  /*
   * 通过code映射消息
   * parms为可变变量
   */
  public static String getMessage(String code, Object... parms)
  {
    try {
      //第一次被动加载
      if (codeToMsg == null) {
        loadData();
      }

      //通过code返回msg
      String msg = codeToMsg.get(code);
      if (msg != null) {
        //格式化数据
        return String.format(msg, parms);
      }

      //不存在code返回共同提示
      return NOT_EXIST_CODE;
    } catch (Exception e) {
      //ExceptionUtil.caught(e, "getMessage exception for the code=" + code);
    }

    return ERROR_CODE;
  }

  //提供jsp页面手动加载，避免重启
  public static String loadData() throws Exception
  {
    InputStreamReader reader = null;
    String result = null;
    try {
      LOG.info("begin:code mapping to msg");
      //加载messages.properties文件
      reader = new InputStreamReader(MessageUtil.class.getResourceAsStream("/conf/messages.properties"), "utf-8");

      //装载Properties
      Properties p = new Properties();
      p.load(reader);

      //处理code对应Msg(被动加载到HashMap)
      handle(p);

      //jsp页面返回结果
      result = "success";

      LOG.info("end:code mapping to msg");
    } catch (Exception e) {
      LOG.warn("exception:code mapping to msg");

      result = "failed";
      //ExceptionUtil.caught(e, "load messages.properties exception.");
      //数据加载失败，立即让其停止
      throw new Exception("load messages.properties exception.", e);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
    return result;
  }

  /**
   * @param p
   * @throws Exception
   */
  private static void handle(Properties p) throws Exception
  {
    try {
      //codeToMsg已存在
      if (codeToMsg != null) {
        removeAll(codeToMsg);
      } else {
        codeToMsg = new HashMap<String, String>();
      }

      //加载配置文件类容
      Iterator<Entry<Object, Object>> it = p.entrySet().iterator();
      while (it.hasNext()) {
        Entry<Object, Object> entry = it.next();

        String key = entry.getKey().toString();
        String value = entry.getValue().toString();

        //放入codeToMsg中
        codeToMsg.put(key, value);
      }
    } catch (Exception e) {
      //ExceptionUtil.caught(e, "handle Properties For Code To Msg.");
      throw new Exception("handle Properties For Code To Msg.", e);
    }
  }

  /**
   * @param hashCodeToMsg
   */
  private static void removeAll(Map<String, String> hashCodeToMsg)
  {
    //先获取所有的Key
    Iterator<String> keys = hashCodeToMsg.keySet().iterator();
    List<String> keysAll = new ArrayList<String>();

    while (keys.hasNext()) {
      keysAll.add(keys.next().toString());
    }

    //删除所有的key对应的val
    int len = keysAll.size();
    for (int i = 0; i < len; i++) {
      codeToMsg.remove(keysAll.get(i));
    }
  }

}
