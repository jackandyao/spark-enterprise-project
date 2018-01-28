package com.djt.controller.convert;

import com.djt.utils.JSONUtil;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import java.io.IOException;
import java.nio.charset.Charset;

public class JacksonHttpMessageConverter extends MappingJacksonHttpMessageConverter
{

  public JacksonHttpMessageConverter()
  {
  }

  /**
   * @see MappingJacksonHttpMessageConverter#writeInternal(Object, HttpOutputMessage)
   */
  @Override
  protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException,
    HttpMessageNotWritableException
  {
    MediaType contentType = outputMessage.getHeaders().getContentType();
    Charset charset = null;
    if (contentType != null && contentType.getCharSet() != null) {
      charset = contentType.getCharSet();
    }

    if (charset == null) {
      charset = DEFAULT_CHARSET;
    }

    try {
      String fromObject = JSONUtil.fromObject(object);
      byte[] bytes = fromObject.getBytes(charset);
      outputMessage.getHeaders().setContentLength(Long.valueOf(bytes.length).longValue());
      outputMessage.getBody().write(bytes);
      outputMessage.getBody().flush();
    } catch (JsonProcessingException ex) {
      throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
    }
  }

}
