package com.frame.sdk.model;

import com.frame.sdk.util.ClassUtil;

public abstract class Model {
	/**
	 * 返回各个字段的名称及对应的值
	 */
	@Override
	public String toString() {
		return ClassUtil.getFieldValue(this);
	}

	public Model() {
		super();
	}

}
