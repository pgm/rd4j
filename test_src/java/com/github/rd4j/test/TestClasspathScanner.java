package com.github.rd4j.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.rd4j.scanner.ClassAcceptor;
import com.github.rd4j.scanner.ClassScanner;
import com.github.rd4j.scanner.FilterVisitorByClassesAnnotation;


public class TestClasspathScanner {
	@Test
	public void testScanner() {
		final List<String> classes = new ArrayList<String>();
		ClassAcceptor acceptor = new ClassAcceptor() {
			public void accept(String className) {
				classes.add(className);
			}
		};
		ClassScanner.findInClasspath(this.getClass().getClassLoader(), 
				"com.github.rd4j.test", 
				new FilterVisitorByClassesAnnotation(SampleAnnotation.class, 
						acceptor));
		assertEquals(1, classes.size());
	}
}
