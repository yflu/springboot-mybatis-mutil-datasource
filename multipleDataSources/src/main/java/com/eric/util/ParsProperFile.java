package com.eric.util;

import java.util.ResourceBundle;

/**
 * 读取配置文件
 * <p>
 * Created by eric on 16-3-16.
 */
public class ParsProperFile {

  private static ResourceBundle businessProcBud = ResourceBundle.getBundle("business");
  private static ResourceBundle appProcBud = ResourceBundle.getBundle("application");

  public static String getBusinessProp(String key) {
    return businessProcBud.getString(key);
  }

  public static Long getBusinessPropToLong(String key) {
    return Long.parseLong(businessProcBud.getString(key));
  }

  public static Integer getBusinessPropToInt(String key) {
    return Integer.parseInt(businessProcBud.getString(key));
  }

  public static String getAppProp(String key) {
    return appProcBud.getString(key);
  }
}
