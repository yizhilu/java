package com.hc.wechat.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 时间工具类
 * 
 * @author cxj
 *
 */
public class DateUtils {
  private static final Log LOG = LogFactory.getLog(DateUtils.class);

  /**
   * yyy-MM-dd
   */
  public static final String SHORT_DATE_FORMATE_ONE = "yyyy-MM-dd";

  /**
   * yyy/MM/dd
   */
  public static final String SHORT_DATE_FORMATE_TWO = "yyyy/MM/dd";

  /**
   * yyyMMdd
   */
  public static final String SHORT_DATE_FORMATE_FOUR = "yyyyMMdd";

  /**
   * yyyMM
   */
  public static final String SHORT_DATE_FORMATE_THREE = "yyyyMM";
  /**
   * yyyy.MM.dd
   */
  public static final String SHORT_DATE_FORMATE_FIVE = "yyyy.MM.dd";

  /**
   * 2009-05-19 12:47:28 yyyy-MM-dd HH:mm:ss
   */
  public static final String DEFAULT_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";

  public static final String SHORT_DATE_FORMAT_STR = "yyyy-MM-dd";
  public static final String LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
  private static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat(SHORT_DATE_FORMAT_STR);
  public static final DateFormat LONG_DATE_FORMAT = new SimpleDateFormat(LONG_DATE_FORMAT_STR);
  private static final String EARLY_TIME = "00:00:00";
  private static final String LATE_TIME = "23:59:59";

  private static final String MONTH_EARLY_TIME = "01 00:00:00";

  /**
   * 使用预设Format格式化Date成字符串
   * 
   * @return String
   */
  public static String format(Date date) {
    return date == null ? "" : format(date, DEFAULT_DATE_FORMATE);
  }

  /**
   * 使用参数Format格式化Date成字符串
   * 
   * @return String
   */
  public static String format(Date date, String pattern) {
    return date == null ? "" : new SimpleDateFormat(pattern).format(date);
  }

  /**
   * 字符串解析成 java.sql.Date 日期
   * 
   * @author fengyuan
   * @param shortDate
   * @param format
   * @return
   */
  public static java.sql.Date parserShortDate(String shortDate, String format) {
    DateFormat dateFormate = new SimpleDateFormat(format);
    try {
      Date date = dateFormate.parse(shortDate);
      return new java.sql.Date(date.getTime());
    } catch (ParseException e) {
      LOG.error("parser java.sql.Date error", e);
      return null;
    }
  }

  /**
   * 字符串解析成日期
   * 
   * @author fengyuan
   * @param dateStr
   * @param format
   * @return
   */
  public static java.util.Date parserDate(String dateStr, String format) {
    DateFormat dateFormate = new SimpleDateFormat(format);
    try {
      Date date = dateFormate.parse(dateStr);
      return new java.util.Date(date.getTime());
    } catch (ParseException e) {
      throw new RuntimeException();
    }
  }

  /**
   * 增加时间的秒数
   * 
   * @param date 要增加的日期
   * @param second 增加的时间（以秒为单位）
   * @return 增加时间后的日期
   */

  public static Date addSecond(Date date, int second) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.SECOND, second);
    return cal.getTime();
  }

  /**
   * 根据TimeUnit增加指定日期的的时间
   * 
   * @author fengyuan
   * @param date 要增加的日期
   * @param timeUnit 增加的日历字段（只能取 DAYS 到 MILLISECONDS 之间的枚举，否则报错）
   * @param value 增加的值(当值为负数时表示减少)
   * @return
   */
  public static Date add(Date date, TimeUnit timeUnit, int value) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int field = 0;
    if (timeUnit == TimeUnit.YEAR)
      field = Calendar.YEAR;
    else if (timeUnit == TimeUnit.DAYS)
      field = Calendar.DAY_OF_YEAR;
    else if (timeUnit == TimeUnit.HOURS)
      field = Calendar.HOUR_OF_DAY;
    else if (timeUnit == TimeUnit.MINUTES)
      field = Calendar.MINUTE;
    else if (timeUnit == TimeUnit.SECONDS)
      field = Calendar.SECOND;
    else if (timeUnit == TimeUnit.MILLISECONDS)
      field = Calendar.MILLISECOND;
    else
      throw new RuntimeException("timeUnit error");
    cal.add(field, value);
    return cal.getTime();
  }

  /**
   * 根据TimeUnit清除指定的日历字段
   * 
   * @author fengyuan
   * @param date 要清除的日期
   * @param timeUnit 清除的日历字段（只能取 DAYS 到 MILLISECONDS 之间的枚举，否则报错）
   * @return
   */
  public static Date clear(Date date, TimeUnit timeUnit) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int field = 0;
    if (timeUnit == TimeUnit.YEAR)
      field = Calendar.YEAR;
    else if (timeUnit == TimeUnit.DAYS)
      field = Calendar.DAY_OF_YEAR;
    else if (timeUnit == TimeUnit.HOURS)
      field = Calendar.HOUR_OF_DAY;
    else if (timeUnit == TimeUnit.MINUTES)
      field = Calendar.MINUTE;
    else if (timeUnit == TimeUnit.SECONDS)
      field = Calendar.SECOND;
    else if (timeUnit == TimeUnit.MILLISECONDS)
      field = Calendar.MILLISECOND;
    else
      throw new RuntimeException("timeUnit error");
    cal.clear(field);
    return cal.getTime();
  }

  /**
   * <br>
   * 传入的日期减去天数后返回的日期</br>
   * 
   * @param date 被减日期
   * @param day 减去的天数
   * @return 返回减去指定天数后的日期
   */
  public static Date subtractDay(Date date, int day) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    dayOfMonth -= day;
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    return cal.getTime();
  }

  /**
   * <br>
   * 第一个日期减去第二个日期后得到的天数</br> <br>
   * 如果减去的后的天数有不满足一整天的，则不计入天数内</br>
   * 
   * @param date 被减日期
   * @param day 减去的日期
   * @return 返回减去后的天数
   */
  public static long subtractDay(Date date, Date other) {
    return subtractSecond(date, other) / (24 * 60 * 60);
  }

  public static long subtractSecond(Date date, Date other) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    long dateTimeInMillis = calendar.getTimeInMillis();
    Calendar otherCalendar = Calendar.getInstance();
    otherCalendar.setTime(other);
    long otherTimeInMillis = otherCalendar.getTimeInMillis();
    return (dateTimeInMillis - otherTimeInMillis) / (1000);
  }

  /**
   * 根据指定天数获得距离当前日期之前的日期(java.sql.date)
   * 
   * @param day 距离当前日期之前的天数
   * @return
   */
  public static java.sql.Date getBeforTodayDate(int day) {
    Date dateTime = new Date();
    dateTime = DateUtils.subtractDay(dateTime, day);
    return new java.sql.Date(dateTime.getTime());
  }

  /**
   * 字符串解析成 java.sql.Time 时间
   * 
   * @author fengyuan
   * @param timeStr
   * @param timeFormat
   * @return
   */
  public static java.sql.Time parserTime(String timeStr, String timeFormat) {
    DateFormat dateFormate = new SimpleDateFormat(timeFormat);
    try {
      Date date = dateFormate.parse(timeStr);
      return new java.sql.Time(date.getTime());
    } catch (ParseException e) {
      LOG.error("parser java.sql.Time error", e);
      return null;
    }
  }

  public static enum TimeUnit {
    YEAR, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS;
  }

  /**
   * 得到某个日期在这一天中时间最早的日期对象
   * 
   * @param date
   * @return
   * @throws ParseException
   */
  public static Date getEarlyInTheDay(Date date) {
    String dateString = SHORT_DATE_FORMAT.format(date) + " " + EARLY_TIME;
    try {
      return LONG_DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("parser date error.", e);
    }
  }

  /**
   * 得到某个日期在这一天中时间最早的日期对象（yyyy-MM-dd）
   * 
   * @param date
   * @return
   * @throws ParseException
   */
  public static Date getEarlyInTheDay2(Date date) {
    String dateString = SHORT_DATE_FORMAT.format(date);
    try {
      return SHORT_DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("parser date error.", e);
    }
  }

  /**
   * 得到某个日期在这一天中时间最晚的日期对象
   * 
   * @param date
   * @return
   * @throws ParseException
   */
  public static Date getLateInTheDay(Date date) {
    String dateString = SHORT_DATE_FORMAT.format(date) + " " + LATE_TIME;
    try {
      return LONG_DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("parser date error.", e);
    }
  }

  /**
   * 得到某月中最早的时间
   * 
   * @param date
   * @return
   */
  public static Date getEarlyInTheMonth(Date date) {
    String dateString = SHORT_DATE_FORMAT.format(date);
    dateString = dateString.substring(0, dateString.length() - 2);
    dateString += MONTH_EARLY_TIME;
    try {
      return LONG_DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("parser date error.", e);
    }
  }

  /**
   * 根据传入的两个日期获得日期的时间差<br/>
   * 如果距离时间小于1分钟则返回的时间为秒<br/>
   * 如果距离时间小于1小时则返回的时间为分<br/>
   * 如果距离时间小于1天则返回的时间为小时<br/>
   * 其它则去天数
   * 
   * @author fengyuan
   * @param date
   * @return
   */
  public static TimeOutEnum getDistanceTimeOut(Date beforeDate, Date aferDate) {
    long beforeMilliseconds = beforeDate.getTime();
    long aferMilliseconds = aferDate.getTime();
    long distanceTime = aferMilliseconds - beforeMilliseconds;
    distanceTime = Math.abs(distanceTime) / 1000;
    long time = 0;
    TimeOutEnum timeOut = null;
    if (distanceTime - 60 < 0) {
      time = distanceTime;
      timeOut = TimeOutEnum.SECONDS;
    } else if (distanceTime - (60 * 60) < 0) {
      time = distanceTime / 60;
      timeOut = TimeOutEnum.MINUTES;
    } else if (distanceTime - (60 * 60 * 24) < 0) {
      time = distanceTime / (60 * 60);
      timeOut = TimeOutEnum.HOURS;
    } else {
      time = distanceTime / (60 * 60 * 24);
      timeOut = TimeOutEnum.DAYS;
    }
    time = (aferMilliseconds - beforeMilliseconds > 0 ? time : 0 - time);
    timeOut.setDistanceTimeOut(time);
    return timeOut;
  }

  public static enum TimeOutEnum {
    DAYS, HOURS, MINUTES, SECONDS;
    private long distanceTimeOut;

    public long getDistanceTimeOut() {
      return this.distanceTimeOut;
    }

    private void setDistanceTimeOut(long distanceTimeOut) {
      this.distanceTimeOut = distanceTimeOut;
    }

    public String getTimeOutString() {
      String dateString = "";
      switch (this) {
        case SECONDS:
          dateString = this.distanceTimeOut + "秒钟前";
          break;
        case MINUTES:
          dateString = this.distanceTimeOut + "分钟前";
          break;
        case HOURS:
          dateString = this.distanceTimeOut + "小时前";
          break;
        case DAYS:
          dateString = this.distanceTimeOut + "天前";
          break;
        default:
          break;
      }
      return dateString;
    }
  }

  /**
   * 页面显示调用
   * 
   * @param createDate
   * @return
   */
  public static String getTimeOutString(Date createDate) {
    Date beforeDate = new Date(createDate.getTime());
    Date nowDate = new Date();
    if (subtractDay(nowDate, beforeDate) > 3) {
      return DateUtils.format(beforeDate, SHORT_DATE_FORMAT_STR);
    }
    return getDistanceTimeOut(beforeDate, nowDate).getTimeOutString();
  }

  /**
   * 根据年龄计算出生日
   * 
   * @author fengyuan
   * @param age
   * @return
   */
  public static java.sql.Date getBirthday(int age) {
    Date date = new Date();
    date = add(date, TimeUnit.YEAR, -age);
    return new java.sql.Date(date.getTime());
  }

  /**
   * 根据生日获取年龄
   * 
   * @param birthDay 生日
   * @return
   */
  public static int getAge(Date birthDay) {
    Calendar cal = Calendar.getInstance();// 获取当前系统时间
    if (cal.before(birthDay)) {// 如果出生日期大于当前时间，则抛出异常
      throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
    }
    int yearNow = cal.get(Calendar.YEAR);// 取出系统当前时间的年、月、日部分
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthDay);// 将日期设置为出生日期
    int yearBirth = cal.get(Calendar.YEAR);// 取出出生日期的年、月、日部分
    int monthBirth = cal.get(Calendar.MONTH);
    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
    int age = yearNow - yearBirth;// 当前年份与出生年份相减，初步计算年龄
    if (monthNow <= monthBirth) {// 当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
      if (monthNow == monthBirth) {// 如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
        if (dayOfMonthNow < dayOfMonthBirth) age--;
      } else {
        age--;
      }
    }
    return age;
  }

  /**
   * 获得2个时间差(分钟)
   * 
   * @param end
   * @param begin
   * @return
   */
  public static long betweenMin(Date end, Date begin) {
    long between = 0;
    try {
      between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    long day = between / (24 * 60 * 60 * 1000);
    long hour = (between / (60 * 60 * 1000) - day * 24);
    long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);

    return min;
  }

  /**
   * 获得上月第一天
   * 
   * @return
   */
  public static Date getLastMonthFirstDate() {
    GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
    c.add(Calendar.MONTH, -1);
    c.set(Calendar.DAY_OF_MONTH, 1);
    return getEarlyInTheDay(c.getTime());
  }

  /**
   * 获得上月最后一天
   * 
   * @return
   */
  public static Date getLastMonthEndDate() {
    GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH, 1);
    c.set(Calendar.DAY_OF_MONTH, -1);
    return getLateInTheDay(c.getTime());
  }

  /**
   * 获取起止时间区间内的所有时间集合
   * 
   * @param beginDateStr
   * @param endDateStr
   * @param format
   * @return
   */
  public static List<String> getDateRange(String beginDateStr, String endDateStr, String format) {
    List<String> dateRange = new ArrayList<String>();
    Validate.notBlank(beginDateStr, "beginDateStr不能为空");
    Validate.notBlank(endDateStr, "endDateStr不能为空");
    Date beginDate = parserDate(beginDateStr, SHORT_DATE_FORMATE_ONE);
    Date endDate = parserDate(endDateStr, SHORT_DATE_FORMATE_ONE);
    Validate.isTrue(beginDate.before(endDate), "beginDateStr不能大于等于endDateStr");
    // 把开始日期加入到结果集中
    dateRange.add(format(beginDate, format));
    // compareDate 用于比较的日期 第一次 把 beginDate赋值给它
    Date compareDate = beginDate;
    // 再拿比较日期和 结束日期比较
    while (compareDate.before(endDate)) {
      // 把比较日期加一天
      compareDate = add(compareDate, TimeUnit.DAYS, 1);
      dateRange.add(format(compareDate, format));
    }
    return dateRange;
  }
}
