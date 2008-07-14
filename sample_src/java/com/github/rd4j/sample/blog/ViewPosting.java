package com.github.rd4j.sample.blog;

import com.github.rd4j.DjangoTemplateResolution;
import com.github.rd4j.Exposed;
import com.github.rd4j.Resolution;
import com.github.rd4j.sample.blog.domain.Post;

public class ViewPosting {
	@InjectThis
	public DbSession dbSession;
	
	@Exposed(url="/view/(?P<naturalKey>.*)", action="")
	public Resolution view(String naturalKey) {
		Post post = dbSession.findPostByNaturalKey(naturalKey);
		return new DjangoTemplateResolution("viewPosting.rd4j").addAttribute("post", post);
	}
}
