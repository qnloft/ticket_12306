package org.ticket.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class StringUtil {
	
	private static final Byte DefaultValue_Byte = 0;
	private static final Short DefaultValue_Short = 0;
	private static final Integer DefaultValue_Integer = 0;
	private static final Long DefaultValue_Long = 0L;
	private static final Float DefaultValue_Float = 0f;
	private static final Double DefaultValue_Double = 0d;
	private static final Boolean DefaultValue_Boolean = false;
	
	private static final String[] urlReplacements = new String[]{
	    "%", "%25", "$", "%24", "&", "%26", "+", "%2B",
	    ",", "%2C", "/", "%2F", ":", "%3A", ";", "%3B",
	    "=", "%3D", "?", "%3F", "@", "%40", " ", "%20",
	    "\"", "%22", "<", "%3C", ">", "%3E", "#", "%23",
	    "{", "%7B", "}", "%7D", "|", "%7C", "\\", "%5C",
	    "^", "%5E", "~", "%7E", "[", "%5B", "]", "%5D", 
	    "`", "%60"
	};
	/**
	 * 判断对象是否为空
	 * User:T.L
	 * Description:
	 * @param obj
	 * @return
	 *
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String && String.valueOf(obj).trim().equals("")) {
			return true;
		} 
		else if (obj instanceof Boolean && !((Boolean) obj)) {
			return true;
		} else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Map && ((Map) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
			return true;
		}
		return false;
	}
	
	/**
     * 功能描述：解析字符串为最小长度的数字类型。如果value保存的是0~255之间的数则该方法回返回byte类型，如果是256则返回short类型。
     */
    public static Number parseNumber(final String value, final Number... defaultValue) {
        try {
            if (value.indexOf(".") != -1)
                try {
                    return Float.parseFloat(value);
                } catch (Exception e) {
                    return Double.parseDouble(value);
                }
            else
                try {
                    return Byte.parseByte(value);
                } catch (Exception e) {
                    try {
                        return Short.parseShort(value);
                    } catch (Exception e1) {
                        try {
                            return Integer.parseInt(value);
                        } catch (Exception e2) {
                            return Long.parseLong(value);
                        }
                    }
                }
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : 0;
        }
    }
    

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }	
    
	
	/**
	 * 功能描述：返回指定字节长度的字符串
	 * @param str String 字符串
	 * @param length int 指定长度
	 * @return String 返回的字符串
	 */
	public static String toLength(String str, int length) {
		if (str == null)
			return null;
		if (length <= 0)
			return "";
		try {
			if (str.getBytes("GBK").length <= length)
				return str;
		} catch (Exception e) {
		}
		StringBuffer buff = new StringBuffer();
		int index = 0;
		char c;
		length -= 3;
		while (length > 0) {
			c = str.charAt(index);
			if (c < 128) {
				length--;
			} else {
				length--;
				length--;
			}
			buff.append(c);
			index++;
		}
		buff.append("...");
		return buff.toString();
	}
	
	/**
	 * 功能描述：字符串截取函数
	 * 
	 * @param str String 要处理的字符串
	 * @param length int 需要显示的长度
	 * @param symbol String 用于表示省略的信息的字符，如“...”,“>>>”等
	 * @return String 返回处理后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String getLimitLengthString(String str, int length, String symbol) {
		assert str != null;
		assert length > 0;
		assert symbol != null;
		// 如果字符串的位数小于等于要截取的位数，附加上表示省略的信息的字符串后返回
		if (str.length() <= length)
			return str + symbol;
		// 从零开始，截取length个字符，附加上表示省略的信息的字符串后返回
		else {
			try {
				str = new String(str.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			char[] charArray = str.toCharArray();
			char[] charArrayDesc = new char[length];
			System.arraycopy(charArray, 0, charArrayDesc, 0, length);
			return new String(charArrayDesc) + symbol;
		}
	}
	
	/**
	 * 功能描述: 按字节长度截取字符串
	 * @param str 将要截取的字符串参数
	 * @param toCount 截取的字节长度
	 * @param more 字符串末尾补上的字符串
	 * @return 返回截取后的字符串
	 */
	public static String substring(String str, int toCount, String more) {
		int reInt = 0;
		String reStr = "";
        if (str == null) {
            return "";
        }
        char[] tempChar = str.toCharArray();
		for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
			String s1 = String.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}
        if (toCount == reInt || (toCount == reInt - 1)) {
            reStr += more;
        }
        return reStr;
	}
	
	/**
	 * 功能描述: 替换连接里面的特殊字符
	 * @param str 字符串
	 * @return 返回的字符串
	 */
	public static String urlEncode(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
	

	/**
	 * 功能描述：人民币转成大写
	 * @param str 数字字符串
	 * @return String 人民币转换成大写后的字符串
	 */
	public static String hangeToBig(String str) {
		double value;
		try {
			value = Double.parseDouble(str.trim());
		} catch (Exception e) {
			return null;
		}
		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		long midVal = (long) (value * 100); // 转化成整形
		String valStr = String.valueOf(midVal); // 转化成字符串
		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
		String rail = valStr.substring(valStr.length() - 2); // 取小数部分
		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		// 处理小数点后面的数
		if (rail.equals("00")) { // 如果小数部分为0
			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 角分转化出来
		}
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		char zero = '0'; // 标志'0'表示出现过0
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
			int idx = (chDig.length - i - 1) % 4; // 取段内位置
			int vidx = (chDig.length - i - 1) / 4; // 取段位置
			if (chDig[i] == '0') { // 如果当前字符是0
				zeroSerNum++; // 连续0次数递增
				if (zero == '0') { // 标志
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零
			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示
			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置加上万,亿
			}
		}
		if (prefix.length() > 0)
			prefix += '圆'; // 如果整数部分存在,则有圆
		return prefix + suffix; // 返回正确表示
	}
	
	/**
	 * 生成字母数字间隔的随机密码
	 * 
	 * @param pwd_len
	 *            生成的密码的总长度
	 * @return 密码的字符串
	 */
	public static String createPass(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 26;
		final int maxDig = 10;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z'};
		char[] dig = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，

			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1

			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
			i = Math.abs(r.nextInt(maxDig));
			if (i >= 0 && i < dig.length) {
				pwd.append(dig[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	/**
	 * 将字符串中特定模式的字符转换成objs中对应的值
	 * @param s		需要转换的字符串
	 * @param objs	转换所需的键值对集合
	 * @return		转换后的字符串
	 */
	public static String replace(String s, Object...objs) {
        if (objs == null || objs.length == 0) {
            return s;
        }
        if (s.indexOf("{}") == -1) {
            return s;
        }
		StringBuilder ret = new StringBuilder((int)(s.length() * 1.5));
		int cursor = 0;
		int index = 0;
		for(int start; (start = s.indexOf("{}", cursor)) != -1 ;) {
			ret.append(s.substring(cursor, start));
			if(index < objs.length){
				ret.append(objs[index]);				
			} else{
				ret.append("{}");				
			}
			cursor = start + 2;
			index++;
		}
		ret.append(s.substring(cursor, s.length()));
		return ret.toString();
	}

	/**
	 * 
	 * @description 
	 * 功能描述: 获取字符串的长度，Chinese
	 * @author 		  作		 者: 卢春梦
	 * @return	   返回类型: 
	 * @createdate   建立日期：2013-12-4下午3:22:59
	 */
	public static int getStringLength(String string) {
		if (StringUtil.isEmpty(string)) {
			return 0;
		} else {
			char[] chars = string.toCharArray();
			return chars.length;
		}
	}
	
	/**
	 * @Title: contents
	 * @Description: 标题过滤（判断标题中是否包含特定关键词）
	 * @author
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @date 2015-1-4 下午2:38:43
	 * @throws
	 */
	public static boolean contents(String titleString, String[] words) {
		if (!StringUtil.isEmpty(words)) {
			for (String string : words) {
				if (titleString.toLowerCase().contains(string.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @Title: contents
	 * @Description: 标题过滤 忽略大小写（判断标题中是否包含特定关键词）
	 * @author
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @date 2015-1-4 下午2:38:43
	 * @throws
	 */
	public static boolean contents(String titleString, String word) {
		if (!StringUtil.isEmpty(word)) {
			if (titleString.toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}


    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }

    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");

    /**
     * 将输入 1K-3K ，输出1
     * @return
     */
    public static Integer avgSalary(String salary) {
        if (StringUtil.isEmpty(salary)) {
            return 0;
        }
        Matcher m = pattern.matcher(salary);
        int count = 0;
        int sum = 0;
        while(m.find()) {
            if (isEmpty(m.group(count))) {
                break;
            }
            if (salary.contains("万")) {
                sum += Double.valueOf(m.group(count)) * 10;
            } else {
                sum += Double.valueOf(m.group(count));
            }
            count++;
        }

        return sum / 2;
    }

    /**
     * 返回url地址中的最后一个单词
     * 例子：https://jobs.51job.com/gaojiyingjian/ ，返回gaojiyingjian
     * @param str
     * @return
     */
    public static String getUrlLastWord(String str) {
        String result = null;
        int i = 0;
        while(i < 2) {
            int lastFirst = str.lastIndexOf('/');
            result = str.substring(lastFirst + 1);
            str = str.substring(0, lastFirst);
            i++;
        }
        return result;
    }

    /**
     * 截取指定后缀的url数据
     * @param url
     * @return
     */
    public static String getUrlLastId(String url) {
        String result = null;
        if (!StringUtil.isEmpty(url)) {
            int splitIndex = url.indexOf(".html");
            if (splitIndex > 0) {
                result = url.substring(url.lastIndexOf('/') + 1, splitIndex);
            }
        }
        return result;
    }

    /**
     * 根据正则表达式查询字符串
     * @param regex
     * @param source
     * @return
     */
    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * 生成uuid
     * @return
     */
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static void main(String[] args) {
//        String str1 = "Java";
//        String str2 = "测试工程师";
//
//        System.out.println(cnToUnicode(str1));
//        System.out.println(cnToUnicode(str2));
//        System.out.println(avgSalary("12K-15K"));
//        System.out.println(getUUID32());
    }
}
