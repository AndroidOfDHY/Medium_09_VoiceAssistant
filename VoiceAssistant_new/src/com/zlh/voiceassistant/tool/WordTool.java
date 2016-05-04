package com.zlh.voiceassistant.tool;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class WordTool {
	static{
		DBHelper dbhelper;
		
	}
	public static final int FUNCTION_SEARCH = 0;
	public static final int FUNCTION_RUN = 1;
	public static final int FUNCTION_PLAY = 2;
	public static final int FUNCTION_CALL = 3;
	public static final int FUNCTION_MESSAGES = 4;
	private static final String[][] keyList = {
			{ "搜索一下", "搜索", "搜一下", "查询", "查一下" },
			{ "打开一下", "打开", "运行一下", "运行", "启动一下", "启动" }, { "播放音乐","音乐播放", "听一下音乐", "听音乐" },
			{ "拨打", "呼叫", "call", "打电话给", "打电话到", "打电话", "打给" },
			{ "发短信给", "发短信","短信" } };
	private static final String[][] beforePrepositions = { { "将", "把" },
			{ "将", "把" }, {}, { "给" },{ "给" } };

	public static String FindSynonyms(String string, int functionId) {
		for (String word : keyList[functionId]) {
			if (string.indexOf(word) != -1)
				return word;
		}
		return null;
	}

	public static List<Object> FindAllSynonyms(String string) {
		String word;
		for (int i = 0; i < 5; i++) {
			if ((word = FindSynonyms(string, i)) != null) {
				List<Object> list = new ArrayList<Object>();
				list.add(i);
				list.add(word);
				return list;
			}
		}
		return null;
	}

	public static String parsingKey(String string, String key, int functionId) {
		string = string.replaceAll(key, "*");
		StringBuffer word = new StringBuffer(string);
		int i = word.indexOf("*");
		String num = null;
		if (word.length() - 1 == i) {
			for (String preposition : beforePrepositions[functionId]) {
				if (word.indexOf(preposition) != -1) {
					num = word.substring(word.indexOf(preposition) + 1, i);
					if (num.indexOf(",") != -1)
						num = null;
					break;
				}
			}
		} else if (word.charAt(i + 1) == '。' || word.charAt(i + 1) == '，'
				|| word.charAt(i + 1) == '？' || word.charAt(i + 1) == '！') {
			for (String preposition : beforePrepositions[functionId]) {
				int j = word.indexOf(preposition);
				if (j != -1 && j < i) {
					num = word.substring(word.indexOf(preposition) + 1, i);
					if (num.indexOf(",") != -1)
						num = null;
					break;
				}
			}
		} else {
			num = word.substring(i + 1, word.length());
			if (num.indexOf("。") != -1) {
				num = num.substring(0, num.indexOf("。"));
			} else if (num.indexOf("?") != -1) {
				num = num.substring(0, num.indexOf("?"));
			} else if (num.indexOf("!") != -1) {
				num = num.substring(0, num.indexOf("!"));
			} else if (num.indexOf("，") != -1) {
				num = num.substring(0, num.indexOf("，"));
			}
		}
		return num;
	}

	public static String getPinYin(String src) {
		if (src == null)
			return null;
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 += t2[0];
				} else
					t4 += Character.toString(t1[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
