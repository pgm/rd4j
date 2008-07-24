package com.github.rd4j.djangoishtemplate.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.djangoishtemplate.Macro;
import com.github.rd4j.djangoishtemplate.RenderContext;
import com.github.rd4j.djangoishtemplate.Template;
import com.github.rd4j.djangoishtemplate.TemplateFragment;
import com.github.rd4j.djangoishtemplate.lookup.DefaultDefinitionContext;
import com.github.rd4j.expr.Expression;
import com.github.rd4j.writer.HtmlWriter;
import com.github.rd4j.writer.StreamHtmlWriter;

public class TestTemplates {
	@Test
	public void testSimple() throws Exception {
		Template t = createTemplate("no expansion");
		assertEquals("no expansion", renderTemplate(t, null));
	}

	@Test
	public void testVariable() throws Exception {
		Template t = createTemplate("testValue = {{ testValue }}");
		assertEquals("testValue = alpha", renderTemplate(t, this));
	}
	
	@Test
	public void testForLoop() throws Exception {
		Template t = createTemplate("start {% for indexValue in sampleValues %}[{{ indexValue}}] {% endfor %}end");
		assertEquals("start [cat] [dog] [rat] end", renderTemplate(t, this));
	}
	
	@Test
	public void testIfTrueClause() throws Exception {
		Template t = createTemplate("start {% if trueProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, this));
	}

	@Test
	public void testIfFalseClause() throws Exception {
		Template t = createTemplate("start {% if falseProperty %} T {% else %} F {% endif %} end");
		assertEquals("start  F  end", renderTemplate(t, this));
	}
	
	@Test
	public void testHalfIfTrueClause() throws Exception {
		Template t = createTemplate("start {% if trueProperty %} T {% endif %} end");
		assertEquals("start  T  end", renderTemplate(t, this));
	}

	@Test
	public void testHalfIfFalseClause() throws Exception {
		Template t = createTemplate("start {% if falseProperty %} T {% endif %} end");
		assertEquals("start  end", renderTemplate(t, this));
	}
	
	@Test
	public void testBlocks() throws Exception {
		Template parent = createTemplate("start {% block content %} sample text {% endblock content %}{% block final %}*{% endblock final %} end");

		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		resolver.addTemplate("parent", parent);

		Template child = new Template("{% extends parent %} {% block content %} altcontent {% endblock content %}", resolver);
		
		assertEquals("start  altcontent * end", renderTemplate(child, this));
		assertEquals("start  sample text * end", renderTemplate(parent, this));
		
		resolver.addTemplate("child", child);
		Template grandchild = new Template("{% extends child %} {% block final %}!{% endblock final %}", resolver);
		assertEquals("start  altcontent ! end", renderTemplate(grandchild, this));
	}
	
	protected Template createTemplate(String body) {
		return new Template(body, new DefaultDefinitionContext());
	}

	Macro customTextInput = new Macro() {
		public void render(HtmlWriter writer,
				Map<String, Expression> bindings, TemplateFragment body, RenderContext renderContext) {
			String nameValue = bindings.get("name").eval(renderContext.getRoot()).get().toString();
			String valueValue = bindings.get("value").eval(renderContext.getRoot()).get().toString();
			
			writer.writeRaw("<input name=\"");
			writer.writeRaw(nameValue);
			writer.writeRaw("\" value=\"");
			writer.writeRaw(valueValue);
			writer.writeRaw("\" />");
		}
	};

	Macro wrapWithHr = new Macro() {
		public void render(HtmlWriter writer,
				Map<String, Expression> bindings, TemplateFragment body, RenderContext renderContext) throws IOException {
			String _count = bindings.get("count").eval(renderContext.getRoot()).get().toString();
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

		Template t = new Template("start {% customTextInput name=\"firstName\" value=\"smith\" %} end", resolver);
		assertEquals("start <input name=\"firstName\" value=\"smith\" /> end", renderTemplate(t, this));
	}
	
	@Test
	public void testMacroWithBody() throws Exception {
		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		resolver.addMacro("wrapWithHr", wrapWithHr);

		Template t = new Template("start {% wrapWithHr count=\"3\" begin %} rock {% endblock %} end", resolver);
		assertEquals("start <hr><hr><hr> rock <hr><hr><hr> end", renderTemplate(t, this));
	}

	@Test
	public void testNestedBlocks() throws Exception {
		final Map<String, Template> defs = new HashMap<String,Template>();

		DefaultDefinitionContext resolver = new DefaultDefinitionContext();
		
		Template parent = new Template("start {% block content %} sample text {% endblock content %} end", resolver);
		resolver.addTemplate("parent", parent);

		Template child = new Template("{% extends parent %} {% block content %} 1 {% block inner %} 2 {% endblock inner %}{% endblock content %}", resolver);
		resolver.addTemplate("child", child);

		Template grandchild = new Template("{% extends child %} {% block inner %} 3 {% endblock inner %}", resolver);

		assertEquals("start  1  2  end", renderTemplate(child, this));
		assertEquals("start  1  3  end", renderTemplate(grandchild, this));
	}
	
	String renderTemplate(Template tf, Object root) throws Exception {
		StringWriter w = new StringWriter();
		HtmlWriter hw = new StreamHtmlWriter(w);
		tf.renderTemplate(hw, root);
		return w.toString();
	}
	
	public String testValue = "alpha";
	
	public String indexValue = "";
	public String [] sampleValues = new String[] {"cat", "dog", "rat"};
	
	public boolean trueProperty = true;
	public boolean falseProperty = false;
}
