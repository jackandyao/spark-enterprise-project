package com.djt.service;

import com.djt.common.HBaseClient;
import com.djt.model.PackageTimeLenModel;
import com.djt.utils.DateUtils;
import com.djt.utils.MyLogger;
import com.djt.utils.MyStringUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserBehaviorService
{
  private static final MyLogger LOG = MyLogger.getLogger(UserBehaviorService.class);

  @Autowired
  public HBaseClient hBaseClient;

  /*
  * 获取用户某天的应用使用统计
  * */
  public List<PackageTimeLenModel> getUserAppStat(String day, long userId) throws Exception
  {
    List<PackageTimeLenModel> resList = new ArrayList();

    Table table = hBaseClient.getTable("behavior_user_app_" + DateUtils.getCurrent(DateUtils.YYYYMM));
    String rowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + DateUtils.getCurrent(DateUtils.YYYYMMDD);

    Get get = new Get(Bytes.toBytes(rowKey));
    Result r = table.get(get);

    for (Cell cell : r.rawCells()) {
      String packageName = new String(CellUtil.cloneQualifier(cell));
      long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));

      PackageTimeLenModel model = new PackageTimeLenModel();
      model.setPackageName(packageName);
      model.setTimeLen(timeLen);

      resList.add(model);
    }

    Collections.sort(resList);
    return resList;
  }

  /*
  * 获取用户某段时间的应用使用统计
  * */
  public List<PackageTimeLenModel> getUserAppStat(String beginDay, String endDay, long userId)
  {
    LOG.info("getUserAppStat begin,beginDay=" + beginDay + ",endDay=" + endDay);
    Map<String, Long> timeLenMap = new HashMap();

    List<String> monthList = getMonthListByRange(beginDay.substring(0, 6), endDay.substring(0, 6));

    for (String month : monthList) {
      String hbaseTable = "behavior_user_app_" + month;
      String startRowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + beginDay;
      String endRowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + DateUtils.getDateBeforeOrAfter(DateUtils.YYYYMMDD, endDay, 1, DateUtils.YYYYMMDD);

      Table table = null;

      ResultScanner scanner = null;
      try {
        table = hBaseClient.getTable(hbaseTable);

        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));

        Filter filter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("timeLen")));
        scan.setFilter(filter);

        scanner = table.getScanner(scan);

        for (Result res : scanner) {
          for (Cell cell : res.rawCells()) {
            String packageName = new String(CellUtil.cloneQualifier(cell));
            long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));

            Long curTimeLen = timeLenMap.get(packageName);

            if (curTimeLen == null) {
              timeLenMap.put(packageName, timeLen);
            } else {
              timeLenMap.put(packageName, curTimeLen + timeLen);
            }
          }
        }
      } catch (Exception e) {
        LOG.error("getUserAppStat error,hbaseTable=" + hbaseTable, e);
      } finally {
        if (scanner != null) {
          scanner.close();
        }

        if (table != null) {
          try {
            table.close();
          } catch (Exception ex) {
            LOG.error("getUserAppStat error,close table " + hbaseTable, ex);
          }
        }
      }
    }

    List<PackageTimeLenModel> sortList = new ArrayList();
    Iterator<Map.Entry<String, Long>> it = timeLenMap.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<String, Long> entry = it.next();
      String packageName = entry.getKey();
      long timeLen = entry.getValue() == null ? 0 : entry.getValue();
      timeLen = timeLen == 0 ? 1 : timeLen;

      PackageTimeLenModel model = new PackageTimeLenModel();
      model.setPackageName(packageName);
      model.setTimeLen(timeLen);

      sortList.add(model);
    }

    Collections.sort(sortList);

    return sortList;
  }

  /*
  * 获取某天的使用统计
  * */
  public List<Map<String, Object>> getHourTotalStat(String day, long userId) {
    List<Map<String, Object>> resList = new ArrayList();

    Table table = null;

    try {
      table = hBaseClient.getTable("behavior_user_hour_time_" + day.substring(0, 6));
      String rowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + day;

      Get get = new Get(Bytes.toBytes(rowKey));
      Result r = table.get(get);

      for (Cell cell : r.rawCells()) {
        String hour = new String(CellUtil.cloneQualifier(cell));
        long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));

        Map<String, Object> map = new HashMap();
        map.put("dateType", "hour");
        map.put("dateFlag", hour + ":00");
        map.put("timeLen", timeLen);

        resList.add(map);
      }
    } catch (Exception e) {
      LOG.error("getUserDayTimeFromHbase error", e);
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (Exception ex) {
          LOG.error("close hbasetable error", ex);
        }
      }
    }

    return resList;
  }

  /*
  * 获取一段时间的使用统计
  * */
  public List<Map<String, Object>> getDayTotalStat(String beginDay, String endDay, long userId)
  {
    LOG.info("getDayTotalStat:begin...");
    List<Map<String, Object>> list = new ArrayList();

    List<String> dayList = getDayListByRange(beginDay, endDay);

    Map<String, Long> dayTimeList = getUserDayTime(userId, beginDay, endDay);

    for (String day : dayList) {
      Long timeLenLong = dayTimeList.get(day);
      long timeLen = timeLenLong == null? 0 : timeLenLong.longValue();

      Map<String, Object> map = new HashMap();
      map.put("dateFlag", day);
      map.put("dateType", "day");
      map.put("timeLen", timeLen);

      list.add(map);
    }

    return list;
  }

  public Map<String, Long> getUserDayTime(long userId, String beginDay, String endDay)
  {
    String beginMonth = beginDay.substring(0, 6);
    String endMonth = endDay.substring(0, 6);

    Map<String, Long> resultMap = new HashMap();

    //获取月份列表
    List<String> monthList = getMonthListByRange(beginMonth, endMonth);

    String rowKey = MyStringUtil.getFixedLengthStr(userId + "", 10);

    for (String month : monthList) {
      String hbaseTable = "behavior_user_day_time_" + month;

      Table table = null;

      try {
        table = hBaseClient.getTable(hbaseTable);

        Get get = new Get(Bytes.toBytes(rowKey));
        Result r = table.get(get);

        for (Cell cell : r.rawCells()) {
          String onlyDay = new String(CellUtil.cloneQualifier(cell));
          long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));
          //String day = DateUtils.getDateFormatFromDay(DateUtils.YYYYMMDD, month + onlyDay, DateUtils.YYYY_MM_DD);
          resultMap.put(month + onlyDay, timeLen);
        }
      } catch (Exception e) {
        LOG.error("getUserDayTimeFromHbase error,hbaseTable=" + hbaseTable + ",rowKey=" + rowKey, e);
      } finally {
        if (table != null) {
          try {
            table.close();
          } catch (Exception ex) {
            LOG.error("close hbasetable=" + hbaseTable + "error", ex);
          }
        }
      }
    }

    return resultMap;
  }

  /*
  * 获取某个应用玩机小时趋势
  * */
  public List<Map<String, Object>> getHourPackageStat(String packageName, String day, long userId)
  {
    List<Map<String, Object>> resList = new ArrayList();

    Table table = null;

    try {
      table = hBaseClient.getTable("behavior_user_hour_app_time_" + day.substring(0, 6));
      String rowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + day + ":" + packageName;

      Get get = new Get(Bytes.toBytes(rowKey));
      Result r = table.get(get);

      for (Cell cell : r.rawCells()) {
        String hour = new String(CellUtil.cloneQualifier(cell));
        long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));

        Map<String, Object> map = new HashMap();
        map.put("dateType", "hour");
        map.put("dateFlag", hour + ":00");
        map.put("timeLen", timeLen);

        resList.add(map);
      }
    } catch (Exception e) {
      LOG.error("getHourPackageStat error", e);
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (Exception ex) {
          LOG.error("close hbasetable error", ex);
        }
      }
    }

    return resList;
  }

  /*
  * 获取某个应用玩机天趋势
  * */
  public List<Map<String, Object>> getPackageDayStat(String packageName, String beginDay, String endDay, long userId)
  {
    List<Map<String, Object>> list = new ArrayList();

    Map<String, Long> dayTimeList = getUserPackageDayTime(userId, packageName, beginDay, endDay);

    List<String> dayList = getDayListByRange(beginDay, endDay);

    for (String day : dayList) {
      Long timeLenLong = dayTimeList.get(day);
      long timeLen = timeLenLong == null? 0 : timeLenLong.longValue();

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("dateFlag", day);
      map.put("dateType", "day");
      map.put("timeLen", timeLen);

      list.add(map);
    }

    return list;
  }

  /*
  * 从hbase获取用户某个应用某月每天的玩机时长
  * */
  public Map<String, Long> getUserPackageDayTime(long userId, String packageName, String beginDay, String endDay)
  {
    String beginMonth = beginDay.substring(0, 6);
    String endMonth = endDay.substring(0, 6);

    Map<String, Long> resultMap = new HashMap();

    //获取月份列表
    List<String> monthList = getMonthListByRange(beginMonth, endMonth);

    String rowKey = MyStringUtil.getFixedLengthStr(userId + "", 10) + ":" + packageName;

    for (String month : monthList) {
      String hbaseTable = "behavior_user_day_app_time_" + month;

      Table table = null;

      try {
        table = hBaseClient.getTable(hbaseTable);

        Get get = new Get(Bytes.toBytes(rowKey));
        Result r = table.get(get);

        for (Cell cell : r.rawCells()) {
          String onlyDay = new String(CellUtil.cloneQualifier(cell));
          long timeLen = Bytes.toLong(CellUtil.cloneValue(cell));
          resultMap.put(month + onlyDay, timeLen);
        }
      } catch (Exception e) {
        LOG.error("getUserDayTimeFromHbase error,hbaseTable=" + hbaseTable + ",rowKey=" + rowKey, e);
      } finally {
        if (table != null) {
          try {
            table.close();
          } catch (Exception ex) {
            LOG.error("close hbasetable=" + hbaseTable + "error", ex);
          }
        }
      }
    }

    return resultMap;
  }

  /*
  * 获取天列表
  * */
  public List<String> getDayListByRange(String beginDay, String endDay)
  {
    List<String> dayList = new ArrayList();

    dayList.add(beginDay);

    for (String d = beginDay; d.compareTo(endDay) < 0;) {
      String ds = DateUtils.getDateBeforeOrAfter(DateUtils.YYYYMMDD, d, 1, DateUtils.YYYYMMDD);
      dayList.add(ds);
      d = ds;
    }

    return dayList;
  }

  /*
  * 获取月列表
  * */
  public List<String> getMonthListByRange(String beginMonth, String endMonth)
  {
    List<String> list = new ArrayList();

    list.add(beginMonth);

    for (String m = beginMonth; m.compareTo(endMonth) < 0;) {
      String ms = DateUtils.getMonthBeforeOrAfter(DateUtils.YYYYMM, m, 1, DateUtils.YYYYMM);
      list.add(ms);
      m = ms;
    }

    return list;
  }

  /*
  * 获取全天的小时列表
  * */
  public List<String> getDayHourList()
  {
    String curHour = DateUtils.getCurrent(DateUtils.HH);
    List<String> hourList = new ArrayList();
    for (int i = 0; i < 24; i++) {
      String hour = i < 10 ? "0" + i : i + "";

      if (hour.compareTo(curHour) <= 0) {
        hourList.add(hour);
      }
    }
    return hourList;
  }
}
