package com.example.constbuildertest;


import java.util.ArrayList;

public class Constellation {
	private String id;
	private ArrayList<IntPair> stars = new ArrayList<IntPair>();
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
	public ArrayList<IntPair> getStars() {return stars;}
	public IntPair getStar(int index) {return stars.get(index);}
	public int numStars() {return stars.size();}
	public ArrayList<IntPair> getLines() {return lines;}
	public IntPair getLine(int index) {return lines.get(index);}
	public int numLines() {return lines.size();}
	
	//Set functions
	public void setId(String aid) {id=aid;}
	public void setStar(int index, IntPair astar) { stars.set(index, astar);}
	public void setLine(int index, IntPair aline) { lines.set(index, aline);}
	
	//Add and Delete functions
	public void addStar(IntPair astar) {stars.add(astar);}
	public void deleteStar(int index) {
		IntPair ip;
		for (int i=0; i<lines.size(); ++i) {
			ip = lines.get(i);
			if (ip.first==index || ip.second==index) {
				lines.remove(i);
				--i;
			}
		}
		stars.remove(index);
		for (int i=0; i<lines.size(); ++i) {
			ip = lines.get(i);
			if (ip.first>index) {ip.first-=1;}
			if (ip.second>index) {ip.second-=1;}
		}
	}
	public void addLine(IntPair aline) {lines.add(aline);}
	public void deleteLine(int index) {lines.remove(index);}
	
	//Draw function
	public void draw() {
		//Need to implement.
	}
}