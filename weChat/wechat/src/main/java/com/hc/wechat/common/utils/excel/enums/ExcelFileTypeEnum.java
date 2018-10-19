package com.hc.wechat.common.utils.excel.enums;

/**
 * excel 版本
 * 
 * @author hc
 *
 */
public enum ExcelFileTypeEnum {
  /**
   * excel2003
   */
  XLS(".xls"),
  /**
   * excel2007后
   */
  XLSX(".xlsx");
  private String value;

  private ExcelFileTypeEnum(String value) {
    this.setValue(value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
