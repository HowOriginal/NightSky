package com.example.helloworldapp;

public class Story {
	private String author;
	private String id;
	private String text;
	private String title;
	private int voteCount = 0;
	private int viewCount = 0;
	
	//Constructors
	public Story() {
		this.author = "No Author";
		this.id = "No id";
		this.title = "No title";
		this.text = "This is the default story text.";
	}
	public Story(String aauthor, String aid, String atitle) {
		this.author=aauthor;
		this.id=aid;
		this.title=atitle;
		this.text="This is the default story text.";
	}
	
	//Get functions
	public String getAuthor() { return this.author; }
	public String getId() { return this.id; }
	public String getText() { return this.text; }
	public String getTitle() { return this.title; }
	public int getVoteCount() { return this.voteCount; }
	public int getViewCount() { return this.viewCount; }
	
	//Set functions
	public void setAuthor(String newauthor) {this.author = newauthor;}
	public void setId(String newid) {this.id = newid;}
	public void setText(String newtext) {this.text = newtext;}
	public void setTitle(String newtitle) {this.title = newtitle;}
	public void incrementVote() {this.voteCount++;}
	public void incrementView() {this.viewCount++;}
}
