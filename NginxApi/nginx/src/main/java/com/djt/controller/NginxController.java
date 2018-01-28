package com.djt.controller;

import com.djt.model.UrlPvModel;
import com.djt.model.UrlUvModel;
import com.djt.result.IResult;
import com.djt.service.NginxService;
import com.djt.utils.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NginxController extends BaseController {
  private static final MyLogger LOG = MyLogger.getLogger(NginxController.class);

  @Autowired
  private NginxService nginxService;

  @RequestMapping(value = "/pv", method = RequestMethod.GET)
  @ResponseBody
  public IResult pv(HttpServletRequest request,
                               @RequestParam(value = "bizType", required = true) String bizType,
                               @RequestParam(value = "url", required = true) String url,
                               @RequestParam(value = "day", required = true) String day) throws Exception
  {
    List<UrlPvModel> list = nginxService.getUrlPv(bizType, url, day);
    return createResult(list);
  }

  @RequestMapping(value = "/uv", method = RequestMethod.GET)
  @ResponseBody
  public IResult uv(HttpServletRequest request,
                    @RequestParam(value = "bizType", required = true) String bizType,
                    @RequestParam(value = "url", required = true) String url,
                    @RequestParam(value = "day", required = true) String day) throws Exception
  {
    List<UrlUvModel> list = nginxService.getUrlUv(bizType, url, day);
    return createResult(list);
  }
}
