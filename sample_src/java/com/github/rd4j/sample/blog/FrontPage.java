package com.github.rd4j.sample.blog;

import com.github.rd4j.DjangoTemplateResolution;
import com.github.rd4j.Exposed;
import com.github.rd4j.Resolution;

public class FrontPage {
	@InjectThis
	public DbSession session;
	
	@Exposed(url="/", action="")
	public Resolution defaultHandler() {
		return new DjangoTemplateResolution("frontPage.rd4j")
			.addAttribute("posts", session.getAllPosts());
	}

}
