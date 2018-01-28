package com.djt.spark.registrator;

import com.djt.spark.key.BizMinuteUrlIpKey;
import com.djt.spark.key.BizMinuteUrlKey;
import com.djt.spark.model.NginxLogModel;
import com.esotericsoftware.kryo.Kryo;
import org.apache.spark.serializer.KryoRegistrator;
@SuppressWarnings("all")
public class MyKryoRegistrator implements KryoRegistrator
{
  @Override
  public void registerClasses(Kryo kryo)
  {
    kryo.register(NginxLogModel.class);
    kryo.register(BizMinuteUrlIpKey.class);
    kryo.register(BizMinuteUrlKey.class);
  }
}
