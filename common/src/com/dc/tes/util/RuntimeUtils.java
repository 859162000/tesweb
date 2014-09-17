package com.dc.tes.util;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.util.StreamUtils;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.Message;


/**
 * 工具类 用于提供一些运行时的基础支持
 * 
 * @author huangzx
 * 
 */
public class RuntimeUtils {
	
	private static Log log = LogFactory.getLog(RuntimeUtils.class);
	
	/**
	 * 获取模拟器运行的当前路径
	 */
	public static final String startDir;
	/**
	 * 获取模拟器运行的classpath当前路径
	 */
	public static final String classDir;

	static {
		try {
			String path = new RuntimeUtils().getClass().getClassLoader().getResource("").toURI().getPath();
			classDir = new File(path).getPath();
			startDir = new File(path).getParent();
		} catch (Exception ex) {
			throw new TESException(CommonErr.GetStartDirFail, ex);
		}
	}

	/**
	 * gb2312编码
	 */
	public static final Charset gb2312 = Charset.forName("gb2312");
	/**
	 * utf-8编码
	 */
	public static final Charset utf8 = Charset.forName("utf-8");

	/**
	 * 打开一个指定资源的输入流
	 * 
	 * @param path
	 *            资源的路径 规定使用/作为路径分隔符
	 * @return 指向该资源的输入流 如果该资源不存在则返回null
	 */
	public static InputStream OpenResource(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	/**
	 * 读取指定的资源
	 * 
	 * @param path
	 *            资源的路径 规定使用/作为路径分隔符
	 * @return 该资源的内容 如果该资源不存在则返回null
	 */
	public static byte[] ReadResource(String path) {
		InputStream s = OpenResource(path);
		if (s == null)
			return null;

		try {
			return StreamUtils.getBytes(s);
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOReadFail, path, ex);
		} finally {
			try {
				s.close();
			} catch (IOException ex) {
				throw new TESException(CommonErr.IO.CloseInputStreamFail, path, ex);
			}
		}
	}

	/**
	 * 以字符串形式读取指定的文本资源
	 * 
	 * @param path
	 *            资源的路径 规定使用/作为路径分隔符
	 * @param encoding
	 *            该文本资源的编码
	 * @return 该资源的内容 如果该资源不存在则返回null
	 */
	public static String ReadResource(String path, Charset encoding) {
		byte[] bytes = ReadResource(path);
		if (bytes == null)
			return null;
		else
			return new String(bytes, encoding);
	}

	/**
	 * 将相对于模拟器根路径的相对路径映射为文件系统的绝对路径
	 * 
	 * @param path
	 *            相对路径
	 * @return 绝对路径
	 */
	public static String MapPath(String path) {
		if (path == null || path.length() == 0)
			return RuntimeUtils.startDir;

		return path.startsWith("/") || path.startsWith("\\") ? RuntimeUtils.startDir + path : RuntimeUtils.startDir + File.separator + path;
	}

	/**
	 * 将相对于模拟器根路径的相对路径映射为一个文件对象
	 * 
	 * @param path
	 *            相对路径
	 * @return 与提供的相对路径对应的文件对象
	 */
	public static File MapFile(String path) {
		if (path == null || path.length() == 0)
			return null;

		return new File(MapPath(path));
	}

	/**
	 * 从指定的文本文件中读取全部数据
	 * 
	 * @param f
	 *            要读取的文件
	 * @param encoding
	 *            编码
	 * @return 文件中的全部数据
	 * @throws IOException
	 */
	public static String ReadFile(File f, Charset encoding) {
		return new String(ReadFile(f), encoding);
	}

	/**
	 * 从指定的文件中读取全部数据
	 * 
	 * @param f
	 *            要读取的文件
	 * @return 文件中的全部数据
	 * @throws
	 * @throws IOException
	 */
	public static byte[] ReadFile(File f) {
		if (f == null)
			throw new TESException(CommonErr.IO.FileNotFound, "<null>");

		FileInputStream s = null;
		try {
			s = new FileInputStream(f);
			return StreamUtils.getBytes(s);
		} catch (FileNotFoundException ex) {
			throw new TESException(CommonErr.IO.FileNotFound, f.getPath());
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOReadFail, f.getPath(), ex);
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
					throw new TESException(CommonErr.IO.CloseInputStreamFail, f.getPath(), ex);
				}
		}
	}

	/**
	 * 向指定的文本文件写入数据（覆盖写入模式）
	 * 
	 * @param f
	 *            要写入到的文本文件
	 * @param content
	 *            要写入的内容
	 * @param encoding
	 *            编码
	 */
	public static void WriteFile(File f, String content, Charset encoding) {
		if (content == null)
			content = "";

		WriteFile(f, content.getBytes(encoding));
	}

	/**
	 * 向指定的文件写入数据（覆盖写入模式）
	 * 
	 * @param f
	 *            要写入到的文件
	 * @param bytes
	 *            要写入的内容
	 */
	public static void WriteFile(File f, byte[] bytes) {
		if (f == null)
			throw new TESException(CommonErr.IO.CreateFileFail, "<null>");
		if (bytes == null)
			bytes = ArrayUtils.EMPTY_BYTE_ARRAY;

		FileOutputStream s = null;

		try {
			s = new FileOutputStream(f, false);
			s.write(bytes);
		} catch (FileNotFoundException ex) {
			throw new TESException(CommonErr.IO.FileNotFound, f.getPath());
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOWriteFail, f.getPath(), ex);
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
					throw new TESException(CommonErr.IO.CloseOutputStreamFail, f.getPath(), ex);
				}
		}
	}

	/**
	 * 向指定的文本文件写入数据（追加写入模式）
	 * 
	 * @param f
	 *            要追加到的文本文件
	 * @param content
	 *            要追加的内容
	 * @param encoding
	 *            编码
	 */
	public static void AppendFile(File f, String content, Charset encoding) {
		if (content == null)
			content = "";

		AppendFile(f, content.getBytes(encoding));
	}

	/**
	 * 向指定的文件写入数据（追加写入模式）
	 * 
	 * @param f
	 *            要追加到的文件
	 * @param bytes
	 *            要追加的内容
	 * @throws IOException
	 */
	public static void AppendFile(File f, byte[] bytes) {
		if (bytes == null)
			bytes = ArrayUtils.EMPTY_BYTE_ARRAY;

		FileOutputStream s = null;

		try {
			s = new FileOutputStream(f, true);
			s.write(bytes);
		} catch (FileNotFoundException ex) {
			throw new TESException(CommonErr.IO.FileNotFound, f.getPath());
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOWriteFail, f.getPath(), ex);
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
					throw new TESException(CommonErr.IO.CloseOutputStreamFail, f.getPath(), ex);
				}
		}
	}

	/**
	 * 列出某个目录及其子目录下的所有文件
	 * 
	 * @param f
	 *            要遍历的目录
	 * @return
	 */
	public static File[] ListFiles(File f) {
		if (!f.isDirectory())
			return new File[] { f };
		if (!f.exists())
			return new File[0];

		ArrayList<File> lst = new ArrayList<File>();

		for (File entry : f.listFiles())
			if (entry.isDirectory())
				lst.addAll(Arrays.asList(ListFiles(entry)));
			else
				lst.add(entry);

		return lst.toArray(new File[0]);
	}

	/**
	 * 删除指定的目录及其子目录
	 * 
	 * @param dir
	 *            要删除的目录
	 */
	public static void DeleteDirectory(File dir) {
		if (dir == null)
			throw new TESException(CommonErr.IO.DeleteTargetNotFound, "<null>");

		if (!dir.exists())
			throw new TESException(CommonErr.IO.DeleteTargetNotFound, dir.getPath());
		if (!dir.isDirectory())
			throw new TESException(CommonErr.IO.DeleteTargetNotDir, dir.getPath());

		for (File f : dir.listFiles())
			if (f.isDirectory())
				DeleteDirectory(f);
			else
				f.delete();

		dir.delete();
	}

	/**
	 * 将字节流以一个易于观察的方式打成字符串
	 * 
	 * @param bytes
	 *            字节流
	 * @param charset
	 *            字节的编码
	 * @return 字节流的一个易于观察的表现形式
	 */
	public static String PrintHex(byte[] bytes, Charset charset) {
		if (bytes == null)
			bytes = ArrayUtils.EMPTY_BYTE_ARRAY;

		StringBuffer buffer = new StringBuffer("ADDR |  0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F |  D A T A [" + bytes.length + "]" + SystemUtils.LINE_SEPARATOR);

		int lineCount = bytes.length % 16 == 0 ? bytes.length / 16 : bytes.length / 16 + 1;
		for (int i = 0; i < lineCount; i++) {
			int count = i == lineCount - 1 ? bytes.length % 16 == 0 ? 16 : bytes.length % 16 : 16;

			String addr = StringUtils.leftPad(Integer.toHexString(i).toUpperCase(), 3, '0') + "0";
			StringBuffer body = new StringBuffer(48);
			String data;
			try {
				data = new String(bytes, 16 * i, count, charset.name());
			} catch (UnsupportedEncodingException ex) {
				throw new TESException(CommonErr.UnsupportedEncoding, charset.name());
			}

			for (int j = 0; j < count; j++) {
				byte b = bytes[i * 16 + j];

				int m = b >> 4 & 0x0F;
				int n = b & 0x0F;

				body.append(" ");
				body.append((char) (m > 9 ? 'A' + m - 10 : '0' + m));
				body.append((char) (n > 9 ? 'A' + n - 10 : '0' + n));
			}

			StringBuffer dataBuffer = new StringBuffer();
			for (int j = 0; j < data.length(); j++)
				dataBuffer.append(data.charAt(j) < 32 ? '.' : data.charAt(j));

			buffer.append(addr).append(" |");
			buffer.append(StringUtils.rightPad(body.toString(), 48)).append(" | ");
			buffer.append(dataBuffer.toString()).append(SystemUtils.LINE_SEPARATOR);
		}

		return buffer.toString();
	}

	/**
	 * 将异常的信息导出为字符串
	 * 
	 * @param ex
	 *            异常对象
	 * @return 异常的信息
	 */
	public static String PrintEx(Throwable ex) {
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		return w.toString();
	}

	/**
	 * 将一个字符串数组解析为由指定的类型组成的数组 目前支持String int boolean Enum
	 * 
	 * @param <T>
	 *            期望的数组中的元素类型
	 * @param value
	 *            要转换的字符串的数组
	 * @param cls
	 *            期望的数组中的元素类型
	 * @return 由字符串数组转换得到的对象数组 如果给定的字符串数组为null 则返回null 如果数组中某个元素为null 则对应的对象为null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] FromString(String[] value, Class<T> cls) {
		if (value == null)
			return null;

		ArrayList<T> lst = new ArrayList<T>();
		for (String str : value)
			lst.add(FromString(str, cls));
		return lst.toArray((T[]) Array.newInstance(cls, 0));
	}

	/**
	 * 将一个字符串解析为指定的类型 目前支持String int boolean Enum
	 * 
	 * @param <T>
	 *            期望的类型
	 * @param value
	 *            要转换的字符串
	 * @param cls
	 *            期望的类型
	 * @return 由字符串转换得到的对象 如果给定的字符串为null 则返回null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T FromString(String value, Class<T> cls) {
		if (value == null)
			return null;
		if (cls == null)
			throw new TESException(CommonErr.UnsupportedConversionFromString, "class = <null>");

		try {
			if (cls == String.class)
				return (T) value;
			if (cls == int.class || cls == Integer.class)
				return (T) (Integer) Integer.parseInt(value);
			if (cls == boolean.class || cls == Boolean.class)
				return (T) (Boolean) ("true".equalsIgnoreCase(value));
			if (cls.isEnum())
				return (T) EnumUtils.getEnum(cls, value);

			throw new TESException(CommonErr.UnsupportedConversionFromString, "[" + value + "] -> " + cls.getName());
		} catch (Exception ex) {
			throw new TESException(CommonErr.ConvertFromStringFail, "[" + value + "] -> " + cls.getName(), ex);
		}
	}

	/**
	 * 获取一个指定类型的默认值
	 * <p>
	 * <li>对于数字类型来说是0</li>
	 * <li>对于bool类型来说是false</li>
	 * <li>对于字符串类型来说是空字符串</li>
	 * <li>对于数组类型来说是一个长度为0的数组</li>
	 * <li>对于其它类型来说是null</li>
	 * </p>
	 * 
	 * @param <T>
	 *            类型
	 * @param cls
	 *            类型
	 * @return 类型的默认值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T CreateDefaultValue(Class<T> cls) {
		if (cls == boolean.class || cls == Boolean.class)
			return (T) (Boolean) false;
		if (cls == char.class || cls == Character.class)
			return (T) (Character) '\0';
		if (cls == byte.class || cls == Byte.class)
			return (T) (Byte) (byte) 0;
		if (cls == short.class || cls == Short.class)
			return (T) (Short) (short) 0;
		if (cls == int.class || cls == Integer.class)
			return (T) (Integer) 0;
		if (cls == long.class || cls == Long.class)
			return (T) (Long) (long) 0;
		if (cls == float.class || cls == Float.class)
			return (T) (Float) (float) 0;
		if (cls == double.class || cls == Double.class)
			return (T) (Double) (double) 0;
		if (cls == String.class)
			return (T) "";
		if (cls.isArray())
			return (T) Array.newInstance(cls.getComponentType(), 0);

		return null;
	}

	/**
	 * 编译一篇完整的java类代码 得到一个类 使用jdk1.6中的编译api实现
	 * 
	 * @param code
	 *            java类代码
	 * @param clsName
	 *            类名称
	 * @return 编译出的类对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws URISyntaxException
	 */
	public static Class<?> CompileClass(final String code, String clsName) {
		File f = null;
		
		StringBuilder cp = new StringBuilder(); 
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader(); 
		for (URL url : urlClassLoader.getURLs()) 
			 cp.append(url.getFile()).append(File.pathSeparator); 
			
		try {
			
			String classPath = java.net.URLDecoder.decode(cp.toString(),"utf-8");
			log.debug(classPath);
			// 将代码写到当前的类路径中的某个临时文件中
			f = new File(classDir + File.separator + clsName + ".java");
			RuntimeUtils.WriteFile(f, code, RuntimeUtils.utf8);

			// 建立编译器实例
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null)
				return CompileClass2(code, clsName);

			// 将错误信息重定向到内存中
			ByteArrayOutputStream err = new ByteArrayOutputStream();

			// 编译			
			int result = compiler.run(null, null, err, "-cp", classPath, f.getPath());

			// 判断编译是否成功
			if (result != 0)
				throw new TESException(CommonErr.DynamicCompileSyntaxError, new String(err.toByteArray()));

			// 加载类
			return Thread.currentThread().getContextClassLoader().loadClass(clsName);
		} catch (Exception ex) {
			throw new TESException(CommonErr.DynamicCompileFail, ex);
		} finally {
			// 删除已经没用了的java源文件和class文件
			if (f != null) {
				f.delete();
				File f2 = new File(classDir + File.separator + clsName + ".class");
				if (f2.exists())
					f2.delete();
			}
		}
	}

	/**
	 * 编译一篇完整的java类代码 得到一个类 使用tools.jar中的javac实现
	 * 
	 * @param code
	 *            java类代码
	 * @param clsName
	 *            类名称
	 * @return 编译出的类对象
	 */
	public static Class<?> CompileClass2(final String code, String clsName) {
		
		StringBuilder cp = new StringBuilder(); 
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader(); 
		for (URL url : urlClassLoader.getURLs()) 
			 cp.append(url.getFile()).append(File.pathSeparator); 
	
		File f = null;
		
		try {
			
			String classPath = java.net.URLDecoder.decode(cp.toString(),"utf-8");
			
			// 将代码写到当前的类路径中的某个临时文件中
			f = new File(classDir + File.separator + clsName + ".java");
			//f = new File(clsName + ".java");
			RuntimeUtils.WriteFile(f, code, RuntimeUtils.utf8);

			// 调用javac编译该文件
			String[] cpargs = new String[] {
					"-d",
					RuntimeUtils.classDir,
					"-cp",
					classPath,
					f.getPath() };
			StringWriter w = new StringWriter();
			int status = com.sun.tools.javac.Main.compile(cpargs, new PrintWriter(w));

			// 判断编译是否成功
			if (status != 0)
				throw new TESException(CommonErr.DynamicCompileSyntaxError, w.getBuffer().toString());

			// 加载类
			return Thread.currentThread().getContextClassLoader().loadClass(clsName);
		} catch (Exception ex) {
			throw new TESException(CommonErr.DynamicCompileFail, ex);
		} finally {
			// 删除已经没用了的java源文件和class文件
			if (f != null) {
				f.delete();
				File f2 = new File(classDir + File.separator + clsName + ".class");
				if (f2.exists())
					f2.delete();
			}
		}
	}
	
	
	/**
	 * 当前classpath中所有的类的类名的缓存
	 */
	private static String[] s_classNameCache = null;

	/**
	 * 列出classpath中包含的所有的类的名称
	 * 
	 * @return 当前classpath中包含的所有的类的名称
	 */
	public static String[] ListClassNames() {
		if (s_classNameCache != null)
			return s_classNameCache;

		ArrayList<String> lst = new ArrayList<String>();
		String classpath = System.getProperty("java.class.path");
		for (String entry : classpath.split(SystemUtils.IS_OS_WINDOWS ? "\\;" : "\\:"))
			try {
				if (entry.endsWith(".jar"))
					for (Object e : EnumerationUtils.toList(new JarFile(entry).entries()))
						if (((JarEntry) e).getName().endsWith(".class"))
							lst.add(((JarEntry) e).getName().substring(0, ((JarEntry) e).getName().length() - 6).replace('/', '.'));
						else
							;
				else
					for (File f : ListFiles(new File(entry)))
						if (f.getName().endsWith(".class")) {
							String fullName = f.getPath().substring(entry.length() + 1);
							lst.add(fullName.substring(0, fullName.length() - 6).replace('/', '.').replace('\\', '.'));
						}
			} catch (Throwable ex) {
				continue;
			}

		s_classNameCache = lst.toArray(new String[0]);
		return s_classNameCache;
	}

	/**
	 * 尝试列出当前classpath下的所有类
	 * <p>
	 * !!!注意!!!
	 * </p>
	 * 不保证会将classpath中提到的所有类都列出来，尤其是在使用了自定义的ClassLoader时（运行在应用服务器中时通常会发生这种情况） <br/>
	 * 另外，该方法会尝试将classpath中所有的类都加载到当前的ClassLoader中，这会造成非常严重的性能问题
	 * 
	 * @return 当前classpath下的所有类
	 */
	public static Class<?>[] ListClasses() {
		ArrayList<Class<?>> lst = new ArrayList<Class<?>>();

		for (String className : ListClassNames())
			try {
				lst.add(Class.forName(className));
			} catch (Exception ex) {
			}
		return lst.toArray(new Class[0]);
	}

	/**
	 * 尝试列出指定的包下面的所有类
	 * 
	 * @param p
	 *            包
	 * @param includeSubPackages
	 *            是否将该包下面的子包中的类也列出来
	 * @return 该包下的所有类
	 */
	public static Class<?>[] ListClasses(Package p, boolean includeSubPackages) {
		ArrayList<Class<?>> lst = new ArrayList<Class<?>>();

		for (String className : ListClassNames())
			if (className.startsWith(p.getName()))
				if (!includeSubPackages && !className.substring(p.getName().length() + 1).contains("."))
					try {
						lst.add(Class.forName(className));
					} catch (Exception ex) {
					}
		return lst.toArray(new Class[0]);
	}

	/**
	 * 获取CPU使用率
	 * 
	 * @return CPU使用率 是一个1~100之间的整数
	 */
	public int MeasureCPU() {
		return 5;
	}
}