package com.djt.model;

public class PackageTimeLenModel implements Comparable<PackageTimeLenModel>
{
  private String packageName;
  private long timeLen;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public long getTimeLen() {
    return timeLen;
  }

  public void setTimeLen(long timeLen) {
    this.timeLen = timeLen;
  }

  @Override
  public int compareTo(PackageTimeLenModel o)
  {
    if (timeLen == o.getTimeLen()) {
      return 0;
    } else if (timeLen > o.getTimeLen()) {
      return -1;
    } else {
      return 1;
    }
  }
}
