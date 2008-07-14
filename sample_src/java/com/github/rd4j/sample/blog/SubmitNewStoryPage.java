package com.github.rd4j.sample.blog;

import java.util.Date;

import com.github.rd4j.Exposed;
import com.github.rd4j.RedirectResolution;
import com.github.rd4j.Resolution;
import com.github.rd4j.sample.blog.domain.Post;
import com.github.rd4j.sample.blog.domain.User;

public class SubmitNewStoryPage {
	@InjectThis
	public DbSession dbSession;
	
	@Exposed(url="/submitNewStory", action="save")
	public Resolution saveStory(String title, String body) {

		User u = new User();
		u.setName("joe");
		
		Post p = new Post();
		p.setTitle(title);
		p.setBody(body);
		p.setPostedDate(new Date());
		p.setUser(u);
		p.computeAndAssignNaturalName();
		
		dbSession.save(p);

		return new RedirectResolution("/");
	}

	@Exposed(url="/submitNewStory", action="cancel")
	public Resolution cancel() {
		return new RedirectResolution("/");
	}
}
