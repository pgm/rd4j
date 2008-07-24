package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.rd4j.writer.HtmlWriter;

public class FragmentSequence implements TemplateFragment {
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		for(Block block : blocks) {
			block.fragment.render(w, renderContext);
		}
	}
	
	final private List<Block> blocks = new ArrayList<Block>();
	
	static class Block {
		public final String name;
		public final TemplateFragment fragment;
		
		public Block(String name, TemplateFragment fragment) {
			super();
			this.name = name;
			this.fragment = fragment;
		}
	}

	public void addBlock(TemplateFragment fragment) {
		addBlock(null, fragment);
	}
	
	public void addBlock(String name, TemplateFragment fragment) {
		this.blocks.add(new Block(name, fragment));
	}
}
