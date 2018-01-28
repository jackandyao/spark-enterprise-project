package com.djt.flume.iterceptor;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.HashMap;
import java.util.List;

public class NginxLogIterceptor implements Interceptor
{
  @Override
  public void initialize()
  {
  }

  @Override
  public Event intercept(Event event)
  {
    if (event == null) {
      return null;
    }

    try {
      String body = new String(event.getBody());

      if (event.getHeaders() == null) {
        event.setHeaders(new HashMap<String, String>());
      }

      String bizType = event.getHeaders().get("bizType");

      if (StringUtils.isEmpty(bizType)) {
        bizType = "none";
      }

      event.getHeaders().put("key", body);

      body = bizType + " " + body;
      event.setBody(body.getBytes());

      return event;
    } catch (Exception e) {
      e.printStackTrace();
      return event;
    }
  }

  @Override
  public List<Event> intercept(List<Event> events)
  {
    List<Event> out = Lists.newArrayList();
    for (Event event : events) {
      Event outEvent = intercept(event);
      if (outEvent != null) { out.add(outEvent); }
    }
    return out;
  }

  @Override
  public void close()
  {
  }

  public static class NginxLogIterceptorBuilder implements Interceptor.Builder
  {
    @Override
    public void configure(Context context)
    {
    }

    public Interceptor build()
    {
      return new NginxLogIterceptor();
    }
  }
}
