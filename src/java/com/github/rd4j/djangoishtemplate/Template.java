package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.rd4j.djangoishtemplate.Tokenizer.BlockToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.ExtendsToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.ForToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.IfToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.MacroToken;
import com.github.rd4j.djangoishtemplate.lookup.DefinitionContext;
import com.github.rd4j.writer.HtmlWriter;

public class Template {
	final protected boolean isExtension;
	
	final protected TemplateFragment rootFragment;
	final protected Map<String, TemplateFragment> blocks;
	final DefinitionContext context;
	final String name;
	
	protected TemplateFragment parseUntil(Tokenizer t, TokenType terminator) {
		TemplateFragment fragment = parseUntilEndOfBlock(t, Collections.singleton(terminator));		

		// consume the expected token
		Token tk = t.getNextToken();
		assert(tk.type == terminator);
		
		return fragment;
	}
	
	protected TemplateFragment parseUntilEndOfBlock(Tokenizer t, Collection<TokenType> expectedTerminator) throws TemplateParseException {
		FragmentSequence seq = new FragmentSequence();
		
		while(true) {
			Token tk = t.getNextToken();
			
			if(tk.type == TokenType.COMMENT) {
				// ignore comments
			} else if(tk.type == TokenType.RAWTEXT ) {
				seq.addBlock(new StaticFragment(t.getText()));
			} else if(tk.type == TokenType.VARIABLE) {
				seq.addBlock(new ExpressionFragment(t.getText()));
			} else if(tk.type == TokenType.ENDIF ||
					tk.type == TokenType.ELSE ||
					tk.type == TokenType.ENDBLOCK || 
					tk.type == TokenType.ENDFOR ||
					tk.type == TokenType.EOF ) {
				
				if(!expectedTerminator.contains(tk.type)) {
					throw new TemplateParseException(name, tk.startLine, tk.startColumn, "unexpected "+tk.type+", expected one of "+expectedTerminator);
				}
				
				t.pushNextToken(tk);

				return seq;
				
			} else if(tk.type == TokenType.IF) {
				IfToken _tk = (IfToken)tk;

				TemplateFragment trueClause = parseUntilEndOfBlock(t, Arrays.asList(TokenType.ELSE, TokenType.ENDIF));
				
				Token nextToken = t.getNextToken();
				
				TemplateFragment falseClause;
				if(nextToken.type == TokenType.ELSE) {
					falseClause = parseUntil(t, TokenType.ENDIF);
				} else {
					assert(nextToken.type == TokenType.ENDIF);
					falseClause = new NopFragment();
				}
				
				seq.addBlock(new IfFragment(_tk.expr, trueClause, falseClause));

			} else if(tk.type == TokenType.BLOCK) {
				BlockToken _tk = (BlockToken)tk;
				TemplateFragment body = parseUntil(t, TokenType.ENDBLOCK);
				seq.addBlock(new BlockFragment(_tk.name));
				
				blocks.put(_tk.name, body);

			} else if(tk.type == TokenType.FOR) { 
				ForToken _tk = (ForToken)tk;
				TemplateFragment body = parseUntil(t, TokenType.ENDFOR);
				seq.addBlock(new ForFragment(_tk.elmExpr, _tk.colExpr, body));
			} else if(tk.type == TokenType.MACRO) {
				MacroToken _tk = (MacroToken)tk;
				Macro macro = context.getMacro(_tk.macroName);
				TemplateFragment body;
				if(_tk.hasBodyFlag) {
					body = parseUntil(t, TokenType.ENDBLOCK);
				} else {
					body = new NopFragment();
				}
				MacroFragment fragment = new MacroFragment(_tk.args, body, macro);
				seq.addBlock(fragment);
			} else {
				throw new RuntimeException("unknown token "+tk.type+"("+tk.image+")");
			}
		}
	}

	public TemplateFragment getBlock(String name) {
		return blocks.get(name);
	}
	
	public Template(String name, Reader templateSource, DefinitionContext context) throws TemplateParseException {
		this.name = name;
		this.context = context;
		this.blocks = new HashMap<String, TemplateFragment>();

		Tokenizer t = new Tokenizer(name, templateSource);

		Token lookahead = t.getNextToken();
		if(lookahead.type == TokenType.EXTENDS) {
			isExtension = true;
			ExtendsToken _tk = (ExtendsToken)lookahead;

			// HACK
			String templateName = _tk.name.replace("\"", "");
			Template parentTemplate = context.getTemplate(templateName);
			this.blocks.putAll(parentTemplate.blocks);
			this.rootFragment = parentTemplate.rootFragment;
			
			// has side effect of populating map
			parseUntilEndOfBlock(t, Collections.singleton(TokenType.EOF));

		} else {
			isExtension = false;
			t.pushNextToken(lookahead);
			this.rootFragment = parseUntilEndOfBlock(t, Collections.singleton(TokenType.EOF));
		}
	}

	public void renderTemplate(HtmlWriter w, Object root) throws IOException {
		RenderContext renderContext = new RenderContext(this, root);
		rootFragment.render(w, renderContext);
	}
}
