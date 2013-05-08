package edu.berkeley.cs160.stackunderflow.photofocus;

public class Comment {
	private int userId;
	private String name;
	private String body;
	
	public Comment(int id, String n, String b) {
		userId = id;
		name = n;
		body = b;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String toString() {
		return name + ": " + body;
	}
}
