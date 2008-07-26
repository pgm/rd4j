package com.github.rd4j.scanner;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

import com.github.rd4j.scanner.ClassScanner.Visitor;

public class FilterVisitorByClassesAnnotation implements Visitor {

	public static class AnnotationCheckingClassVisitor extends ClassAdapter {
		String queryAnnotation;
		ClassAcceptor classAcceptor;
		String className;
		boolean isAccepted = false;
		
		MethodAdapter methodAdapter = new MethodAdapter(new EmptyVisitor()) {

			@Override
			public AnnotationVisitor visitAnnotation(String names, boolean visible) {
				isAccepted = true;
				return new EmptyVisitor();
			}
			
		};
		
		public AnnotationCheckingClassVisitor(ClassVisitor visitor, String queryAnnotation, ClassAcceptor classAcceptor) {
			super(visitor);
			this.queryAnnotation = queryAnnotation;
			this.classAcceptor = classAcceptor;
		}

		@Override
		public void visit(int version, int access, String name, String signature,
				String superName, String[] interfaces) {
			this.className = name;
			
			super.visit( version, access, name, signature, superName, interfaces);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String classDesc, boolean visible) {
			if(classDesc.equals(queryAnnotation)) {
				classAcceptor.accept(className);
			}
			return null;
		}

		@Override
		public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
				String arg3, String[] arg4) {
			return methodAdapter;
		}

		
		
	}

	String queryAnnotation;
	ClassAcceptor classAcceptor;
	public FilterVisitorByClassesAnnotation (Class queryAnnotation, ClassAcceptor classAcceptor) {
		this.queryAnnotation = "L"+queryAnnotation.getCanonicalName().replace(".","/")+";";
		this.classAcceptor = classAcceptor;
	}
	
	public void visit(String className, InputStream contents) {
		ClassReader cr;
		try {
			cr = new ClassReader(contents);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		AnnotationCheckingClassVisitor v = new AnnotationCheckingClassVisitor(new EmptyVisitor(), queryAnnotation, classAcceptor);
		cr.accept(v, 0);
		
		if(v.isAccepted) {
			classAcceptor.accept(v.className.replace("/", "."));
		}
	}
	
}
