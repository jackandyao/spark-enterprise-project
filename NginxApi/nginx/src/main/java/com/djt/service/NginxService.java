package com.djt.service;

import com.djt.common.HBaseClient;
import com.djt.model.UrlPvModel;
import com.djt.model.UrlUvModel;
import com.djt.utils.MyLogger;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NginxService
{
  private static final MyLogger LOG = MyLogger.getLogger(NginxService.class);

  @Autowired
  public HBaseClient hBaseClient;

  public List<UrlPvModel> getUrlPv(String bizType, String url, String day) throws Exception
  {
    List<UrlPvModel> resList = new ArrayList();

    Table table = hBaseClient.getTable("mon_url_pvuv_" + day.substring(0,4));
    String rowKey = bizType + ":" + day + ":" + url;

    Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rowKey)));
    Filter filter2 = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("pv")));

    List<Filter> filterList = new ArrayList();
    filterList.add(filter1);
    filterList.add(filter2);

    Scan scan = new Scan();
    scan.setFilter(new FilterList(FilterList.Operator.MUST_PASS_ALL, filterList));

    ResultScanner scanner = table.getScanner(scan);

    for (Result res : scanner) {
      for (Cell cell : res.rawCells()) {
        String minute = new String(CellUtil.cloneQualifier(cell));
        long pv = Bytes.toLong(CellUtil.cloneValue(cell));

        UrlPvModel model = new UrlPvModel();
        model.setMinute(minute);
        model.setPv(pv);

        resList.add(model);
      }
    }

    return resList;
  }

  public List<UrlUvModel> getUrlUv(String bizType, String url, String day) throws Exception
  {
    List<UrlUvModel> resList = new ArrayList();

    Table table = hBaseClient.getTable("mon_url_pvuv_" + day.substring(0,4));
    String rowKey = bizType + ":" + day + ":" + url;

    Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rowKey)));
    Filter filter2 = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("uv")));

    List<Filter> filterList = new ArrayList();
    filterList.add(filter1);
    filterList.add(filter2);

    Scan scan = new Scan();
    scan.setFilter(new FilterList(FilterList.Operator.MUST_PASS_ALL, filterList));

    ResultScanner scanner = table.getScanner(scan);

    for (Result res : scanner) {
      for (Cell cell : res.rawCells()) {
        String minute = new String(CellUtil.cloneQualifier(cell));
        long uv = Bytes.toLong(CellUtil.cloneValue(cell));

        UrlUvModel model = new UrlUvModel();
        model.setMinute(minute);
        model.setUv(uv);

        resList.add(model);
      }
    }

    return resList;
  }
}
