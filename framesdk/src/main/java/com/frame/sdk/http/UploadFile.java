package com.frame.sdk.http;

import java.io.File;

/**
 * 上传的文件封装类
 * 
 */
public class UploadFile extends File {
	private static final long serialVersionUID = 1L;
	private String formName;// 表单字段名
	private String fileName;// 文件名

	public UploadFile(String path, String formName, String fileName) {
		super(path);
		this.formName = formName;
		this.fileName = fileName;
	}

	public UploadFile(String path, String formName) {
		super(path);
		this.formName = formName;
		this.fileName = getName();
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
