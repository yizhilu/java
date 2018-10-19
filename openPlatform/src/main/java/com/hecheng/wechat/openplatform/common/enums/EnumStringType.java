/**
 * @(#)EnumType.java
 *
 *                   Copyright 2016 Vanda Inc. All rights reserved.
 */
package com.hecheng.wechat.openplatform.common.enums;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * hibernate 自定义枚举客户化映射类型<br/>
 * 参考配置：
 * 
 * @TypeDefs({@TypeDef(name = "status" , typeClass = EnumType. class , parameters = {
 * @Parameter(name = "class" , value = "demo.domain.one2one.testtwo.Order$StatusEnum" ),
 * @Parameter(name = "field" , value = "status" ),
 * @Parameter(name = "method" , value = "getStatusEnum" )}) })
 * @Type(type = "status" ) Parameter： class:枚举类名(必填) field:需要保存进数据库的字段名(选填
 *            默认为"value")，该字段必须为String类型 method:根据字段值获取枚举的静态方法名(选填 ,默认为"get") * @author hc
 *
 * @version 1.0,2017年8月8日
 */

public class EnumStringType implements UserType, ParameterizedType {
  private Class<? extends Enum<?>> enumClass;
  private Field field;
  private Method method;

  private static String CLASSNAME_KEY = "class";
  private static String FIELD_KEY = "field";
  private static String METHOD_KEY = "method";

  private static String DEFALUT_FIELD_VALUE = "value";
  private static String DEFALUT_METHOD_VALUE = "get";

  private String className;
  private String fieldName;
  private String methodName;

  @SuppressWarnings("unchecked")
  public void setParameterValues(Properties properties) {
    intitValue(properties);
    if (className == null || className.trim().equals(""))
      throw new HibernateException("Enum class not found");
    try {
      enumClass = ReflectHelper.classForName(className, this.getClass()).asSubclass(Enum.class);
    } catch (ClassNotFoundException e) {
      throw new HibernateException("Enum class not found : " + className, e);
    }
    try {
      field = enumClass.getDeclaredField(fieldName);
    } catch (Exception e) {
      throw new HibernateException("Enum field not found : " + className, e);
    }
    String fieldType = field.getType().getSimpleName();
    if (!fieldType.equalsIgnoreCase("String")) {
      throw new HibernateException("Enum field :" + fieldName + " is must string");
    }
    field.setAccessible(true);
    try {
      method = enumClass.getDeclaredMethod(methodName, String.class);
    } catch (Exception e) {
      throw new HibernateException(
          "Enum method not found : " + methodName + " in class " + className, e);
    }
    method.setAccessible(true);
  }

  private void intitValue(Properties properties) {
    className = properties.getProperty(CLASSNAME_KEY);
    fieldName = properties.getProperty(FIELD_KEY, DEFALUT_FIELD_VALUE);
    methodName = properties.getProperty(METHOD_KEY, DEFALUT_METHOD_VALUE);
  }

  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y;
  }

  public int hashCode(Object x) throws HibernateException {
    return x == null ? 0 : x.hashCode();
  }

  public boolean isMutable() {
    return false;
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  public Class<? extends Enum<?>> returnedClass() {
    return enumClass;
  }

  public int[] sqlTypes() {
    return new int[] {Types.INTEGER};
  }

  /**
   * 从JDBC ResultSet读取数据,将其转换为自定义类型后返回 (此方法要求对可能出现null值进行处理) paramArrayOfString中包含了当前自定义类型的映射字段名称
   * 
   */
  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
      throws HibernateException, SQLException {
    String resultStr = rs.getString(names[0]);
    if (resultStr == null || resultStr.trim().equals("")) return null;
    try {
      String result = resultStr;
      return method.invoke(null, result);
    } catch (Exception e) {
      throw new HibernateException("invoke enum static method error.", e);
    }
  }

  /**
   * 本方法将在Hibernate进行数据保存时被调用   我们可以通过PreparedStateme将自定义数据写入到对应的数据库表字段
   *
   */
  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
      throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.VARCHAR);
    } else {
      try {
        st.setString(index, (String) field.get(value));
      } catch (Exception e) {
        throw new HibernateException("get enum field value error.", e);
      }
    }

  }

}
