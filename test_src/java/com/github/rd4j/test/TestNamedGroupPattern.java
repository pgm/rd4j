package com.github.rd4j.test;

import java.util.regex.Matcher;

import org.junit.Test;

import com.github.rd4j.NamedGroupPattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestNamedGroupPattern {
	@Test
	public void testNoGroupMatch() {
		NamedGroupPattern pattern = new NamedGroupPattern("abc");
		Matcher matcher = pattern.matcher("abc");
		assertTrue(matcher.matches());
	}

	@Test
	public void testSingleGroupMatch() {
		NamedGroupPattern pattern = new NamedGroupPattern("ab(?P<rock>.)c");
		Matcher matcher = pattern.matcher("abdc");
		assertTrue(matcher.matches());
		assertEquals("d", pattern.getNamedGroup("rock", matcher));
	}

	@Test
	public void testMultipleGroupMatch() {
		NamedGroupPattern pattern = new NamedGroupPattern("ab(?P<rock>.)c(?P<on>\\d)");
		Matcher matcher = pattern.matcher("abdc9");
		assertTrue(matcher.matches());
		assertEquals("d", pattern.getNamedGroup("rock", matcher));
		assertEquals("9", pattern.getNamedGroup("on", matcher));
	}
}
