package com.github.rd4j.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.rd4j.BoundUrl;

public class ClassScanner {
	static interface Visitor {
		void visit(String name, InputStream contents);
	}
	
	static boolean matches(String base, String filename) {
		return filename.endsWith(".class");
	}
	
	static void findWithinJar(String jarPath, String base, Visitor visitor) throws IOException {
		JarFile jar = new JarFile(jarPath);
		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if(matches(base, entry.getName())) {
				InputStream is = jar.getInputStream(entry);
				visitor.visit(entry.getName(), is);
				is.close();
			}
		}
	}

	static public void findWithinDirectory(String filepath, String base, Visitor visitor) throws IOException {
		File rootDir = new File(filepath);
		File files[] = rootDir.listFiles();
		for(File f : files) {
			if(matches(base, f.getAbsolutePath())) {
				InputStream is = new FileInputStream(f);
				visitor.visit(f.getAbsolutePath(), is);
				is.close();
			}
		}
	}
	
	static public void findInClasspath(ClassLoader classLoader, String base, Visitor visitor) {
		try {
			Enumeration<URL> e = classLoader.getResources(base.replace(".", "/"));
			while(e.hasMoreElements()) {
				URL url = e.nextElement();
				if(url.getProtocol().equals("jar")) {
					String path = url.getPath();
					int bangIndex = path.indexOf("!");
					if(bangIndex < 0) {
						throw new RuntimeException("Could not read jar entry: "+path);
					}
					String jarPath = path.substring(0, bangIndex);
					String fileWithinJar = path.substring(bangIndex+1);
					findWithinJar(jarPath, base, visitor);
				} else if(url.getProtocol().equals("file")) {
					findWithinDirectory(url.getPath(), base, visitor);
				} else {
					throw new RuntimeException("Only classpaths including files or jars are supported");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> findClassesWithAnnotation(ClassLoader classLoader, String packageName) {
		final List<String> classNames = new ArrayList<String>();
		
		ClassAcceptor classAcceptor = new ClassAcceptor() {
			public void accept(String className) {
				classNames.add(className);
			}
		};
		
		ClassScanner.findInClasspath(classLoader, 
			packageName, 
			new FilterVisitorByClassesAnnotation(BoundUrl.class, classAcceptor));
		
		return classNames;
	}
}
