

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	//Variables
	private String id;
	private String password;
	private ArrayList<String> privateLibrary = new ArrayList<String>();
	private ArrayList<String> publicLibrary = new ArrayList<String>();
	private HashMap<String, Integer> votedStories = new HashMap<String, Integer>();
	
	//Get functions
	public String getId() { return this.id; }
	
	//Add functions
	public void addPrivateStory(String astoryid) { privateLibrary.add(astoryid); }
	public void addPublicStory(String astoryid) { publicLibrary.add(astoryid); }
	
	//Miscellaneous functions
	public void vote(String astoryid, int rating) { votedStories.put(astoryid, rating); }
	public boolean login(String apassword) { return password.equals(apassword); }
}
