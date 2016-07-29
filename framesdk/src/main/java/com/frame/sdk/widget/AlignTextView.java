package com.frame.sdk.widget;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 文本对齐的TextView
 */
public class AlignTextView extends TextView {
	private String indentText = "    ";

	public AlignTextView(Context context) {
		super(context);
	}

	public AlignTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlignTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		String text = getText().toString();
		text = toDBC(text);
		text = stringFilter(text);
		// text = textIndent(text);
		setText(text);
		super.onDraw(canvas);
	}

	/**
	 * 字符全角化
	 */
	private String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 清除掉特殊字符
	 */
	private String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("？", "?").replaceAll("《", "<<").replaceAll("》", ">>").replaceAll("，", ",").replaceAll("（", "(")
				.replaceAll("）", ")").replaceAll("：", ":").replaceAll("；", ";");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 首行缩进(有点问题)
	 */
	private String textIndent(String input) {
		char[] c = input.toCharArray();
		StringBuilder sb = new StringBuilder(indentText);
		for (int i = 0; i < c.length; i++) {
			sb.append(c[i]);
			if (c[i] == '\n') {
				sb.append(indentText);
			}
		}
		return sb.toString();
	}

	/**
	 * 设置首行缩进多少个字符
	 */
	public void setIndentSpace(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(" ");
		}
		indentText = sb.toString();
	}
}
