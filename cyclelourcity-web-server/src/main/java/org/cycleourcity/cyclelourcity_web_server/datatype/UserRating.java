package org.cycleourcity.cyclelourcity_web_server.datatype;

import java.util.ArrayList;
import java.util.List;

public class UserRating {
	private String username;
	private Long userId;
	private List<Long> ratings;
	
	public UserRating(Long userId, String username){
		this.username = username;
		this.userId = userId;
		this.ratings = new ArrayList<Long>();
	}
	
	public void addRating(Long rating){
		this.ratings.add(rating);
	}
	
	public Long getUserId(){
		return userId;
	}
	
	public List<Long> getRatings(){
		return ratings;
	}
	
	public String getUserName(){
		return username;
	}
	
	public String toString(){
		String result = "";
		
		for(Long rating : ratings){
			result += rating.toString() + " | ";
		}
		
		return result; 
	}
	
}
