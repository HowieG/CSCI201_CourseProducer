package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataStorage {
	//maps a username to a user object
	protected Map<String, User> usersMap;
	//maps a movie title to a movie object
	protected Map<String, Movie> moviesMap;
	//maps a case insensitive username to a list of case sensitive matches
	private Map<String, Set<User>> usernameToUsers;
	//maps a username to a list of followers
	private Map<String, Set<String>> usernameToFollowers;
	//maps an actor to a list of movies
	private Map<String, Set<Movie>> actorToMovies;
	//maps a genre to a list of movies
	private Map<String, Set<Movie>> genreToMovies;
	//maps a case insensitive movie title to a list of case sensitive matches
	private Map<String, Set<Movie>> titleToMovies;
	//genres
	private List<String> genresList;
	//actions
	private List<String> actionsList;

	private Document doc;
	
	//for strings used more than once, a constant is used instead
	protected static final String genre = "genre";
	protected static final String title = "title";
	protected static final String actor = "actor";
	
	private static final String actors = "actors";
	private static final String writers = "writers";
	private static final String rating = "rating";
	private static final String action = "action";
	private static final String username = "username";
	private static final String feed = "feed";
	private static final String following = "following";
	
	public DataStorage(String filepath){
		usersMap = new HashMap<>();
		moviesMap = new HashMap<>();
		genresList = new ArrayList<>();
		actionsList = new ArrayList<>();
		usernameToUsers = new HashMap<>();
		usernameToFollowers = new HashMap<>();
		actorToMovies = new HashMap<>();
		genreToMovies = new HashMap<>();
		titleToMovies = new HashMap<>();
		

		try{
			//create the document object and parse it
			DocumentBuilder dBuilder= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(new File(filepath));
			parse();
			
			for (User user : usersMap.values()) {
				Set<String> followers = usernameToFollowers.get(user.getUsername());
				if (followers != null) {user.setFollowers(followers);}
			}
		}
		catch (SAXException | IOException | ParserConfigurationException e){
			System.out.println(e.getMessage());
		}
		
	}
	
	//search methods
	protected Set<Movie> searchByGenre(String genre){
		return genreToMovies.get(genre.toLowerCase());
	}
	
	protected Set<Movie> searchByTitle(String title){
		return titleToMovies.get(title.toLowerCase());
	}
	
	protected Set<Movie> searchByActor(String actor){
		return actorToMovies.get(actor.toLowerCase());
	}
	
	protected Set<User> searchForUser(String username){
		return usernameToUsers.get(username.toLowerCase());
	}
	
	//takes a NodeList and either the list of actions or list of genres 
	private void addToGenresOrActions(List<String> toAddTo, NodeList startNode){
		//children of the parent tag (either <actions> or <genres>)
		NodeList children = startNode.item(0).getChildNodes();
		//iterate through the children and get their text content, add it to the list
		for (int i = 0; i<children.getLength(); i++){

			if (children.item(i).hasChildNodes()){

				toAddTo.add(children.item(i).getFirstChild().getTextContent());
			}
		}
	}
	
	private void parse(){
		
		NodeList genresNodeList = doc.getElementsByTagName("genres");
		NodeList actionsNodeList = doc.getElementsByTagName("actions");
		NodeList moviesNodeList = doc.getElementsByTagName("movies");
		NodeList usersNodeList = doc.getElementsByTagName("users");
		//parsing actions and genres
		addToGenresOrActions(genresList, genresNodeList);
		addToGenresOrActions(actionsList, actionsNodeList);
		//parsing movies and users (parsing movies first so we will have the movie objects ready when parsing a user's feed)
		parseObjects(moviesNodeList, true);
		parseObjects(usersNodeList, false);
	}
	
	//the same logic is used for iterating through movie and user nodes. So, we pass in a boolean to determine the helper method to call
	private void parseObjects(NodeList rootNode, Boolean isMovie){
		NodeList children = rootNode.item(0).getChildNodes();
		
		for (int i = 0; i<children.getLength(); i++){
			Node node = children.item(i);
			//some of the elements show up as text elements, so we need this check before we choose to parse
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				if (isMovie) parseMovie(node);
				else parseUser(node);
			}
		}
	}
	
	//PARSE MOVIES
	//parsing one movie object
	private void parseMovie(Node rootNode){
		//get the fields of the movie
		NodeList movieFields = rootNode.getChildNodes();
		Movie movie = new Movie();
		
		for (int i = 0; i<movieFields.getLength(); i++){
			//get the current field of the movie
			Node movieField = movieFields.item(i);
			String nodeName = movieField.getNodeName();
			
			switch (nodeName){
			
				case "director":
					movie.setDirector(movieField.getFirstChild().getTextContent());
					break;
				case writers:
					//parse the writers in a helper method
					parseMovieSubList(writers, movieField, movie);
					break;
				case "year":
					movie.setYear(Integer.parseInt(movieField.getFirstChild().getTextContent()));
					break;
				case actors:
					//parse the actors in a helper method
					parseMovieSubList(actors, movieField, movie);
					break;
				case genre:
					movie.setGenre(movieField.getFirstChild().getTextContent());
					break;
				case title:
					movie.setTitle(movieField.getFirstChild().getTextContent());
					break;
				case "description":
					movie.setDescription(movieField.getFirstChild().getTextContent());
					break;
				case rating:
					//check to see if there is a non-empty rating value
					if (movieField.getFirstChild() != null) movie.setRating(Double.parseDouble(movieField.getFirstChild().getTextContent()));
					break;
			}
		}
		//add the movie to the movies map
		moviesMap.put(movie.getTitle(), movie);
		//add the movie to the correct genre's set
		addObjectToMap(genreToMovies, movie.getGenre().toLowerCase(), movie);
		//add the movie to the correct title's set
		addObjectToMap(titleToMovies, movie.getTitle().toLowerCase(), movie);
	}
	
	//used to parse actors and writers
	private void parseMovieSubList(String key, Node movieNode, Movie movie){
		NodeList children = movieNode.getChildNodes();
		
		for (int j = 0; j<children.getLength(); j++) {
			
			Node child = children.item(j);
			//if the current child is of the appropriate Node type
			if (child.getNodeType() == Node.ELEMENT_NODE) {
		       
		        String value = children.item(j).getFirstChild().getTextContent();
		        //if we are parsing the actors
				if (key.equals(DataStorage.actors)){
					//add the actor to the movie, then add the movie to the map from actors to movies
					movie.addActor(value);
					addObjectToMap(actorToMovies, value.toLowerCase(), movie);
				}
				//if we are parsing the writers, add the value to the movie's list of writers
				else movie.addWriter(children.item(j).getFirstChild().getTextContent());
		    }
		}
		
	}
	
	//PARSE USERS
	//parse a user object
	private void parseUser(Node rootNode){
		NodeList children = rootNode.getChildNodes();
		User user = new User();
		
		for (int i = 0; i<children.getLength(); i++){
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			
			switch(nodeName){
				case username:
					user.setUsername(child.getFirstChild().getTextContent());
					break;
				case "fname":
					user.setFName(child.getFirstChild().getTextContent());
					break;
				case "lname":
					user.setLName(child.getFirstChild().getTextContent());
					break;
				case "password":
					user.setPassword(child.getFirstChild().getTextContent());
					break;
				//parse the following list
				case following:
					parseUserSubList(following, child, user);
					break;
				//parse the feed
				case feed:
					parseUserSubList(feed, child, user);
					break;
			}
		}
		//add the user to the users map
		usersMap.put(user.getUsername(), user);
		//add the user with their lowercased username to the usernameToUsers map
		addObjectToMap(usernameToUsers, user.getUsername().toLowerCase(), user);
	}
	
	//used to parse following and feed
	private void parseUserSubList(String key, Node userNode, User user){
		NodeList following = userNode.getChildNodes();
		
		for (int j = 0; j<following.getLength(); j++) {
			
			Node node = following.item(j);
			//if the current child is of the appropriate Node type
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				//if we are parsing the feed, parse this event
		        if (key.equals(feed)) parseEvent(user, node);
		        //else add the username to the user's following set, then add the relationship to the usernameToFollowers map
		        else{
		        	String username = following.item(j).getFirstChild().getTextContent();
		        	user.addFollowing(username);
		        	addObjectToMap(usernameToFollowers, username, user.getUsername());
		        }
				
		    }
			
		}
	}
	
	//parse an event object
	private void parseEvent(User user, Node eventNode){
		NodeList eventFields = eventNode.getChildNodes();
		Event event = new Event();
		event.setUsername(user.getUsername());
		
		for (int i = 0; i<eventFields.getLength(); i++){
			Node eventField = eventFields.item(i);
			String nodeName = eventField.getNodeName();
			
			switch(nodeName){
				//set the movie object of the event.
				//this is why in parse() we made sure to parse the movies node list before the users node list: so this movie value wouldn't be null
				case "movie":
					event.setMovie(moviesMap.get(eventField.getFirstChild().getTextContent()));
					break;
				case action:
					event.setAction(eventField.getFirstChild().getTextContent());
					break;
				case rating:
					if (eventField.getFirstChild() != null) event.setRating(Double.parseDouble(eventField.getFirstChild().getTextContent()));
					break;
			}
		}
		//add the event to the user
		user.addEvent(event);
	}
	
	//SHARED HELPER METHODS
	
	//more generics!! Yay!!!! They are so helpful in times like these!!!
	//adds a new object to the appropriate set
	private <T> void addObjectToMap(Map<String, Set<T>> map, String key, T object){
		//if the map already contains the provided key, retrieve the value (which is a set) and add the new object
		if (map.containsKey(key)) map.get(key).add(object);
		//else create a new set with the object, and add the provided key and created set to the map
		else{
			Set<T> objects = new HashSet<>();
			objects.add(object);
			map.put(key, objects);
		}
	}
}