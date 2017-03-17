package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
	private String username;
	private String fname;
	private String lname;
	private String password;
	private String URL;
	private List<Event> feed;
	private Set<String> following;
	private Set<String> followers;

	public User() {
		following = new HashSet<>();
		followers = new HashSet<>();
		feed = new ArrayList<>();
	}

	
	//GETTERS
	public String getUsername() {
		return username;
	}
	
	public String getFullname(){
		return fname + " "+lname;
	}
	
	public String getImage(){
		return URL;
	}
	
	public String getLName(){
		return lname;
	}
	
	public String getFName() {
		return fname;
	}

	public String getPassword() {
		return password;
	}

	public List<Event> getFeed() {
		return feed;
	}

	public Set<String> getFollowing() {
		return following;
	}
	
	public Set<String> getFollowers(){
		return followers;
	}
	
	//SETTERS
	public void setFName(String fname){
		this.fname = fname;
	}
	
	public void setImage(String URL){
		this.URL = URL;
	}
	
	public void setLName(String lname){
		this.lname = lname;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFeed(List<Event> feed) {
		this.feed = feed;
	}

	public void setFollowing(Set<String> following) {
		this.following = following;
	}

	public void setFollowers(Set<String> set) {
		this.followers = set;
	}
	
	//METHODS
	public void addFollower(String username){
		followers.add(username);
	}
	
	public void addFollowing(String username){
		following.add(username);
	}
	
	public void addEvent(Event event){
		feed.add(event);
	}
}
