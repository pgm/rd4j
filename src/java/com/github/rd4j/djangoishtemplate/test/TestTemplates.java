package com.github.rd4j.djangoishtemplate.test;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.djangoishtemplate.Template;
import com.github.rd4j.djangoishtemplate.TemplateResolver;

public class TestTemplates {
	@Test
	public void testSimple() throws Exception {
		Template t = new Template("no expansion");
		assertEquals("no expansion", renderTemplate(t, null));
	}

	@Test
	public void testVariable() throws Exception {
		Template t = new Template("testValue = {{ testValue }}");
		assertEquals("testValue = alpha", renderTemplate(t, this));
	}
	
	@Test
	public void testForLoop() throws Exception {
		Template t = new Template("start {% for indexValue in sampleValues %}[{{ indexValue}}] {% endfor %}end");
		assertEquals("start [cat] [dog] [rat] end", renderTemplate(t, this));
	}
	
	@Test
	public void testIfTrueClause() throws Exception {
		Template t = new Template("start {% if trueProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, this));
	}

	@Test
	public void testIfFalseClause() throws Exception {
		Template t = new Template("start {% if falseProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  F  end", renderTemplate(t, this));
	}
	
	@Test
	public void testHalfIfTrueClause() throws Exception {
		Template t = new Template("start {% if trueProperty %} T {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, this));
	}

	@Test
	public void testHalfIfFalseClause() throws Exception {
		Template t = new Template("start {% if falseProperty %} T {% endif %} end");
		assertEquals("start  end", renderTemplate(t, this));
	}

	
	@Test
	public void testBlocks() throws Exception {
		final Map<String, Template> defs = new HashMap<String,Template>();
		Template parent = new Template("start {% block content %} sample text {% endblock content %}{% block final %}*{% endblock final %} end");
		defs.put("parent", parent);
		TemplateResolver resolver = new TemplateResolver() {

			public Template findTemplate(String name) {
				return defs.get(name);
			}
			
		};

		Template child = new Template("{% extends parent %} {% block content %} altcontent {% endblock content %}", resolver);
		
		assertEquals("start  altcontent * end", renderTemplate(child, this));
		assertEquals("start  sample text * end", renderTemplate(parent, this));
		
		defs.put("child", child);
		Template grandchild = new Template("{% extends child %} {% block final %}!{% endblock final %}", resolver);
		assertEquals("start  altcontent ! end", renderTemplate(grandchild, this));
	}
	

	@Test
	public void testNestedBlocks() throws Exception {
		final Map<String, Template> defs = new HashMap<String,Template>();

		TemplateResolver resolver = new TemplateResolver() {

			public Template findTemplate(String name) {
				return defs.get(name);
			}
		};

		
		Template parent = new Template("start {% block content %} sample text {% endblock content %} end");
		defs.put("parent", parent);

		Template child = new Template("{% extends parent %} {% block content %} 1 {% block inner %} 2 {% endblock inner %}{% endblock content %}", resolver);
		defs.put("child", child);

		
		Template grandchild = new Template("{% extends child %} {% block inner %} 3 {% endblock inner %}", resolver);

		assertEquals("start  1  2  end", renderTemplate(child, this));
		assertEquals("start  1  3  end", renderTemplate(grandchild, this));
	}
	
	String renderTemplate(Template tf, Object root) throws Exception {
		StringWriter w = new StringWriter();
		tf.renderTemplate(w, root);
		return w.toString();
	}
	
	public String testValue = "alpha";
	
	public String indexValue = "";
	public String [] sampleValues = new String[] {"cat", "dog", "rat"};
	
	public boolean trueProperty = true;
	public boolean falseProperty = false;
}
