package com.hc.security.entity.enums;

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
 * 自定义枚举客户化映射类型<br/>
 * 参考配置：
 * 
 * @TypeDefs({@TypeDef(name = "status" , typeClass = EnumType. class , parameters = {
 * @Parameter(name = "class" , value = "demo.domain.one2one.testtwo.Order$StatusEnum" ),
 * @Parameter(name = "field" , value = "status" ),
 * @Parameter(name = "method" , value = "getStatusEnum" )}) })
 * @Type(type = "status" ) Parameter： class:枚举类名(必填) field:需要保存进数据库的字段名(选填 默认为"value")，该字段必须为int类型
 *            method:根据字段值获取枚举的静态方法名(选填 ,默认为"get")
 * @author hc
 *
 * @version 1.0,2017年8月8日
 */

public class EnumType implements UserType, ParameterizedType {
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

  private void intitValue(Properties properties) {
    className = properties.getProperty(CLASSNAME_KEY);
    fieldName = properties.getProperty(FIELD_KEY, DEFALUT_FIELD_VALUE);
    methodName = properties.getProperty(METHOD_KEY, DEFALUT_METHOD_VALUE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setParameterValues(Properties parameters) {
    intitValue(parameters);
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
    if (!fieldType.equals("int")) {
      throw new HibernateException("Enum field :" + fieldName + " is must int");
    }
    field.setAccessible(true);
    try {
      method = enumClass.getDeclaredMethod(methodName, int.class);
    } catch (Exception e) {
      throw new HibernateException("Enum method not found : " + methodName + " in class "
          + className, e);
    }
    method.setAccessible(true);

  }

  @Override
  public int[] sqlTypes() {
    return new int[] {Types.INTEGER};
  }

  @Override
  public Class<? extends Enum<?>> returnedClass() {
    return enumClass;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y;
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x == null ? 0 : x.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
      throws HibernateException, SQLException {
    String resultStr = rs.getString(names[0]);
    if (resultStr == null || resultStr.trim().equals("")) return null;
    try {
      int result = Integer.parseInt(resultStr);
      return method.invoke(null, result);
    } catch (Exception e) {
      throw new HibernateException("invoke enum static method error.", e);
    }
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
      throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.INTEGER);
    } else {
      try {
        st.setInt(index, field.getInt(value));
      } catch (Exception e) {
        throw new HibernateException("get enum field value error.", e);
      }
    }

  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

}
