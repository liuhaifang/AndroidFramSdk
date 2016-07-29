package com.frame.sdk.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写工具类
 */
public class FileUtil {

	/**
	 * 读取文本文件
	 */
	public static String readText(String filePath) {
		StringBuffer str = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filePath));
			String s;
			try {
				while ((s = in.readLine()) != null)
					str.append(s + '\n');
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	/**
	 * 写入指定的文本文件
	 * 
	 * @param isAppend
	 *            为true表示追加，false表示重头开始写
	 * @param text
	 *            要写入的文本字符串，为null时直接返回
	 */
	public static void writeText(String filePath, boolean isAppend, String text) {
		if (text == null)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, isAppend));
			try {
				out.write(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取二进制文件
	 */
	public static byte[] readBytes(String filePath) {
		byte[] data = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
			try {
				data = new byte[in.available()];
				in.read(data);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 把字节数组写入二进制文件
	 * 
	 * @param isAppend
	 *            为true表示追加，false表示重头开始写
	 */
	public static void writeBytes(String filePath, boolean isAppend, byte[] data) {
		if (data == null)
			return;
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath, isAppend));
			try {
				out.write(data);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个对象写入文件
	 * 
	 * @param isAppend
	 *            为true表示追加，false表示重头开始写
	 */
	public static void writeObject(String filePath, Object o, boolean isAppend) {
		if (o == null)
			return;
		try {
			File f = new File(filePath);
			MyObjectOutputStream out = MyObjectOutputStream.newInstance(f, new FileOutputStream(f, isAppend));
			try {
				out.writeObject(o);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个对象数组写入文件
	 * 
	 * @param isAppend
	 *            为true表示追加，false表示重头开始写
	 */
	public static void writeObject(String filePath, Object[] objects, boolean isAppend) {
		if (objects == null)
			return;
		try {
			File f = new File(filePath);
			MyObjectOutputStream out = MyObjectOutputStream.newInstance(f, new FileOutputStream(f, isAppend));
			try {
				for (Object o : objects)
					out.writeObject(o);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取对象，返回一个对象
	 */
	public static Object readObject(String filePath) {
		Object o = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
			try {
				o = in.readObject();
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 读取对象，返回一个对象列表
	 */
	public static List<Object> readObjects(String filePath) {
		List<Object> objects = new ArrayList<Object>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
			try {
				while (true) {
					Object obj = in.readObject();
					if (obj != null)
						objects.add(obj);
					else
						break;
				}
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objects;
	}

	/**
	 * 此类继承ObjectOutputStream，重写writeStreamHeader()方法,以实现追加写入时去掉头部信息
	 */
	private static class MyObjectOutputStream extends ObjectOutputStream {
		private static File f;

		// writeStreamHeader()方法是在ObjectOutputStream的构造方法里调用的
		// 由于覆盖后的writeStreamHeader()方法用到了f。如果直接用此构造方法创建
		// 一个MyObjectOutputStream对象，那么writeStreamHeader()中的f是空指针
		// 因为f还没有初始化。所以这里采用单态模式
		private MyObjectOutputStream(OutputStream out, File f) throws IOException, SecurityException {
			super(out);
		}

		// 返回一个MyObjectOutputStream对象，这里保证了new MyObjectOutputStream(out, f)
		// 之前f已经指向一个File对象
		public static MyObjectOutputStream newInstance(File file, OutputStream out) throws IOException {
			f = file;// 本方法最重要的地方：构建文件对象，两个引用指向同一个文件对象
			return new MyObjectOutputStream(out, f);
		}

		@Override
		protected void writeStreamHeader() throws IOException {
			// 文件不存在或文件为空,此时是第一次写入文件，所以要把头部信息写入。
			if (!f.exists() || (f.exists() && f.length() == 0)) {
				super.writeStreamHeader();
			} else {
				// 不需要做任何事情
			}
		}
	}
}
