package com.djt.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class SignUtils
{
  private SignUtils()
  {
  }

  private static final MyLogger LOG = MyLogger.getLogger(SignUtils.class);

  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String getFormattedText(byte[] bytes)
  {

    int len = bytes.length;

    StringBuilder buf = new StringBuilder(len * 2);

    // 把密文转换成十六进制的字符串形式
    for (int j = 0; j < len; j++) {
      buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
      buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
    }
    return buf.toString();
  }

  public static byte[] resolve(String txt)
  {
    byte[] by = new byte[txt.length() / 2];
    int index = 0;

    for (int i = 0; i < by.length; i++) {
      String subStr = txt.substring(index, index + 2);
      by[i] = (byte) Integer.parseInt(subStr, 16);
      index += 2;
    }
    return by;
  }

  public static byte[] gzipEncrypt(String formatted) throws Exception
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    GZIPOutputStream gos = null;
    try {
      gos = new GZIPOutputStream(os);
      gos.write(formatted.getBytes());
      gos.finish();
      gos.flush();
      return os.toByteArray();
    } finally {
      IOUtils.closeQuietly(gos);
      IOUtils.closeQuietly(os);
    }
  }

  /**
   * gzip解压
   */
  public static byte[] gzipDecrypt(byte[] b) throws Exception
  {
    ByteArrayInputStream bin = null;
    GZIPInputStream ginzip = null;
    ByteArrayOutputStream out = null;
    byte[] by = null;
    try {
      // 如果不是json格式，则进行解压
      bin = new ByteArrayInputStream(b);
      ginzip = new GZIPInputStream(bin);
      out = new ByteArrayOutputStream();
      IOUtils.copy(ginzip, out);
      by = out.toByteArray();
    } finally {
      IOUtils.closeQuietly(bin);
      IOUtils.closeQuietly(ginzip);
      IOUtils.closeQuietly(out);
    }
    return by;
  }

  public static String getRequestData(HttpServletRequest request)
  {
    byte[] nbytes = null;
    String st = null;
    try {
      nbytes = getRequestBytes(request);
      if (ArrayUtils.isEmpty(nbytes)) {
        return null;
      } else {
        // 16进制字符串
        return new String(nbytes, "utf-8");
      }
    } catch (Exception e) {
      LOG.warn("读取内容失败.content-encoding:"
          + request.getHeader("content-encoding") + "\n" + "nbytes:"
          + Arrays.toString(nbytes) + "\n" + "formated content:" + st);
    }
    return null;
  }

  public static byte[] getRequestBytes(HttpServletRequest request) throws Exception
  {
    byte[] by = IOUtils.toByteArray(request.getInputStream());
    // 如果内容是gzip格式
    if (by.length > 2 && 0x1F == by[0] && (byte) 0x8B == by[1]) {
      LOG.warn("Gzip request! Parse it manually!");
      // step1 进行gzip解压
      return SignUtils.gzipDecrypt(by);
    } else {
      return by;
    }
  }

  /*
   * public static void main(String[] args) throws UnsupportedEncodingException
   * { //客户端上传流程是这样的：post数据做aes加密，加密后的字节转化为16进制字符串，gzip压缩16进制字符串后post请求到数据上报服务器
   * //服务端流程：gzip解压上报post数据，16进制字符串转化为字节数组进行aes解密，最终得到post业务数据。
   * 
   * String content = "好aa";
   * 
   * { "bizinfo ": "adId=1&type=aa", "lcaid": "123456", "position": 1,
   * "signature": "69504e7477cf67c4b9c1805219958076de65030a", "ts": 1343214114 }
   * 
   * String password = "a0b3778b749bbc30";
   * System.out.println("--------------------加密--------------"); //post数据进行aes加密
   * byte[] bytes = AESSignature.encrypt(content, password); //格式化成16进制字符串
   * String formatted = getFormattedText(bytes); //gzip压缩 byte[] abytes =
   * gzipEncrypt(formatted);
   * System.out.println("--------------------解密--------------"); //gzip解压数据
   * byte[] nbytes = gzipDecrypt(abytes); String st = new String(nbytes,
   * Constants.CHAR_SET); //把16进制字符串转化成byte数组 byte[] b1 = resolve(st); //AES解密
   * System.out.println(new String(AESSignature.decrypt(b1, password))); }
   */
}
