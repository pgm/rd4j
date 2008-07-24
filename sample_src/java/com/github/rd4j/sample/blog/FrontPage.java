package com.github.rd4j.sample.blog;

import com.github.rd4j.DjangoTemplateResolution;
import com.github.rd4j.DjangoTemplateResolutionFactory;
import com.github.rd4j.Exposed;
import com.github.rd4j.Resolution;

public class FrontPage {
	@InjectThis
	public DbSession session;
	
	@InjectThis
	public DjangoTemplateResolutionFactory templateResolutions;
	
	@Exposed(url="/", action="")
	public Resolution defaultHandler() {
		return templateResolutions.create("frontPage.rd4j")
			.addAttribute("posts", session.getAllPosts());
	}

}
