package com.hc.wechat.common.message;

import com.hc.wechat.common.enums.EnumTypeInterface;

/**
 * 通知
 * @author Administrator
 *
 */
public enum WxTemplateType implements EnumTypeInterface {

	/** 通知*/
	WXTEMPLATE_NOTICE(1, "通知"),
	;
	
	private int value;
	private String desc;
	
	WxTemplateType(int value, String desc){
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
	
	public static WxTemplateType get(int value) {
		for (WxTemplateType status : WxTemplateType.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("argument error: " + value);
	}

}
