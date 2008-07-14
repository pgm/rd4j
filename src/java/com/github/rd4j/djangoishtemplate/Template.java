package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.rd4j.djangoishtemplate.Tokenizer.BlockToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.ExtendsToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.ForToken;
import com.github.rd4j.djangoishtemplate.Tokenizer.IfToken;

public class Template {

	protected TemplateFragment parseUntil(Tokenizer t, TokenType terminator) {
		TemplateFragment fragment = parseUntilEndOfBlock(t, Collections.singleton(terminator));		

		// consume the expected token
		Token tk = t.getNextToken();
		assert(tk.type == terminator);
		
		return fragment;
	}
	
	protected TemplateFragment parseUntilEndOfBlock(Tokenizer t, Collection<TokenType> expectedTerminator) {
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
					throw new RuntimeException("unexpected "+tk.type+", expected one of "+expectedTerminator);
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
				/*
				if(isExtension) {
					if(!blocks.containsKey(_tk.name)) {
						throw new Exception("");
					}
				} else {
					if(!blocks.containsKey(_tk.name)) {
						
					}
				}*/
				
				blocks.put(_tk.name, body);

			} else if(tk.type == TokenType.FOR) { 
				ForToken _tk = (ForToken)tk;
				TemplateFragment body = parseUntil(t, TokenType.ENDFOR);
				seq.addBlock(new ForFragment(_tk.elmExpr, _tk.colExpr, body));
			} else {
				throw new RuntimeException("unknown token "+tk.type+"("+tk.image+")");
			}
		}
	}

	final boolean isExtension;
	
	final TemplateFragment rootFragment;
//	final Map<String, Template> definitions;
	final Map<String, TemplateFragment> blocks;
	
	public TemplateFragment getBlock(String name) {
		return blocks.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public Template(String body) {
		this(body, new TemplateResolver() {

			public Template findTemplate(String name) {
				throw new UnsupportedOperationException("no "+name);
			} 
			
		});
	}
	
	public Template(String body, TemplateResolver resolver)  {
		this.blocks = new HashMap<String, TemplateFragment>();
//		this.definitions = definitions;

		Tokenizer t = new Tokenizer(new StringReader(body));

		Token lookahead = t.getNextToken();
		if(lookahead.type == TokenType.EXTENDS) {
			isExtension = true;
			ExtendsToken _tk = (ExtendsToken)lookahead;

			// HACK
			Template parentTemplate = resolver.findTemplate(_tk.name.replace("\"", ""));
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

	public void renderTemplate(Writer w, Object root) throws IOException {
		RenderContext renderContext = new RenderContext(this, root);
		rootFragment.render(w, renderContext);
	}
}
