package data;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataSaving {

	private Document doc;
	private String filepath;
	//logged in user
	private User loggedInUser;
	//lambda that adds the user to the loggedin user's following list
	private BiConsumer<Node, User> addFollowing;
	//lambda that removes the user from the loggedin user's following list
	private BiConsumer<Node, User> removeFollowing;
	
	public DataSaving(Document doc, String filepath){
		//filepath an doc needed to modify and save the xml file
		this.doc = doc;	
		this.filepath = filepath;
		//instantiate and define the lambdas
		createLambdas();
	}
	
	private void createLambdas(){
		addFollowing = (following, user) -> {
			
			//create a username element
			Element newFollowing = doc.createElement(StringConstants.USERNAME);
			newFollowing.appendChild(doc.createTextNode(user.getUsername()));
			//append the new following node to the node that contians the entire following list
			following.appendChild(newFollowing);
			//save to the xml file
			save();
			//add the user's username to the the loggedin user's following list
			loggedInUser.getFollowing().add(user.getUsername());
			//add the loggedin user's username to the user's followers list
			user.getFollowers().add(loggedInUser.getUsername());
			return;
		};
		
		removeFollowing = (following, user) ->{
			//iterate through the following list to find the target element
			NodeList usernames = following.getChildNodes();
			
			for (int i = 0; i<usernames.getLength(); i++) {
				
				Node node = usernames.item(i);
	
				if (node.getNodeType() == Node.ELEMENT_NODE) {

			        String follow = node.getFirstChild().getTextContent();
			        //if this is the username we want to remove from the list
			        if (follow.equals(user.getUsername())){
			        	//remove this username from the following list
			        	following.removeChild(node);
			        	//save to the mxl file
			        	save();
			        	//remove this user's username from the loggedin user's following list
			        	loggedInUser.getFollowing().remove(user.getUsername());
			        	//remove the loggedin user's username from the user's followers list
			    		user.getFollowers().remove(loggedInUser.getUsername());
			        	return;
			        }
			    }
				
			}
		};
		
	}
	
	//setters
	public void setLoggedInUser(User user){
		loggedInUser = user;
	}
	
	//methods to modify xml
	public void addFollowing(User user){
		//pass in the addFollowing lambda
		changeFollowing(user, addFollowing);
	}
	
	public void removeFollowing(User user){
		//pass in the removeFollowing lambda
		changeFollowing(user, removeFollowing);
	}
	
	public void createNewUser(User user) throws ParserConfigurationException, SAXException, IOException{
		//create new user element
		Element newUser = doc.createElement(StringConstants.USER);
		//create elements for new user
		Element fname = doc.createElement(StringConstants.FNAME);
		Element lname = doc.createElement(StringConstants.LNAME);
		Element username = doc.createElement(StringConstants.USERNAME);
		Element password = doc.createElement(StringConstants.PASSWORD);
		Element feed = doc.createElement(StringConstants.FEED);
		Element following = doc.createElement(StringConstants.FOLLOWING);
		//add content to all the elements
		fname.appendChild(doc.createTextNode(user.getFName()));
		lname.appendChild(doc.createTextNode(user.getLName()));
		username.appendChild(doc.createTextNode(user.getUsername()));
		password.appendChild(doc.createTextNode(user.getPassword()));
		//append elements as children to new user element
		newUser.appendChild(fname);
		newUser.appendChild(lname);
		newUser.appendChild(username);
		newUser.appendChild(password);
		newUser.appendChild(following);
		newUser.appendChild(feed);
		//append new user element to users list
		doc.getElementsByTagName(StringConstants.USERS).item(0).appendChild(newUser);
		//save to xml
		save();
	}
	
	//retreives the node we want to modify
	//target: the node value we are targeting
	//targetKey: the nodeName we are targeting
	//tagName: the tagName to use to get the starting nodeList
	private Node getTargetNode(String target, String targetKey, String tagName){
		NodeList nodeList = doc.getElementsByTagName(tagName);
		
		NodeList children = nodeList.item(0).getChildNodes();

		for (int i = 0; i<children.getLength(); i++){
			Node child = children.item(i);
		
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				
				NodeList grandchildren = child.getChildNodes();
				
				for (int j = 0; j<grandchildren.getLength(); j++){
					
					Node grandchild = grandchildren.item(j);
					String nodeName = grandchild.getNodeName();
					//if the nodename matches the name of the node we are targeting
					if(nodeName.equals(targetKey)){
						//if the content of the node matches the content we are targeting, return the node
						if (grandchild.getFirstChild().getTextContent().equals(target)){
							return child;
						}
					
					}
				}
			}
		}
		
		return null;

	}
	
	//create an event
	public void addEvent(String actionTaken, String title, Integer ratingGiven){
		
		Node targetUser = getTargetNode(loggedInUser.getUsername(), StringConstants.USERNAME, StringConstants.USERS);
		NodeList children = targetUser.getChildNodes();

		for (int j = 0; j<children.getLength(); j++){
			Node child = children.item(j);
			String nodeName = child.getNodeName();
	
			if(nodeName.equals(StringConstants.FEED)){
				//create the elements we need
				Element eventElement = doc.createElement(StringConstants.EVENT);
				Element movieTitle = doc.createElement(StringConstants.MOVIE);
				Element action = doc.createElement(StringConstants.ACTION);
				Element rating = doc.createElement(StringConstants.RATING);
				//set the content of the elements
				movieTitle.appendChild(doc.createTextNode(title));
				action.appendChild(doc.createTextNode(actionTaken));
				
				if (ratingGiven != null){
					rating.appendChild(doc.createTextNode(Integer.toString(ratingGiven)));
				}
				
				eventElement.appendChild(action);
				eventElement.appendChild(movieTitle);
				eventElement.appendChild(rating);
				//add the new event element to the feed
				child.appendChild(eventElement);
				//save to the xml
				save();
				return;
			}
		}
	
	}
	
	//either add or remove user from the loggedin user's following list
	public void changeFollowing(User user, BiConsumer<Node, User> function){
		Node targetUser = getTargetNode(loggedInUser.getUsername(), StringConstants.USERNAME, StringConstants.USERS);
		NodeList children = targetUser.getChildNodes();

		for (int j = 0; j<children.getLength(); j++){
			Node child = children.item(j);
			String nodeName = child.getNodeName();
	
			if(nodeName.equals(StringConstants.FOLLOWING)){
				//call the lambda on the following node
				function.accept(child, user);
			}
		}
	}
	
	//change the rating data on a movie
	public void changeRating(String title, Movie movie){
		//get target movie node
		Node targetMovie = getTargetNode(title, StringConstants.TITLE, StringConstants.MOVIES);

		NodeList children = targetMovie.getChildNodes();

		for (int j = 0; j<children.getLength(); j++){
			Node oldChild = children.item(j);
			String nodeName = oldChild.getNodeName();
			//if we found the rating-total node
			if(nodeName.equals(StringConstants.RATING_TOTAL)){
				//create a new rating-total node and set the content
				Element newChild = doc.createElement(StringConstants.RATING_TOTAL);
				newChild.appendChild(doc.createTextNode(Long.toString(movie.getTotalRating())));
				//replace the existing rating-total node
				targetMovie.replaceChild(newChild, oldChild);
			
			}
			//if we found the rating-count node
			else if (nodeName.equals(StringConstants.RATING_COUNT)){
				//create a new rating-count node and set the content
				Element newChild = doc.createElement(StringConstants.RATING_COUNT);
				newChild.appendChild(doc.createTextNode(Integer.toString(movie.getRatingCount())));
				//replace the existing rating-count node
				targetMovie.replaceChild(newChild, oldChild);
			}

		}
		//save to the xml file
		save();
	}
	
	//save to the xml file
	public void save(){
		try{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filepath)));
		}
		catch (TransformerException e){
			System.out.println("exception in saving the xml file: "+e.getMessage());
		}
	}
}
