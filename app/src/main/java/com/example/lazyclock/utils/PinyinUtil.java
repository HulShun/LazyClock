package com.example.lazyclock.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by Administrator on 2016/1/19.
 */
public class PinyinUtil {
    private static PinyinUtil util;


    public static PinyinUtil getInstence() {
        if (util == null) {
            synchronized (FileUtil.class) {
                if (util == null) {
                    util = new PinyinUtil();
                }
            }
        }
        return util;
    }


    public String getPinyin(String src) {
        if (src != null && !src.trim().equalsIgnoreCase("")) {
            char[] srcChar;
            srcChar = src.toCharArray();
            //汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

            //输出设置，大小写，音标方式等
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            String[][] pingyinArray = new String[src.length()][];
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < srcChar.length; i++) {
                try {
                    pingyinArray[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
                    for (int m = 0; m < pingyinArray[i].length; m++) {
                        sb.append(pingyinArray[i][m]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();

        }
        return null;
    }
}
