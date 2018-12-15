package com.eric.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.springframework.util.StringUtils;

/**
 * 拼音工具
 *
 * @author xiaomin.gu
 */
public class PinyinUtils {
  /**
   * 获取汉字串拼音首字母，英文字符不变
   *
   * @param chinese 汉字串
   * @return 汉语拼音首字母
   */
  public static String cn2FirstSpell(String chinese) {
    if (chinese == null || chinese == "")
      return chinese;

    StringBuffer pybf = new StringBuffer();
    char[] arr = chinese.toCharArray();
    HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    for (int i = 0; i < arr.length; i++) {
      if (isChinese(arr[i])) {
        try {
          String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i],
              defaultFormat);
          if (_t != null) {
            pybf.append(_t[0].charAt(0));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        pybf.append(arr[i]);
      }
    }
    return pybf.toString().replaceAll("\\W", "").trim();
  }

  /**
   * 获取汉字串拼音，英文字符不变
   *
   * @param chinese 汉字串
   * @return 汉语拼音
   */
  public static String cn2Spell(String chinese) {
    if (StringUtils.isEmpty(chinese))
      return chinese;

    StringBuffer pybf = new StringBuffer();
    char[] arr = chinese.toCharArray();
    HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    for (int i = 0; i < arr.length; i++) {
      if (isChinese(arr[i])) {
        try {
          pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i],
              defaultFormat)[0]);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        pybf.append(arr[i]);
      }
    }
    return pybf.toString();
  }

  /**
   * 根据Unicode编码判断中文汉字(不包含中文标点符号）
   *
   * @param c
   * @return
   */
  public static boolean isChinese(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B) {
      return true;
    }
    return false;
  }

  /**
   * 根据Unicode编码完美的判断中文汉字和符号
   *
   * @param c
   * @return
   */
  public static boolean isChineseOrSign(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
      return true;
    }
    return false;
  }
}
