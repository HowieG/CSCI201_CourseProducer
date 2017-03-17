package data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
//extends DataParsing class which holds on the data structures and parses the xml file
public class DataStorage extends DataParsing{

	private User loggedInUser;
	//class with methods to alter the XML file
	private DataSaving dataSaving;
	
	public DataStorage(String filepath) throws CinemateException{
		super(filepath);
		dataSaving = new DataSaving(doc, filepath);
	}
	
	//search methods
	public Set<Movie> searchByGenre(String genre){
		return genreToMovies.get(genre.toLowerCase());
	}
	
	public Set<Movie> searchByTitle(String title){
		return titleToMovies.get(title.toLowerCase());
	}
	
	public Set<Movie> searchByActor(String actor){
		return actorToMovies.get(actor.toLowerCase());
	}
	
	//retrieve all users with matching username, fname or lname
	public Set<User> searchForUser(String username){
		Set<User> userSets = new HashSet<>();
		Set<User> usernames = usernameToUsers.get(username.toLowerCase());
		Set<User> fnames = firstNameToUsers.get(username.toLowerCase());
		Set<User> lnames = lastNameToUsers.get(username.toLowerCase());
		
		if (usernames != null) userSets.addAll(usernames);
		if(fnames != null) userSets.addAll(fnames);
		if (lnames != null) userSets.addAll(lnames);
		
		return userSets;
	}
	
	//getters
	public User getUser(String usernanme){
		return usersMap.get(usernanme);
	}
	
	public User getLoggedInUser(){
		return loggedInUser;
	}
	
	public Movie getMovie(String title){
		return moviesMap.get(title);
	}
	
	//setters
	public void setLoggedInUser(String username){
		loggedInUser = usersMap.get(username);
		dataSaving.setLoggedInUser(loggedInUser);
	}
	
	//validation methods
	
	//check if a username is valid
	public Boolean validUsername(String username){
		return usersMap.containsKey(username);
	}
	//check if a password is correct
	public Boolean correctPassword(String username, String password){
		return usersMap.get(username).getPassword().equals(password);
	}
	
	//modify data methods
	public void addEvent(String actionTaken, String title, Integer ratingGiven){
		//save the newe event to the xml file
		dataSaving.addEvent(actionTaken, title, ratingGiven);
		//create event object and add it to logged in user's feed
		Event event = new Event();
		event.setAction(actionTaken);
		event.setMovie(moviesMap.get(title));
		
		if (ratingGiven != null){
			event.setRating(ratingGiven);
		}
		
		event.setUsername(loggedInUser.getUsername());

		if (event.getAction().equals(StringConstants.ACTION_RATED)){
			event.setActionIcon(actionToIcon.get(event.getRatingToDisplay()+StringConstants.ACTION_RATED));
		}
		else{
			event.setActionIcon(actionToIcon.get(event.getAction()));
		}
		
		loggedInUser.getFeed().add(event);
	}
	
	public void removeFollowing(String username){
		dataSaving.removeFollowing(getUser(username));
	}
	
	public void addFollowing(String username){
		dataSaving.addFollowing(getUser(username));
	}
	
	public void addUser(User user){
		//add the new user to all the appropriate maps
		usersMap.put(user.getUsername(), user);
		addObjectToMap(usernameToUsers, user.getUsername().toLowerCase(), user);
		addObjectToMap(firstNameToUsers, user.getFName().toLowerCase(), user);
		addObjectToMap(lastNameToUsers, user.getLName().toLowerCase(), user);

		//save the new user to the xml file
		try {
			dataSaving.createNewUser(user);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.out.println("Exception thrown in creating user "+e.getMessage());
		}
	}
	
	//change the average rating of the movie
	public void changeRating(String title, Integer rating){
		//add a rated event to the logged in user
		addEvent(StringConstants.ACTION_RATED, title, rating);
		//update the rating of the movie object
		Movie movie = moviesMap.get(title);
		movie.incrementRatingCount();
		movie.updateRatingTotal(rating);
		//save the rating changes to the xml file
		dataSaving.changeRating(title, movie);
	}

}