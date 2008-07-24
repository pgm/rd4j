package com.github.rd4j.sample.blog;

import com.github.rd4j.DjangoTemplateResolutionFactory;
import com.github.rd4j.Exposed;
import com.github.rd4j.Resolution;
import com.github.rd4j.sample.blog.domain.Post;

public class ViewPosting {
	@InjectThis
	public DbSession dbSession;
	@InjectThis
	public DjangoTemplateResolutionFactory templateResolutions;
	
	@Exposed(url="/view/(?P<naturalKey>.*)", action="")
	public Resolution view(String naturalKey) {
		Post post = dbSession.findPostByNaturalKey(naturalKey);
	
		return templateResolutions.create("viewPosting.rd4j").addAttribute("post", post);
	}
}
