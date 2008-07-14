package com.github.rd4j.sample.blog.domain;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post implements Entity {
	String id;
	String title;
	String body;
	String naturalKey;
	Date postedDate;
	User user;
	
	public String getNaturalKey() {
		return naturalKey;
	}
	public void setNaturalKey(String naturalKey) {
		this.naturalKey = naturalKey;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	
	public void computeAndAssignNaturalName() {
		Pattern p = Pattern.compile("\\W+");
		Matcher m = p.matcher(title);
		
		naturalKey = m.replaceAll("-").toLowerCase();
	}
}
