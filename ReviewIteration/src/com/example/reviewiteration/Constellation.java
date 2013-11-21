package com.example.reviewiteration;


import java.util.ArrayList;

//Probably don't need to implement Drawable as this might be the only drawable class.
public class Constellation implements Drawable {
	private String id;
	private ArrayList<FloatPair> stars = new ArrayList<FloatPair>();
	private ArrayList<IntPair> lines = new ArrayList<IntPair>();
	
	//Constructors
	public Constellation() {
		id = "No id";
	}
	public Constellation(String aid) {
		id = aid;
	}
	
	//Get functions
	public String getId(){return id;}
	public ArrayList<FloatPair> getStars() {return stars;}
	public FloatPair getStar(int index) {return stars.get(index);}
	public ArrayList<IntPair> getLines() {return lines;}
	public IntPair getLine(int index) {return lines.get(index);}
	
	//Set functions
	public void setId(String aid) {id=aid;}
	public void setStar(int index, FloatPair astar) { stars.set(index, astar);}
	public void setLine(int index, IntPair aline) { lines.set(index, aline);}
	
	//Add and Delete functions
	public void addStar(FloatPair astar) {stars.add(astar);}
	public void deleteStar(int index) {stars.remove(index);}
	public void addLine(IntPair aline) {lines.add(aline);}
	public void deleteLine(int index) {lines.remove(index);}
	
	//Draw function
	public void draw() {
		//Need to implement.
	}
}
