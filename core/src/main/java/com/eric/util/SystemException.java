package com.eric.util;

/**
 * 系统异常处理类
 */
public class SystemException extends RuntimeException {
  private static final long serialVersionUID = 2293897900102583273L;

  private String key;

  /**
   * 默认构造器
   */
  public SystemException() {
    super();
  }

  /**
   * 不带key的构造器，视为系统异常
   *
   * @param message
   */
  public SystemException(String message) {
    super(message);
  }

  /**
   * 具体信息的异常
   *
   * @param key     错误码
   * @param message 错误消息
   */
  public SystemException(String key, String message) {
    super(message);
    this.key = key;
  }

  public SystemException(String message, Throwable cause) {
    super(message, cause);
  }

  public SystemException(Throwable cause) {
    super(cause);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

}
