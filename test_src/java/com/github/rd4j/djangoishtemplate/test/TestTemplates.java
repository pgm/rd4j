package com.github.rd4j.djangoishtemplate.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.djangoishtemplate.ExpressionContext;
import com.github.rd4j.djangoishtemplate.ExpressionParser;
import com.github.rd4j.djangoishtemplate.Macro;
import com.github.rd4j.djangoishtemplate.ParsedExpression;
import com.github.rd4j.djangoishtemplate.RenderContext;
import com.github.rd4j.djangoishtemplate.Template;
import com.github.rd4j.djangoishtemplate.TemplateFragment;
import com.github.rd4j.djangoishtemplate.TemplateParseException;
import com.github.rd4j.djangoishtemplate.lookup.DefaultDefinitionContext;
import com.github.rd4j.writer.HtmlWriter;
import com.github.rd4j.writer.StreamHtmlWriter;

public class TestTemplates {
	static class MapExpressionContext implements ExpressionContext
	{
		final Map<String, Object> map = new HashMap<String, Object>();
		
		public void put(String name, Object value) {
			map.put(name, value);
		}
	}
	
	public static class LookupInMap implements ExpressionParser {
		public ParsedExpression parseExpression(String expression) {
			final String key = expression.trim();
			
			return new ParsedExpression() {
				public Object get(ExpressionContext context) {
					MapExpressionContext map = (MapExpressionContext)context;
					
					return map.map.get(key);
				}

				public void set(ExpressionContext context, Object value) {
					MapExpressionContext map = (MapExpressionContext)context;
					
					map.put(key, value);
				}
			};
		}

		public ExpressionContext createContext() {
			return new MapExpressionContext();
		}
	};

	ExpressionParser parser = new LookupInMap();
	ExpressionContext map = parser.createContext();

	public TestTemplates ()
	{
		map.put("testValue", "alpha");
		map.put("indexValue", "");
		map.put("sampleValues", new String[] {"cat", "dog", "rat"});
		map.put("trueProperty", true);
		map.put("falseProperty", false);
	}

	@Test
	public void testSimple() throws Exception {
		Template t = createTemplate("no expansion");
		assertEquals("no expansion", renderTemplate(t, null));
	}

	@Test
	public void testVariable() throws Exception {
		Template t = createTemplate("testValue = {{ testValue }}");
		assertEquals("testValue = alpha", renderTemplate(t, map));
	}
	
	@Test
	public void testForLoop() throws Exception {
		Template t = createTemplate("start {% for indexValue in sampleValues %}[{{ indexValue}}] {% endfor %}end");
		assertEquals("start [cat] [dog] [rat] end", renderTemplate(t, map));
	}
	
	@Test
	public void testIfTrueClause() throws Exception {
		Template t = createTemplate("start {% if trueProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, map));
	}

	@Test
	public void testIfFalseClause() throws Exception {
		Template t = createTemplate("start {% if falseProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  F  end", renderTemplate(t, map));
	}
	
	@Test
	public void testHalfIfTrueClause() throws Exception {
		Template t = createTemplate("start {% if trueProperty %} T {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, map));
	}

	@Test
	public void testHalfIfFalseClause() throws Exception {
		Template t = createTemplate("start {% if falseProperty %} T {% endif %} end");
		assertEquals("start  end", renderTemplate(t, map));
	}
	
	@Test
	public void testBlocks() throws Exception {
		Template parent = createTemplate("start {% block content %} sample text {% endblock content %}{% block final %}*{% endblock final %} end");

		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		resolver.addTemplate("parent", parent);

		Template child = parseTemplate("{% extends parent %} {% block content %} altcontent {% endblock content %}", resolver);
		
		assertEquals("start  altcontent * end", renderTemplate(child, null));
		assertEquals("start  sample text * end", renderTemplate(parent, null));
		
		resolver.addTemplate("child", child);
		Template grandchild = parseTemplate("{% extends child %} {% block final %}!{% endblock final %}", resolver);
		assertEquals("start  altcontent ! end", renderTemplate(grandchild, null));
	}
	
	protected Template createTemplate(String body) {
		return parseTemplate(body, new DefaultDefinitionContext());
	}

	Macro customTextInput = new Macro() {
		public void render(HtmlWriter writer,
				Map<String, ParsedExpression> bindings, TemplateFragment body, RenderContext renderContext) {
			String nameValue = bindings.get("name").get(renderContext.getRoot()).toString();
			String valueValue = bindings.get("value").get(renderContext.getRoot()).toString();
			
			writer.writeRaw("<input name=\"");
			writer.writeRaw(nameValue);
			writer.writeRaw("\" value=\"");
			writer.writeRaw(valueValue);
			writer.writeRaw("\" />");
		}
	};

	Macro wrapWithHr = new Macro() {
		public void render(HtmlWriter writer,
				Map<String, ParsedExpression> bindings, TemplateFragment body, RenderContext renderContext) throws IOException {
			String _count = bindings.get("count").get(renderContext.getRoot()).toString();
			int count = Integer.parseInt(_count);

			for(int i = 0;i<count;i++)
				writer.writeRaw("<hr>");

			body.render(writer, renderContext);
			
			for(int i = 0;i<count;i++)
				writer.writeRaw("<hr>");
		}
	};
	
	
	@Test
	public void testBodylessMacro() throws Exception {
		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		resolver.addMacro("customTextInput", customTextInput);

		Template t = parseTemplate("start {% customTextInput name=\"firstName\" value=\"smith\" %} end", resolver);
		assertEquals("start <input name=\"firstName\" value=\"smith\" /> end", renderTemplate(t, null));
	}
	
	@Test
	public void testMacroWithBody() throws Exception {
		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		resolver.addMacro("wrapWithHr", wrapWithHr);

		Template t = parseTemplate("start {% wrapWithHr count=\"3\" begin %} rock {% endblock %} end", resolver);
		assertEquals("start <hr><hr><hr> rock <hr><hr><hr> end", renderTemplate(t, null));
	}

	@Test
	public void testNestedBlocks() throws Exception {
		final Map<String, Template> defs = new HashMap<String,Template>();

		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		
		Template parent = parseTemplate("start {% block content %} sample text {% endblock content %} end", resolver);
		resolver.addTemplate("parent", parent);

		Template child = parseTemplate("{% extends parent %} {% block content %} 1 {% block inner %} 2 {% endblock inner %}{% endblock content %}", resolver);
		resolver.addTemplate("child", child);

		Template grandchild = parseTemplate("{% extends child %} {% block inner %} 3 {% endblock inner %}", resolver);

		assertEquals("start  1  2  end", renderTemplate(child, null));
		assertEquals("start  1  3  end", renderTemplate(grandchild, null));
	}
	
	@Test
	public void testParseException() throws Exception {
		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		
		try { 
			parseTemplate("start {% unknown %} sample text", resolver);
		} catch (TemplateParseException ex) {
			assertEquals(1, ex.getLineNumber());
			// you know, I don't actually care what the column # is as long as it's close
			assertTrue(7<= ex.getColumn() && ex.getColumn() <= 19);
			ex.printStackTrace();
			return;
		}
		
		fail();
	}
	
	String renderTemplate(Template tf, ExpressionContext root) throws Exception {
		StringWriter w = new StringWriter();
		HtmlWriter hw = new StreamHtmlWriter(w);
		tf.renderTemplate(hw, root);
		return w.toString();
	}
	
	public Template parseTemplate(String body, DefaultDefinitionContext resolver) {
		StringReader reader = new StringReader(body);
		return new Template("<string>", reader, resolver, new LookupInMap());
	}
}
