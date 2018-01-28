package com.djt.controller;

import com.djt.model.PackageTimeLenModel;
import com.djt.result.IResult;
import com.djt.service.UserBehaviorService;
import com.djt.utils.DateUtils;
import com.djt.utils.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserBehaviorController extends BaseController {
  private static final MyLogger LOG = MyLogger.getLogger(UserBehaviorController.class);

  @Autowired
  private UserBehaviorService userBehaviorService;

  /*
  * 获某周期内app使用时长列表(可用于饼图和列表展示)
  * */
  @RequestMapping(value = "/periodAppList", method = RequestMethod.GET)
  @ResponseBody
  public IResult periodAppList(HttpServletRequest request,
                               @RequestParam(value = "dateType", required = true) String dateType,
                               @RequestParam(value = "userId", required = true) long userId) throws Exception
  {
    if (!checkDateType(dateType)) {
      return createResult();
    }

    String currentDay = DateUtils.getCurrent(DateUtils.YYYYMMDD);
    List<PackageTimeLenModel> list = null;

    if ("day".equalsIgnoreCase(dateType)) {
      list = userBehaviorService.getUserAppStat(currentDay, userId);
    } else {
      int n = "week".equalsIgnoreCase(dateType) ? -6 : -30;
      String beginDay = DateUtils.getDateBeforeOrAfter(DateUtils.YYYYMMDD, currentDay, n, DateUtils.YYYYMMDD);
      list = userBehaviorService.getUserAppStat(beginDay, currentDay, userId);
    }

    return createResult(list);
  }

  /*
  * 玩机时长总趋势
  * */
  @RequestMapping(value = "/totalStat", method = RequestMethod.GET)
  @ResponseBody
  public IResult totalStat(HttpServletRequest request,
                           @RequestParam(value = "dateType", required = true) String dateType,
                           @RequestParam(value = "userId", required = true) long userId)
  {
    if (!checkDateType(dateType)) {
      return createResult();
    }

    Map<String, Object> result = new HashMap();
    String currentDay = DateUtils.getCurrent(DateUtils.YYYYMMDD);

    if ("day".equalsIgnoreCase(dateType)) {
      List<Map<String, Object>> statList = userBehaviorService.getHourTotalStat(currentDay, userId);
      result.put("stat", statList);
    } else {
      int n = "week".equalsIgnoreCase(dateType) ? -6 : -30;
      String beginDay = DateUtils.getDateBeforeOrAfter(DateUtils.YYYYMMDD, currentDay, n, DateUtils.YYYYMMDD);
      List<Map<String, Object>> statList = userBehaviorService.getDayTotalStat(beginDay, currentDay, userId);
      result.put("stat", statList);
    }

    return createResult(result);
  }

  /*
  * 某个应用的使用趋势
  * */
  @RequestMapping(value = "/appStat", method = RequestMethod.GET)
  @ResponseBody
  public IResult appStat(HttpServletRequest request,
                         @RequestParam(value = "dateType", required = true) String dateType,
                         @RequestParam(value = "packageName", required = true) String packageName,
                         @RequestParam(value = "userId", required = true) long userId)
  {
    if (!checkDateType(dateType)) {
      return createResult();
    }

    Map<String, Object> result = new HashMap();
    String currentDay = DateUtils.getCurrent(DateUtils.YYYYMMDD);

    if ("day".equalsIgnoreCase(dateType)) {
      List<Map<String, Object>> statList = userBehaviorService.getHourPackageStat(packageName, currentDay, userId);
      result.put("stat", statList);
    } else {
      int n = "week".equalsIgnoreCase(dateType) ? -6 : -30;
      String beginDay = DateUtils.getDateBeforeOrAfter(DateUtils.YYYYMMDD, currentDay, n, DateUtils.YYYYMMDD);
      List<Map<String, Object>> statList = userBehaviorService.getPackageDayStat(packageName, beginDay, currentDay, userId);
      result.put("stat", statList);
    }

    return createResult(result);
  }

  private boolean checkDateType(String dateType)
  {
    if ("day".equalsIgnoreCase(dateType) || "week".equalsIgnoreCase(dateType) || "month".equalsIgnoreCase(dateType)) {
      return true;
    }

    return false;
  }
}
