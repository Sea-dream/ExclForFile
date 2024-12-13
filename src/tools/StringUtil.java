package tools;

import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by HuangBote on 2016/9/26.
 */
public class StringUtil {

    /**
     * insert 表操作时，产生UUID
     *
     * @return 新UUID
     */
    public static String GetTableUUID_new() {
        Random ra = new Random();
        String s = "";
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s += ra.nextFloat() * Math.pow(10, 8);
        s = s.replace(".", "");
        String s1 = "";
        if (s.length() > 8) {
            s1 = s.substring(0, 8);
        }
        String s2 = "";
        if (s.length() > 16) {
            s2 = s.substring(8, 16);
        }
        String s3 = "";
        if (s.length() > 24) {
            s3 = s.substring(16, 24);
        }
        //--规则：年+月+日+时+分+秒+毫秒+24位随机数-------------------
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");
        LocalDateTime date = LocalDateTime.now();
        return date.format(f) + "-" + s1 + "-" + s2 + "-" + s3;
    }

    /**
     * 获取UUID字符串
     *
     * @return UUID
     */
    public static String GetUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取盐值，长度为16的字节数组
     *
     * @return 盐值
     */
    public static byte[] getSalt() {
        return getSalt(16);
    }

    /**
     * 获取盐值，长度为length 的字节数组
     *
     * @param length 长度
     * @return 盐值
     */
    public static byte[] getSalt(int length)  {
        try {
            Random random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[length];
            random.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 判断对象是否为空，包括NULL、空字符串""
     *
     * @param str 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object str) {
        if (str instanceof String) {
            return "".equals(((String) str).trim());
        }
        return str == null;
    }

    /**
     * 判断字符串是否为指定位数的整数
     *
     * @param str  字符串
     * @param bits 位数
     * @return true 表示是 bits 位数整数字符串
     */
    public static boolean isIntegerNumber(String str, int bits) {
        Pattern pattern = Pattern.compile(String.format("[1-9][0-9]{%d}", bits - 1));
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 是否为数字
     *
     * @param cs 数字字符串
     * @return true表示是数字字符串，false表示非数字字符串
     */
    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();
            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 判断字符串是否为六位数整数
     *
     * @param str 字符串
     * @return true 表示是6位数整数字符串
     */
    public static boolean isSixFigures(String str) {
        return isIntegerNumber(str, 6);
    }

    public static boolean isFiveFigures(String str) {
        return isIntegerNumber(str, 5);
    }

    public static String DatePickerToString(String str) {
        String monthStr = str.substring(4, 7);
        if ("Jan".equals(monthStr)) {
            monthStr = "01";
        }
        if ("Feb".equals(monthStr)) {
            monthStr = "02";
        }
        if ("Mar".equals(monthStr)) {
            monthStr = "03";
        }
        if ("Apr".equals(monthStr)) {
            monthStr = "04";
        }
        if ("May".equals(monthStr)) {
            monthStr = "05";
        }
        if ("Jun".equals(monthStr)) {
            monthStr = "06";
        }
        if ("Jul".equals(monthStr)) {
            monthStr = "07";
        }
        if ("Aug".equals(monthStr)) {
            monthStr = "08";
        }
        if ("Sep".equals(monthStr)) {
            monthStr = "09";
        }
        if ("Oct".equals(monthStr)) {
            monthStr = "10";
        }
        if ("Nov".equals(monthStr)) {
            monthStr = "11";
        }
        if ("Dec".equals(monthStr)) {
            monthStr = "12";
        }
        return String.format("%s%s%s%s%s", str.substring(11, 15), "-", monthStr, "-", str.substring(8, 10));
    }

    /**
     * 解析形式为： ‘,xxx1,,xxx2,xxx3,...’的字符串，转换为字符串列表，过滤空串，不去重
     *
     * @param str ‘,xxx1,,xxx2,xxx3,...’的字符串
     * @return 返回有效字符串列表集合
     */
    public static List<String> filterEmptyToList(String str) {
        if (isEmpty(str)) {
            return new ArrayList<>(0);
        }
        return Arrays.stream(str.trim().split("[,]+")).filter(item -> !isEmpty(item)).collect(Collectors.toList());
    }

    /**
     * 解析形式为： ‘,xxx1,,xxx2,xxx3,...’的字符串，转换为Integer列表，过滤空串，不去重
     *
     * @param str ‘,xxx1,,xxx2,xxx3,...’的字符串
     * @return 返回有效Integer列表集合
     */
    public static List<Integer> filterEmptyToIntegerList(String str) {
        if (isEmpty(str)) {
            return new ArrayList<>(0);
        }
        return Arrays.stream(str.trim().split("[,]+"))
                .filter(item -> !isEmpty(item) && !"null".equals(item))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * 解析形式为： ‘,xxx1,,xxx2,xxx3,...’的字符串，转换为列表，过滤空串
     *
     * @param str ‘,xxx1,,xxx2,xxx3,...’的字符串
     * @return 返回有效字符串数组
     */
    public static String[] filterEmptyToArray(String str) {
        List<String> list = filterEmptyToList(str);
        return list.toArray(new String[0]);
    }

    /**
     * 解析形式为： ‘,xxx1,,xxx2,xxx3,...’的字符串 列表，转换为字符串列表，过滤空串，并去重
     *
     * @param strList ‘,xxx1,,xxx2,xxx3,...’的字符串列表
     * @return 返回有效字符串列表集合
     */
    public static List<String> filterStringEmptyToList(List<String> strList) {
        List<String> newStrList = Lists.newArrayList();
        strList.forEach(item -> newStrList.addAll(filterEmptyToList(item)));
        return newStrList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 解析形式为： ‘,xxx1,,xxx2,xxx3,...’的字符串 列表，转换为Integer列表，过滤空串，并去重
     *
     * @param strList ‘,xxx1,,xxx2,xxx3,...’的字符串列表
     * @return 返回有效Integer列表集合
     */
    public static List<Integer> filterStringEmptyToIntegerList(List<String> strList) {
        List<Integer> newStrList = Lists.newArrayList();
        strList.forEach(item -> newStrList.addAll(filterEmptyToIntegerList(item)));
        return newStrList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 判断字符串是否含有中文符号
     *
     * @param str 字符串
     * @return 是否
     */
    public static boolean hasChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (char item : str.toCharArray()) {
            if (item >= 0x4E00 && item <= 0x9FA5) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换NULL字符串为空字符串
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String convertNullString(String str) {
        Optional<String> s = Optional.ofNullable(str);
        return s.orElse("");
    }

    /**
     * 字符串提取数字
     *
     * @param str 字符串
     * @return 数字
     */
    public static String checkNum(String str) {
        StringBuilder builder = new StringBuilder();
        String regEx = "(\\d+(\\.\\d+)?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            builder.append(m.group());
        }
        return builder.toString();
    }

    /**
     * 检查指定的String既不为null，长度也不为0。
     *
     * @param str 字符串
     * @return true 表示 既不为null，长度也不为0
     */
    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 修剪字符串中所有出现的指定前导字符。
     *
     * @param str              待处理字符串
     * @param leadingCharacter 指定的前导字符
     * @return 去除前导字符之后的字符串
     */
    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(str);

            while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
                sb.deleteCharAt(0);
            }

            return sb.toString();
        }
    }

    /**
     * 修剪字符串中所有出现的指定尾随字符
     *
     * @param str               待处理字符串
     * @param trailingCharacter 指定的尾随字符
     * @return 去除尾随字符之后的字符串
     */
    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (!hasLength(str)) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(str);

            while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();
        }
    }

    /**
     * 去除字符串前后的所有指定字符
     *
     * @param str       待处理字符串
     * @param character 指定字符
     * @return 去除字符之后的字符串
     */
    public static String trimCharacter(String str, char character) {
        if (!hasLength(str)) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(str);

            while (sb.length() > 0 && sb.charAt(0) == character) {
                sb.deleteCharAt(0);
            }
            while (sb.length() > 0 && sb.charAt(sb.length() - 1) == character) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();
        }
    }

    /**
     * 判断字符串是否由单个重复字符组成
     * @param str
     * @return
     */
    public static Boolean isRepeatString(String str) {
        if (StringUtils.isEmpty(str) || str.length() <= 0) {
            return false;
        }
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != str.charAt(0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成整数验证码字符串
     * @param length 验证码字符长度
     * @return 整数字符串
     */
    public static String generatorCaptcha(int length) {
        String integerLetter = "1234567890";
        char[] charArray = integerLetter.toCharArray();
        String result = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charArray.length);
            result += charArray[index];
        }
        return result;
    }

    /**
     * 生成指定个数【count】的有序UUID（前十八位代表时间的字符串相同）
     * 使用场景：相近时间生成多个连续的流程结点时使用，保证这些结点UUID字符串大小顺序跟其生成顺序一致
     *
     * @param count    生成UUID个数（范围：0 < count <= 10）
     * @return  指定个数的有序UUID列表
     */
    public static List<String> BatchGenerateUUID(int count) {
        if (count <= 0 || count > 10) {
            return null;
        }
        Random ra = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 7; ++i) {
            s.append(ra.nextFloat() * Math.pow(10, 8));
        }
        s = new StringBuilder(s.toString().replace(".", ""));
        String s1 = "";
        if (s.length() > 7) {
            s1 = s.substring(0, 7);
        }
        String s2 = "";
        if (s.length() > 15) {
            s2 = s.substring(7, 15);
        }
        String s3 = "";
        if (s.length() > 23) {
            s3 = s.substring(15, 23);
        }
        //--规则：年+月+日+时+分+秒+毫秒+1位顺序数+23位随机数-------------------
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS"));
        List<String> uuidList = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            uuidList.add(timeStr + "-" + i + s1 + "-" + s2 + "-" + s3);
        }
        return uuidList;
    }

    /**
     * 字符串按当前编码的字节数进行截取（不同编码下字节数可能不同）
     * 例如：UTF8下，一个汉字为3个字节，一个英文字符为1个字节
     * @param str
     * @param maxBytes
     * @return
     */
    public static String substringWithMaxBytes(String str, int maxBytes) {
        if (str.getBytes(StandardCharsets.UTF_8).length <= maxBytes) {
            return str;
        }

        int l = 0, r = str.length();
        int m = str.length(), currLength;
        while (l <= r) {
            m = (l + r) >>> 1;
            currLength = str.substring(0, m).getBytes(StandardCharsets.UTF_8).length;
            if (currLength == maxBytes) {
                return str.substring(0, m);
            } else if (currLength > maxBytes) {
                r = m - 1;
            } else {
                l = m + 1;
            }
        }
        return str.substring(0, m);
    }

    /**
     * 移除字符串中的html标签
     * @param htmlStr
     * @return
     */
    public static String removeHtmlTag(String htmlStr) {
        if (StringUtils.isEmpty(htmlStr)) {
            return htmlStr;
        }
        // html的正则表达式
        String pattern="<[^>]+>";
        // 控制正则表达式的匹配行为的参数。让表达式忽略大小写进行匹配
        Pattern compile = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(htmlStr);
        return matcher.replaceAll("");
    }

    /**
     * 创建num位的随机数字
     * @param num 大于0
     * @return
     */
    public static String createFigure(int num) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            Random random = new Random();
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成流水号，规则：prefix + 年月日时分秒毫秒 + 6位随机数
     * @param prefix
     * @return
     */
    public static String createSerialNumber(String prefix) {
        return createSerialNumber(prefix, 6);
    }

    /**
     * 生成流水号，规则：prefix + 年月日时分秒毫秒 + num位随机数
     * @param prefix
     * @param num
     * @return
     */
    public static String createSerialNumber(String prefix, int num) {
        String randomCode = StringUtil.createFigure(num);
        DateFormat df = new SimpleDateFormat("yyMMddhhmmssSSS");
        Date date = new Date();
        return String.format("%s%s%s", prefix, df.format(date), randomCode);
    }

    public static void main(String[] args) {
        String uuid = StringUtil.GetTableUUID_new();
        System.out.println("uuid: " + uuid);
    }

}
