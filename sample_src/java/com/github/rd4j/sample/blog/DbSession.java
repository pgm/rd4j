package com.github.rd4j.sample.blog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rd4j.sample.blog.domain.Entity;
import com.github.rd4j.sample.blog.domain.Post;
import com.github.rd4j.sample.blog.domain.User;

public class DbSession {
	Map<String, Map<String, Entity>> store = new HashMap<String, Map<String, Entity>>();
	
	int nextId = 1;
	
	public String save(Entity p) {
		String id = Integer.toString(nextId);
		nextId ++;
		
		p.setId(id);
		Map<String, Entity> ps = store.get(p.getClass().getName());
		if(ps == null) {
			ps = new HashMap<String, Entity>();
			store.put(p.getClass().getName(), ps);
		}
		ps.put(p.getId(), p);
		
		return id;
	}
	
	public DbSession() {
		User u = new User();
		u.setName("Jon Doe");
		u.setEmail("jdoe@sample.com");
		save(u);
		
		Post p = new Post();
		String body = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		p.setBody(body);
		p.setTitle("The standard Lorem Ipsum passage, used since the 1500s");
		p.setPostedDate(new Date());
		p.setUser(u);
		p.computeAndAssignNaturalName();
		save(p);
		
		p = new Post();
		p.setBody("Integer ullamcorper urna. Vestibulum sed libero. Pellentesque bibendum elit in tellus. Nulla faucibus sapien at pede. Phasellus ac neque. Nulla vel tellus in velit gravida rhoncus. Vivamus lorem. Duis vel quam. Curabitur malesuada blandit lorem. Quisque gravida tristique erat. Nunc id est. Maecenas ac eros ac nibh vestibulum sollicitudin. Vestibulum erat est, elementum tincidunt, rutrum pharetra, facilisis sed, purus. Nulla vulputate ligula. Curabitur luctus elit sit amet arcu. Donec et sapien sagittis neque ultrices tincidunt. Maecenas massa orci, egestas tincidunt, consequat vitae, commodo a, dolor. Aliquam ante tellus, pretium eget, posuere et, porta vel, augue.");
		p.setTitle("Curabitur malesuada blandit lorem");
		p.setPostedDate(new Date());
		p.setUser(u);
		p.computeAndAssignNaturalName();
		save(p);

		p = new Post();
		p.setBody("Etiam nibh nisi, pharetra at, semper sed, vestibulum vel, massa. Cras sed arcu at quam commodo placerat. Duis pellentesque. Suspendisse purus metus, dapibus lobortis, lacinia in, aliquam ut, odio. Vestibulum viverra pede at odio. Cras sodales, lectus congue dapibus tincidunt, urna risus aliquet lectus, condimentum faucibus tortor pede vitae mauris. Aliquam a velit. Nulla facilisi. Maecenas molestie erat et purus lacinia tempor. Curabitur ante lorem, molestie eu, consequat sed, viverra a, pede. Suspendisse condimentum dolor at tortor. ");
		p.setTitle("Curabitur ante lorem, molestie eu!");
		p.setPostedDate(new Date());
		p.setUser(u);
		p.computeAndAssignNaturalName();
		save(p);
	}
	
	public Post findPostByNaturalKey(String key) {
		List<Post> posts = getAllPosts();
		for(Post p:posts) {
			if(p.getNaturalKey().equals(key))
				return p;
		}
		return null;
	}
	
	public List<Post> getAllPosts() {
		List<Post> sortedPosts = new ArrayList<Post>();
		for(Entity e:store.get(Post.class.getName()).values()) {
			sortedPosts.add((Post)e);
		}
		Collections.sort(sortedPosts, new Comparator<Post>() {
			public int compare(Post arg0, Post arg1) {
				return -((Post)arg0).getPostedDate().compareTo(((Post)arg1).getPostedDate());
			}
		});
		
		return sortedPosts;
	}
}

