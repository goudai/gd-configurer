//package io.goudai.gateway.servlet;
//
//import io.goudai.gateway.mvc.annotation.Get;
//import io.goudai.gateway.mvc.annotation.Post;
//import io.goudai.gateway.mvc.annotation.Resources;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.net.URL;
//import java.util.*;
//import java.util.jar.JarEntry;
//import java.util.jar.JarInputStream;
//
///**
// * Created by freeman on 17/3/29.
// */
//
//@Slf4j
//public class DispatcherServlet extends HttpServlet {
//
//	private final static Map<String, MetaCache> mappingCache = new HashMap<>(64);
//
//	@Override
//	public void init() throws ServletException {
//		try {
//			val classpathPackageScanner = new ClasspathPackageScanner("io.goudai");
//			for (val clazz : classpathPackageScanner.getFullyQualifiedClassNameList()) {
//				Class<?> aClass = Class.forName(clazz);
//				val resources = aClass.getDeclaredAnnotation(Resources.class);
//				if (resources != null) {
//					val root = resources.value().intern() == "".intern() ? "/" : resources.value();
//					handleMethodMapping(aClass, root, aClass.newInstance());
//				}
//
//			}
//
//		} catch (Exception e) {
//			throw new ServletException(e.getMessage(), e);
//		}
//
//	}
//
//	private void handleMethodMapping(Class<?> aClass, String root, Object instance) {
//		val methods = aClass.getMethods();
//		root = root.intern() == "/" ? "" : root;
//		for (val method : methods) {
//			Get get = method.getDeclaredAnnotation(Get.class);
//			if (get != null) {
//				String mappingPath = root + get.value().intern() == "".intern() ? "/" : get.value();
//				MetaCache metaCache = MetaCache.builder().instance(instance).method(method).build();
//				Parameter[] parameters = method.getParameters();
//				for (Parameter parameter : parameters) {
//					metaCache.addArgument(ParameterMeta.builder().name(parameter.getName()).type(parameter.getType()).build());
//				}
//				mappingCache.put(mappingPath + "|GET", metaCache);
////				break;
//			}
//			Post post = method.getDeclaredAnnotation(Post.class);
//			if(post != null){
//				if (get != null) {
//					String mappingPath = root + get.value().intern() == "".intern() ? "/" : get.value();
//					MetaCache metaCache = MetaCache.builder().instance(instance).method(method).build();
//					Parameter[] parameters = method.getParameters();
//					for (Parameter parameter : parameters) {
//						metaCache.addArgument(ParameterMeta.builder().name(parameter.getName()).type(parameter.getType()).build());
//					}
//					mappingCache.put(mappingPath + "|POST", metaCache);
////				break;
//				}
//			}
//		}
//
//	}
//
//	@Override
//	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String requestURI = req.getRequestURI();
//		String method = req.getMethod();
//		MetaCache metaCache = mappingCache.get(requestURI+"|"+method.toUpperCase());
////		req.getParameterMap().forEach(());
//
//
//	}
//
//
//}
//
//
//class ClasspathPackageScanner {
//	private Logger logger = LoggerFactory.getLogger(ClasspathPackageScanner.class);
//
//	private String basePackage;
//	private ClassLoader cl;
//
//	/**
//	 * Construct an instance and specify the base package it should scan.
//	 *
//	 * @param basePackage The base package to scan.
//	 */
//	public ClasspathPackageScanner(String basePackage) {
//		this.basePackage = basePackage;
//		this.cl = getClass().getClassLoader();
//
//	}
//
//
//	public ClasspathPackageScanner(String basePackage, ClassLoader cl) {
//		this.basePackage = basePackage;
//		this.cl = cl;
//	}
//
//
//	public List<String> getFullyQualifiedClassNameList() throws IOException {
//		logger.info("开始扫描包{}下的所有类", basePackage);
//		return doScan(basePackage, new ArrayList<>());
//	}
//
//
//	private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
//		// replace dots with splashes
//		String splashPath = StringUtil.dotToSplash(basePackage);
//
//		// get file path
//		URL url = cl.getResource(splashPath);
//		String filePath = StringUtil.getRootPath(url);
//
//		// Get classes in that package.
//		// If the web server unzips the jar file, then the classes will exist in the form of
//		// normal file in the directory.
//		// If the web server does not unzip the jar file, then classes will exist in jar file.
//		List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
//		if (isJarFile(filePath)) {
//			// jar file
//			if (logger.isDebugEnabled()) {
//				logger.debug("{} is a jar", filePath);
//			}
//
//			names = readFromJarFile(filePath, splashPath);
//		} else {
//			// directory
//			if (logger.isDebugEnabled()) {
//				logger.debug("{} is a dir ", filePath);
//			}
//
//			names = readFromDirectory(filePath);
//		}
//
//		for (String name : names) {
//			if (isClassFile(name)) {
//				//nameList.add(basePackage + "." + StringUtil.trimExtension(name));
//				nameList.add(toFullyQualifiedName(name, basePackage));
//			} else {
//				// this is a directory
//				// check this directory for more classes
//				// do recursive invocation
//				doScan(basePackage + "." + name, nameList);
//			}
//		}
//
//		if (logger.isDebugEnabled()) {
//			for (String n : nameList) {
//				logger.debug("found {}", n);
//			}
//		}
//
//		return nameList;
//	}
//
//	/**
//	 * Convert short class name to fully qualified name.
//	 * e.g., String -> java.lang.String
//	 */
//	private String toFullyQualifiedName(String shortName, String basePackage) {
//		StringBuilder sb = new StringBuilder(basePackage);
//		sb.append('.');
//		sb.append(StringUtil.trimExtension(shortName));
//
//		return sb.toString();
//	}
//
//	private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
//		if (logger.isDebugEnabled()) {
//			logger.debug("从jar中读取类: {}", jarPath);
//		}
//
//		JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
//		JarEntry entry = jarIn.getNextJarEntry();
//
//		List<String> nameList = new ArrayList<>();
//		while (null != entry) {
//			String name = entry.getName();
//			if (name.startsWith(splashedPackageName) && isClassFile(name)) {
//				nameList.add(name);
//			}
//
//			entry = jarIn.getNextJarEntry();
//		}
//
//		return nameList;
//	}
//
//	private List<String> readFromDirectory(String path) {
//		File file = new File(path);
//		String[] names = file.list();
//
//		if (null == names) {
//			return null;
//		}
//
//		return Arrays.asList(names);
//	}
//
//	private boolean isClassFile(String name) {
//		return name.endsWith(".class");
//	}
//
//	private boolean isJarFile(String name) {
//		return name.endsWith(".jar");
//	}
//
//
//}
//
//class StringUtil {
//	private StringUtil() {
//
//	}
//
//	/**
//	 * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
//	 * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
//	 */
//	public static String getRootPath(URL url) {
//		String fileUrl = url.getFile();
//		int pos = fileUrl.indexOf('!');
//
//		if (-1 == pos) {
//			return fileUrl;
//		}
//
//		return fileUrl.substring(5, pos);
//	}
//
//	/**
//	 * "cn.fh.lightning" -> "cn/fh/lightning"
//	 *
//	 * @param name
//	 * @return
//	 */
//	public static String dotToSplash(String name) {
//		return name.replaceAll("\\.", "/");
//	}
//
//	/**
//	 * "Apple.class" -> "Apple"
//	 */
//	public static String trimExtension(String name) {
//		int pos = name.indexOf('.');
//		if (-1 != pos) {
//			return name.substring(0, pos);
//		}
//
//		return name;
//	}
//
//	/**
//	 * /application/home -> /home
//	 *
//	 * @param uri
//	 * @return
//	 */
//	public static String trimURI(String uri) {
//		String trimmed = uri.substring(1);
//		int splashIndex = trimmed.indexOf('/');
//
//		return trimmed.substring(splashIndex);
//	}
//}
//
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//class MetaCache {
//	private Method method;
//	private Object instance;
//	private List<ParameterMeta> arguments = new ArrayList<>();
//
//
//	public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
//		return this.method.invoke(instance, args);
//	}
//
//	public Object invoke() throws InvocationTargetException, IllegalAccessException {
//		return this.method.invoke(instance);
//	}
//
//	public void addArgument(ParameterMeta argument) {
//		arguments.add(argument);
//	}
//}
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//class ParameterMeta {
//	String name;
//	Class type;
//}
