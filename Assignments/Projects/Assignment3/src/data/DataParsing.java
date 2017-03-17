package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataParsing {
	//maps a username to a user object
	protected Map<String, User> usersMap;
	//maps a movie title to a movie object
	protected Map<String, Movie> moviesMap;
	//maps a case insensitive username to a list of case sensitive matches
	protected Map<String, Set<User>> usernameToUsers;
	protected Map<String, Set<User>> firstNameToUsers;
	protected Map<String, Set<User>> lastNameToUsers;
	//maps a username to a list of followers
	private Map<String, Set<String>> usernameToFollowers;
	//maps an actor to a list of movies
	protected Map<String, Set<Movie>> actorToMovies;
	//maps a genre to a list of movies
	protected Map<String, Set<Movie>> genreToMovies;
	//maps a case insensitive movie title to a list of case sensitive matches
	protected Map<String, Set<Movie>> titleToMovies;
	//genres
	private List<String> genresList;
	//actions
	private List<String> actionsList;
	//maps an action to the icon associated with it
	//unfortunately doing this makes the assumption we only have the actions: rated, liked, disliked and watched
	protected Map<String, String> actionToIcon;
	//lambda that parses the actors when given the appropriate DOM Node
	private BiConsumer<Node, Movie> parseActors;
	//lambda that parses the writers when given the appropriate DOM Node
	private BiConsumer<Node, Movie> parseWriters;
	
	protected Document doc;
	private String filepath;
	
	public DataParsing(String filepath) throws CinemateException{
		this.filepath = filepath;
		usersMap = new HashMap<>();
		moviesMap = new HashMap<>();
		usernameToUsers = new HashMap<>();
		firstNameToUsers = new HashMap<>();
		lastNameToUsers = new HashMap<>();
		usernameToFollowers = new HashMap<>();
		actorToMovies = new HashMap<>();
		genreToMovies = new HashMap<>();
		titleToMovies = new HashMap<>();
		genresList = new ArrayList<>();
		actionsList = new ArrayList<>();
		actionToIcon = new HashMap<>();
		//add the actions and icons to the actionToIconMap
		createActionIconMap();
		//instantiate and define the lambdas
		createLambdas();
		//parse the file
		parseFile();
	}
	
	private void parseFile() throws CinemateException{
		
		try{
			//create the document object and parse it
			DocumentBuilder dBuilder= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(new File(filepath));
			parse();
			
			for (User user : usersMap.values()) {
				Set<String> followers = usernameToFollowers.get(user.getUsername());
				Set<String> following = user.getFollowing();
				
				//check that all usernames in following list are valid
				for (String follow : following){
					if (!usersMap.containsKey(follow)){
						throw new CinemateException("Invalid username in following list");
					}
				}
				
				if (followers != null) {
					user.setFollowers(followers);
				}
			}
		}
		catch (SAXException | IOException | ParserConfigurationException e){
			throw new CinemateException(e.getMessage());
		}
	}
	
	private void createActionIconMap(){
		actionToIcon.put("0"+StringConstants.ACTION_RATED, StringConstants.RATING_0_ICON);
		actionToIcon.put("1"+StringConstants.ACTION_RATED, StringConstants.RATING_1_ICON);
		actionToIcon.put("2"+StringConstants.ACTION_RATED, StringConstants.RATING_2_ICON);
		actionToIcon.put("3"+StringConstants.ACTION_RATED, StringConstants.RATING_3_ICON);
		actionToIcon.put("4"+StringConstants.ACTION_RATED, StringConstants.RATING_4_ICON);
		actionToIcon.put("5"+StringConstants.ACTION_RATED, StringConstants.RATING_5_ICON);
		actionToIcon.put(StringConstants.ACTION_LIKED, StringConstants.LIKED_ICON);
		actionToIcon.put(StringConstants.ACTION_DISLIKED, StringConstants.DISLIKED_ICON);
		actionToIcon.put(StringConstants.ACTION_WATCHED, StringConstants.WATCHED_ICON);
	}
	
	private void createLambdas(){
		
		parseWriters = (node, movie) ->{
			movie.addWriter(node.getFirstChild().getTextContent());
		};
		
		parseActors = (node, movie) ->{
			Actor actor = new Actor();
			
        	NodeList fields = node.getChildNodes();
        	
        	for (int i = 0; i<fields.getLength(); i++){
        		
        		Node field = fields.item(i);
        			String nodeName = field.getNodeName();
        			
        		switch(nodeName){
        			case StringConstants.FNAME:
        				actor.setFName(field.getFirstChild().getTextContent());
        				break;
        			case StringConstants.LNAME:
        				actor.setLName(field.getFirstChild().getTextContent());
        				break;
        			case StringConstants.IMAGE:
        				actor.setImage(field.getFirstChild().getTextContent());
        				break;
        		}
        	}
        	
        	movie.addActor(actor);
			addObjectToMap(actorToMovies, actor.getFullName().toLowerCase(), movie);
		};
	}
	
	private void parse() throws CinemateException{
		
		NodeList genresNodeList = doc.getElementsByTagName(StringConstants.GENRES);
		NodeList actionsNodeList = doc.getElementsByTagName(StringConstants.ACTIONS);
		NodeList moviesNodeList = doc.getElementsByTagName(StringConstants.MOVIES);
		NodeList usersNodeList = doc.getElementsByTagName(StringConstants.USERS);
		//parsing actions and genres
		addToGenresOrActions(genresList, genresNodeList);
		addToGenresOrActions(actionsList, actionsNodeList);
		//parsing movies and users (parsing movies first so we will have the movie objects ready when parsing a user's feed)
		parseObjects(moviesNodeList, true);
		//if there are no movies, throw an exception
		if (moviesMap.isEmpty()){
			throw new CinemateException("No movies found in file.");
		}
		
		parseObjects(usersNodeList, false);
	}
	
	//the same logic is used for iterating through movie and user nodes. So, we pass in a boolean to determine the helper method to call
	private void parseObjects(NodeList nodeList, Boolean isMovie) throws CinemateException{
		NodeList children = nodeList.item(0).getChildNodes();
		
		for (int i = 0; i<children.getLength(); i++){
			Node node = children.item(i);
			//some of the elements show up as text elements, so we need this check before we choose to parse
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				NodeList grandchildren = node.getChildNodes();
				
				if (isMovie){ parseMovie(grandchildren); }
				else { parseUser(grandchildren); }
			}
		}
	}
	
	//takes a NodeList and either the list of actions or list of genres 
	private void addToGenresOrActions(List<String> toAddTo, NodeList nodeList){
		//children of the parent tag (either <actions> or <genres>)
		NodeList children = nodeList.item(0).getChildNodes();
		//iterate through the children and get their text content, add it to the list
		for (int i = 0; i<children.getLength(); i++){

			if (children.item(i).hasChildNodes()){

				toAddTo.add(children.item(i).getFirstChild().getTextContent());
			}
		}
	}
		
	//PARSE MOVIES
	//parsing one movie object
	private void parseMovie(NodeList movieFields) throws CinemateException{
		Movie movie = new Movie();
		
		for (int i = 0; i<movieFields.getLength(); i++){
			//get the current field of the movie
			Node movieField = movieFields.item(i);
			String nodeName = movieField.getNodeName();
			
			switch (nodeName){
			
				case StringConstants.DIRECTOR:
					movie.setDirector(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.WRITERS:
					//parse the writers in a helper method, pass in the parseWriters lambda
					iterateMovieChildren(movieField, movie, parseWriters);
					break;
				case StringConstants.YEAR:
					//try to parse year, if we get NumberFormatException, we know we weren't given an integer
					try{
						movie.setYear(Integer.parseInt(movieField.getFirstChild().getTextContent()));
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie year is not an int value");
					}
					
					break;
				case StringConstants.IMAGE:
					movie.setImage(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.ACTORS:
					//parse the actors in a helper method, pass in the parseActors lambda
					iterateMovieChildren(movieField, movie, parseActors);
					break;
				case StringConstants.GENRE:
					movie.setGenre(movieField.getFirstChild().getTextContent());
					
					if (!genresList.contains(movie.getGenre())){ throw new CinemateException("Movie object with an invalid genre found");}
					
					break;
				case StringConstants.TITLE:
					movie.setTitle(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.DESCRIPTION:
					movie.setDescription(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.RATING_TOTAL:
					//check to see if there is a non-empty rating valued
					try{
						//try to parse rating, if we get NumberFormatException, we know we weren't given a double
						if (movieField.getFirstChild() != null) {
							movie.setRatingTotal(Long.parseLong(movieField.getFirstChild().getTextContent()));
						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie rating is not a double value");
					}
					break;
				case StringConstants.RATING_COUNT:
					//check to see if there is a non-empty rating valued
					try{
						//try to parse rating, if we get NumberFormatException, we know we weren't given a double
						if (movieField.getFirstChild() != null) {
							movie.setRatingCount(Integer.parseInt(movieField.getFirstChild().getTextContent()));
						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie rating is not a double value");
					}
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

	//iterate over either actors or writers
	private void iterateMovieChildren(Node node, Movie m, BiConsumer<Node, Movie> function){
		NodeList children = node.getChildNodes();
		
		for (int j = 0; j<children.getLength(); j++) {
			
			Node child = children.item(j);
			//if the current child is of the appropriate Node type
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				//the passed in lambda will parse the node as either an actor or a writer
				function.accept(child, m);
			}
		}
	}

	//PARSE USERS
	//parse a user object
	private void parseUser(NodeList children) throws CinemateException{

		User user = new User();
		
		for (int i = 0; i<children.getLength(); i++){
			Node child = children.item(i);
			String nodeName = child.getNodeName();
	
			switch(nodeName){
				case StringConstants.USERNAME:
					user.setUsername(child.getFirstChild().getTextContent());
					break;
				case StringConstants.FNAME:
					user.setFName(child.getFirstChild().getTextContent());
					break;
				case StringConstants.LNAME:
					user.setLName(child.getFirstChild().getTextContent());
					break;
				case StringConstants.IMAGE:
					user.setImage(child.getFirstChild().getTextContent());
					break;
				case StringConstants.PASSWORD:
					user.setPassword(child.getFirstChild().getTextContent());
					break;
				//parse the following list
				case StringConstants.FOLLOWING:
					parseUserSubList(StringConstants.FOLLOWING, child, user);
					break;
				//parse the feed
				case StringConstants.FEED:
					parseUserSubList(StringConstants.FEED, child, user);
					break;
			}
		}
		//add the user to the users map
		usersMap.put(user.getUsername(), user);
		//add the user with their lowercased username to the usernameToUsers map
		addObjectToMap(usernameToUsers, user.getUsername().toLowerCase(), user);
		addObjectToMap(firstNameToUsers, user.getFName().toLowerCase(), user);
		addObjectToMap(lastNameToUsers, user.getLName().toLowerCase(), user);
	}
	
	//used to parse following and feed
	private void parseUserSubList(String key, Node userNode, User user) throws CinemateException{
		NodeList following = userNode.getChildNodes();
		
		for (int j = 0; j<following.getLength(); j++) {
			
			Node node = following.item(j);
			//if the current child is of the appropriate Node type
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				//if we are parsing the feed, parse this event
		        if (key.equals(StringConstants.FEED)) {parseEvent(user, node);}
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
	private void parseEvent(User user, Node eventNode) throws CinemateException{
		NodeList eventFields = eventNode.getChildNodes();
		Event event = new Event();
		event.setUsername(user.getUsername());
		
		for (int i = 0; i<eventFields.getLength(); i++){
			Node eventField = eventFields.item(i);
			String nodeName = eventField.getNodeName();
			
			switch(nodeName){
				//set the movie object of the event.
				//this is why in parse() we made sure to parse the movies node list before the users node list: so this movie value wouldn't be null
				case StringConstants.MOVIE:
					event.setMovie(moviesMap.get(eventField.getFirstChild().getTextContent()));
					break;
				case StringConstants.ACTION:
					event.setAction(eventField.getFirstChild().getTextContent());
					//if we are given an invalid action
					if (!actionsList.contains(event.getAction())) { throw new CinemateException("Invalid action in an event in user's feed");}
					break;
				case StringConstants.RATING:
					try{
						
						if (eventField.getFirstChild() != null) {
							event.setRating(Integer.parseInt(eventField.getFirstChild().getTextContent()));
						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Event rating provided is not an int value");
					}
					
					break;
			}
		}
		//if the action is rated but there is no rating value
		if (event.getAction().equals(StringConstants.ACTION_RATED) && (event.getRating()==-1)){
			throw new CinemateException("found an event with Rated action but no rating");
		}
		//if the action is not rated but we are given a rating value
		if (!event.getAction().equals(StringConstants.ACTION_RATED) && (event.getRating() != -1)){
			throw new CinemateException("found an event without Rated action but with a non-empty rating value");
		}
		//set the icon of the event
		if (event.getAction().equals(StringConstants.ACTION_RATED)){
			event.setActionIcon(actionToIcon.get(event.getRatingToDisplay()+StringConstants.ACTION_RATED));
		}
		else{
			event.setActionIcon(actionToIcon.get(event.getAction()));
		}
		
		user.addEvent(event);
	}
	
	//SHARED HELPER METHODS
	
	//more generics!! Yay!!!! They are so helpful in times like these!!!
	//adds a new object to the appropriate set
	protected <T> void addObjectToMap(Map<String, Set<T>> map, String key, T object){
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
