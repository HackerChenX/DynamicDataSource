package com.yizhilu.os.dynamic.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;*/

public class StringUtils {
	
	/**
	 * 中文数字小写
	 */
	public static final String[] CHINESE_DIGITAL = {"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五"};
	/**
	 * 字符串连接时的分隔符
	 * <p>
	 * 		该分隔符用于{@link #toString(Collection)} 和 {@link #toString(Collection, String)}方法。
	 * </p>
	 */
	public static final String DEFAULT_SEPARATOR = ",";
	
	/**
	 * 检查当前字符串是否为空
	 * <p>
	 * 		如果字符串为null，或者长度为0，都被归为空。
	 * </p>
	 * @param id 要检查的字符串
	 * @return 返回结果，true表示不为空，false表示为空
	 */
	public static boolean isEmpty(Object id) {
		if(id == null || id.toString().trim().length() == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(Object obj){
		return !isEmpty(obj);
	}
	/**
	 * 检查当前字符串是否为空
	 * <p>
	 * 		如果字符串为null，或者调用trim()后长度为0，都被归为空。
	 * </p>
	 * @param str 要检查的字符串
	 * @return 检查结果，true 为空，false不为空
	 */
	public static boolean isTrimEmpty(String str){
		if(str == null || str.trim().length() == 0){
			return true;
		}
		return false;
	}

	/**
	 * 字符串数组转化为 字符串
	 * @param array
	 * @return
	 */
	public static String arrayToString(Object[] array){
		if(array == null) return "";
		StringBuffer result = new StringBuffer();
		for(Object item : array){
			result.append(item).append(",");
		}
		if(result.length() >0 ){
			return result.substring(0, result.length()-1);
		}
		return "";
	}
	/**
	 * 字符串数组转化为 字符串 答案截断
	 * @param array
	 * @return
	 */
	public static String arrayToStringAnswer(String[] array){
		if(array == null) return "";
		StringBuffer result = new StringBuffer();
		for(Object item : array){
			result.append(item);
		}
		return result.toString();
	}

    /**
     * Map转String 以&分隔
     * @param params
     * @return
     */
	public static String mapToString(Map<String,String> params){
		if(ObjectUtils.isNull(params)){
			return "";
		}
        return params.entrySet().stream().filter(e -> StringUtils.isNotEmpty(e.getKey()) && StringUtils.isNotEmpty(e.getValue())).map(e -> {
			try {
				return e.getKey() + "=" + URLEncoder.encode(e.getValue(),"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				return "";
			}
		}).collect(Collectors.joining("&"));
	}

	/**
	 * 判断当前字符串是否是由数字组成
	 * @param str 要检查的字符串
	 * @return 结果
	 */
	public static boolean isDigit(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return false;
		}
		return Pattern.matches("^[0-9]+(.[0-9]{1,2})?$", str);
	}

	/**
	 * 判断当前字符串是否是由整数组成
	 * @param str 要检查的字符串
	 * @return 结果
	 */
	public static boolean isNumber(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return false;
		}
		return Pattern.matches("^\\d+$", str);
	}
	
	/**
	 * 判断当前字符串是否表示数字区间
	 * @param str 要检查的字符串
	 * @return 结果
	 */
	public static boolean isDigitRange(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return false;
		}
		return Pattern.matches("^\\d+-\\d+$", str);
	}
	/**
	 * 替换字符串中的字符,该方法用于velocity层，只替换第一次匹配
	 * @param str 被替换的原始字符串
	 * @param regex 替换的字符
	 * @param value 替换的值
	 * @return  替换结果
	 */
	public static String replace(String str,String regex,String value){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		return str.replace(regex, value);
	}
	
	/**
	 * 替换字符串中的字符,该方法用于velocity层，替换所有匹配
	 * @param str 被替换的原始字符串
	 * @param regex 替换的字符
	 * @param value 替换的值
	 * @return  替换结果
	 */
	public static String replaceAll(String str,String regex,String value){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		return str.replaceAll(regex, value);
	}

	/**
	 * 替换sql的部分特殊字符
	 * @param str
	 * @return
	 */
	public static String formatSql(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		//替换特殊字符串
		str = str.replaceAll("'","''");

		return str;
	}
	/**
	 * 提换html的部分特殊字符
	 * <p>
	 * 		只替换了&、<和>符号
	 * </p>
	 * @param str 要替换的字符串
	 * @return 替换结果
	 */
	public static String formatHtml(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		//替换特殊字符串
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}
	/**
	 * 替换HTML的全部特殊字符
	 * 可以防xss攻击
	 * <p>
	 * 		替换了&、<、>、"和空格
	 * </p>
	 * @param str 要替换的字符串
	 * @return 替换的结果
	 */
	public static String formatAllHtml(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		//替换特殊字符串
		str = str.replaceAll("&", "&amp;")
				 .replaceAll("<", "&lt;")
				 .replaceAll(">", "&gt;")
				 .replaceAll("\"", "&quot;")
				 .replaceAll(" ", "&nbsp;")
				 .replaceAll("/", "&#x2f");
		return str;
	}
	/**
	 * 将过长字符串进行截取，并在末尾追加描述符，如...
	 * @param str 要截取的字符串
	 * @param maxLength 最大长度
	 * @param replace 追加的字符串，如果是null，则默认为...
	 * @return 截取结果
	 */
	public static String cut(String str,int maxLength,String replace){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return str;
		}
		//检查replace是否存在
		if(replace == null){
			replace = "...";
		}
		//检查长度
		if(str.length() + replace.length() <= maxLength || maxLength < 1 || replace.length() > maxLength){
			return str;
		}
		//开始截取
		return str.substring(0, maxLength - replace.length()) + replace;
	}
	/**
	 * 将string 集合拼接成字符串，使用{@value #DEFAULT_SEPARATOR}分隔
	 * @param list 要处理的集合
	 * @return 处理结果
	 */
	public static String toString(Collection<?> list){
		return toString(list,null);
	}
	/**
	 * 将string 集合拼接成字符串，使用特定字符分隔
	 * @param list 要处理的集合
	 * @param separator 分隔符，如果为null，则默认使用{@value #DEFAULT_SEPARATOR}
	 * @return 处理结果
	 */
	public static String toString(Collection<?> list,String separator){
		if(separator == null){
			separator = DEFAULT_SEPARATOR;
		}
		//检查list是否存在
		if(list == null){
			return null;
		}
		StringBuffer rs = new StringBuffer();
		Iterator<?> it = list.iterator();
		Object next = null;
		while(it.hasNext()){
			next = it.next();
			if(next == null){
				continue;
			}
			rs.append(next.toString());
			//如果有下一个值，则添加分隔符
			if(it.hasNext()){
				rs.append(separator);
			}
		}
		return rs.toString();
	}
	
	/**
	 * 检查输入的字符串是否为查询条件 有[ 标识
	 * @param str
	 * @return
	 */
	public static boolean isQueryCondition(String str){
		//检查是否为空
		if(StringUtils.isTrimEmpty(str)){
			return false;
		}
		//检查是否为查询条件
		if(str.indexOf("[") !=-1){
			return true;
		}
		
		return false;
	}
	/**
	 * @Title	strToInt 
	 * @Description	将字符串数字转换成数字
	 * @param ojb
	 * @return Integer	
	 * @author liuqin
	 * @date 2012-12-6 下午1:14:50
	 * @throws
	 */
	public static Integer strToInt(Object ojb){
		if(isEmpty(ojb))return 0;
		try{return Integer.valueOf(ojb.toString());}
		catch(Exception e){return 0;}
	}
	
	/**
	 * @Title filterImgTag
	 * @Description 过滤字符串中的图片标签
	 * @param content
	 * @author chenjingxue
	 * @date 2012-12-17
	 * @return String
	 */
	public static String filterImgTag(String content){
	    return content.replaceAll("<img.*?>", "");
	}
	
	
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_special = "\\&[a-zA-Z]{1,10};";//定义特殊字符的正则表达式
	private static final String regEx_word = "<!.*?>[\\s\\S]*?<!.*?>";// 定义Word XML的正则表达式
    
    /**
     * 删除html 标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
    	if (StringUtils.isEmpty(htmlStr)) {
    		return "";
		}
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

		Pattern p_special= Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
		Matcher m_special = p_special.matcher(htmlStr);
		htmlStr = m_special.replaceAll(""); // 过滤特殊字符标签

        return htmlStr.trim(); // 返回文本字符串
    }

	/**
	 * 删除Word XML内容
	 * @param content
	 * @return
	 */
	public static String delWordXml(String content){
		if (StringUtils.isEmpty(content)) {
			return null;
		}
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(content);
		content = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(content);
		content = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_word, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(content);
		content = m_html.replaceAll(""); // 过滤特殊字符
		return content.trim(); // 返回文本字符串
	}

	/**
	 * 从HTML中提取纯文本
	 * @param content
	 * @return
	 */
	public static String  filterHtmlAndImgTag(String content){
		content = delWordXml(content);
		return filterImgTag(delHTMLTag(content));
	}

    /** 
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符） 
     *  
     * @param c 需要判断的字符 
     * @return 返回true,Ascill字符 
     */  
    public static boolean isLetter(char c) {  
        int k = 0x80;  
        return c / k == 0 ? true : false;  
    }  
  
    /** 
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1 
     *  
     * @param s 需要得到长度的字符串 
     * @return i得到的字符串长度 
     */  
    public static int length(String s) {  
        if (s == null)  
            return 0;  
        char[] c = s.toCharArray();  
        int len = 0;  
        for (int i = 0; i < c.length; i++) {  
            len++;  
            if (!isLetter(c[i])) {  
                len++;  
            }  
        }  
        return len;  
    }  
  
    /** 
     * 截取一段字符的长度,支持中文(中文占2个字符),如果数字不正好，则少取一个字符位 
     *  
     * @param  origin 原始字符串 
     * @param len 截取长度(一个汉字长度按2算的) 
     * @param c 后缀            
     * @return 返回的字符串 
     */  
    public static String substring(String origin, int len,String c) {  
        if (origin == null || origin.equals("") || len < 1)  
            return "";  
        byte[] strByte = new byte[len];  
        if (len > length(origin)) {  
            return origin;  
        }  
        try {  
            System.arraycopy(origin.getBytes("GBK"), 0, strByte, 0, len);  
            int count = 0;  
            for (int i = 0; i < len; i++) {  
                int value = (int) strByte[i];  
                if (value < 0) {  
                    count++;  
                }  
            }  
            if (count % 2 != 0) {  
                len = (len == 1) ? ++len : --len;  
            }  
            return new String(strByte, 0, len, "GBK")+c;  
        } catch (UnsupportedEncodingException e) {  
            throw new RuntimeException(e);  
        }  
    } 
    /**
     * 将数组已,号连接成字符串
     * @param obj 要连接的数组，如果是string则直接返回
     * @return
     */
    public static String join(Object obj){
    	if(obj == null){
    		return null;
    	}else if(obj instanceof String){
    		return obj.toString();
    	}else if(obj.getClass().isArray()){
    		StringBuffer s = new StringBuffer();
    		Object[] list = (Object[]) obj;
    		for(Object o : list){
    			s.append(o.toString() + ",");
    		}
    		if(s.length() > 0){
    			return s.deleteCharAt(s.length() - 1).toString();
    		}
    	}
    	return null;
    }
    
    /**
     * 功能:替换字符串中的\t\n以及前后空格
     * <p>作者文齐辉 2013-3-7 下午1:30:22
     * @param str
     * @return
     */
    public static String replaceTN(String str){
    	if(str==null)return str;
    	return str.replaceAll("\t|\n", "").trim();
    }
    
    /**
     * 处理富文本编辑器产生的文本<br/>
     * 将数据源中的特殊字符（以<开头 >结尾，或空格）全部截除
     * @param source 数据源
     * @return 结果
     */
    public static String trimHtml(String source){
    	if(StringUtils.isTrimEmpty(source)){
    		return null;
    	}
    	return source.replaceAll("[\\s]+", "").replaceAll("<[^>]*>","").trim();
    }
    
    public static String formatDigital(Integer digital){
    	return CHINESE_DIGITAL[digital-1];
    }
    
	/**
	 * 功能:数据精度格式化
	 * <p>作者文齐辉 2013-3-26 下午5:15:50
	 * @param o 只能为数字
	 * @return
	 */
	public static String formatNumber(Object o,Integer precision){
		if(o==null)return "";
		DecimalFormat df = null;
		try{
			switch (precision) {
			case 0:
				df = new DecimalFormat("#######"); 
				break;
			case 1:
				df = new DecimalFormat("#######.#"); 
				break;
			case 2:
				df = new DecimalFormat("#######.##"); 
				break;
			case 3:
				df = new DecimalFormat("#######.###"); 
				break;
			default:
				df = new DecimalFormat("#######.##");
				break;
			}
			return df.format(Double.parseDouble(o.toString())+0.00000001d);
		}catch(Exception e){
			return "0";
		}
	}
	
	public static Float formatNumber(Float number){
		if(number==null)return 0f;
		return number;
	}

	/**
	 * 替换String的a标签中的Href属性
	 * @param src 要替换的String
	 * @return 返回替换后的String
	 */
	public static String replaceTagHref(String src) {
		if (src.indexOf("kindeditor/plugins/emoticons/images") > 0) {
			return src;
		}
		try {
			String reg = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
			Pattern pattern = Pattern.compile(reg, 2);
			Matcher matcher = pattern.matcher(src);
			if (matcher.find()) {
				String ms = matcher.group();
				return src.replace(ms, new StringBuilder().append("<a class='c-blue fsize14' target='_blank' href='")
						.append(ms).append("'>").append(ms).append("</a>").toString());
			}
			return src;
		} catch (Exception e) {
		}
		return src;
	}

	/**
	 * 判断是否是合格的邮箱号
	 * @param email
	 * @return true合格，false不合格
	 */
	public static boolean isEmail(String email){
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		 return  Pattern.compile(check).matcher(email).matches();
	}

	/**
	 * 判断是否是合格的手机号
	 * @param mobile
	 * @return true合格，false不合格
	 */
	public static boolean isMobile(String mobile){
		String _check="^(13[0-9]|14[5,7]|15[^4]|17[0,3,6-8]|18[0-9])[0-9]{8}$";
		return Pattern.compile(_check).matcher(mobile).matches();
	}

	private static final Integer[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
	private static final String[] code = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

	/**
	 * 判断是否是合格的身份证号码
	 * @param idCard
	 * @return true合格，false不合格
	 */
	public static boolean isIdCard(String idCard) {
		//身份证正则验证
		if (ObjectUtils.isNull(idCard) || !idCard.matches("^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5]))\\d{4}(([1-9]\\d{3})((0[1-9]|1[0-2])(0[1-9]|1\\d|2[0-8])|(0[13-9]|1[0-2])(29|30)|(0[13578]|1[02])(31))|([1-9]\\d([13579][26]|[2468][048]|0[48])|([48]|[13579][26]|[2468][048])00)0229)\\d{3}[\\d|Xx]$")) {
			return false;
		}
		//身份证加强验证
        Integer sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(idCard.substring(i, i + 1)) * weight[i];
        }
		return idCard.substring(17).equalsIgnoreCase(code[sum % 11]);
	}
	
	public static String getLength(Object goodsName, int length) {
        if (goodsName == null) {
            return null;
        } else {
            String temp = String.valueOf(goodsName);
            if (temp.length() <= length) {
                return temp;
            } else {
                temp = temp.substring(0, length) + "...";
                return temp;
            }
        }
    }
	
	/**
     * 格式化日期
     * 
     * @param oldDate
     * @return
     */
    public static String getModelDate(Date oldDate) {
        // 判断为空
        if (ObjectUtils.isNotNull(oldDate)) {
            Date newDate = new Date();
            long second = (newDate.getTime() - oldDate.getTime()) / 1000;
            if (second <= 60) {// 0-60秒
                return second + "秒前";
            } else if (60 < second && second <= (60 * 60)) {// 1-60分钟
                second = second / 60;// 分钟数
                return second + "分钟前";
            } else if (60 * 60 < second && second <= (60 * 60 * 24)) {// 1-24小时
                second = second / 60 / 60;// 小时数
                return second + "小时前";
            } else if (60 * 60 * 24 < second && second <= (60 * 60 * 24 * 10)) {// 2-10天
                String formatDate = DateUtils.formatDate(oldDate, "HH:mm:ss");
                second = second / 60 / 60 / 24;// 天数
                return second + "天前 " + formatDate;
            } else {
                // 大于10天不符合条件按原格式返回
                return DateUtils.formatDate(oldDate, "yyyy-MM-dd HH:mm:ss");
            }
        } else {
            return "";
        }
    }
	
    /**
     * UUID
     */
    public static String createUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

	/**
	 * 生成指定长度的随机字符串
	 *
	 * @author liuqinggang
	 * @param strLength
	 * @return
	 */
	public static String getRandomString(int strLength) {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < strLength; i++) {
			int charInt;
			char c;
			if (random.nextBoolean()) {
				charInt = 48 + random.nextInt(10);
				c = (char) charInt;
				buffer.append(c);
				continue;
			}
			charInt = 65;
			if (random.nextBoolean())
				charInt = 65 + random.nextInt(26);
			else
				charInt = 97 + random.nextInt(26);
			if (charInt == 79)
				charInt = 111;
			c = (char) charInt;
			buffer.append(c);
		}

		return buffer.toString();
	}

	public static String getRandStr(int n) {
		Random random = new Random();
		String sRand = "";
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}

	/**
	 * 把字符串的头尾的一个字符去掉
	 * @param str
	 * @param tag
	 * @return
	 */
	public static String subHeadTailString(String str,String tag){
		if(isNotEmpty(str)){
			if(str.trim().endsWith(tag)){
				str = str.trim().substring(0,str.trim().length()-1);
			}
			if(str.trim().startsWith(tag)){
				str = str.trim().substring(1,str.trim().length());
			}
		}
		return str;
	}

	/**
	 * 替换四个字节的字符 😁
	 * @param content
	 * @return
	 */
	public static String removeFourChar(String content) {
		byte[] conbyte = content.getBytes();
		for (int i = 0; i < conbyte.length; i++) {
			if ((conbyte[i] & 0xF8) == 0xF0) {
				for (int j = 0; j < 4; j++) {
					conbyte[i+j]=0x30;
				}
				i += 3;
			}
		}
		content = new String(conbyte);
		return content.replaceAll("0000", " ");
	}

}
