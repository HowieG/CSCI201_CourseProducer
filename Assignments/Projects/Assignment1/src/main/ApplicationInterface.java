package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DataStorage;
import data.Movie;
import data.User;

//ApplicationInterface inherits from DataStorage so it has access to any protected variable and methods from DataStorage
public class ApplicationInterface extends DataStorage{
	private BufferedReader br;
	private User loggedInUser;
	private Map<Integer, String> movieMenu; 
	private Integer currentSearch;
	//valid input sets
	private Set<Integer> validMainMenuInputs;
	private Set<Integer> validMovieMenuInputs;
	private Set<Integer> validLoggedInInputs;
	private Set<Integer> zeroInput;
	
	public ApplicationInterface(String filePath){
		super(filePath);
		//sets of valid inputs depending on the menu
		validMainMenuInputs = new HashSet<>();
		validLoggedInInputs = new HashSet<>();
		validMovieMenuInputs = new HashSet<>();
		zeroInput = new HashSet<>();
		//maps from the search choice in the movie search menu, to the string of what they are searching by
		movieMenu = new HashMap<>();
		movieMenu.put(3, DataStorage.genre);
		movieMenu.put(2, DataStorage.title);
		movieMenu.put(1, DataStorage.actor);
		
		//populating the valid inputs for each menu
		zeroInput.add(0);
	
		for (int i = 1; i<7; i++) {
			validLoggedInInputs.add(i);
			
			if (i <5 ){
				validMovieMenuInputs.add(i);
				
				if (i<3) validMainMenuInputs.add(i);
			}
		}
		
		//begin user interaction
		br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			print("Welcome to Cinemate!");
			start();
		} catch (IOException e) {
			print("Something went wrong! "+e.getMessage());
		}
	}
	
	private void start() throws IOException{
		//print the main menu
		printMainMenu();
		//get a valid input from the user and if it is 2, then exit
		if (getValidInput(validMainMenuInputs) == 2) {
			print("Goodbye!");
			System.exit(0);
		}
		//else we try to login
		login();
	}
	
	//try to login
	private void login() throws IOException{
		//give three tries to give a username
		print("Please enter your username");
		giveThreeTries("Invalid username. ", false);
		//give three tries to give the correct password
		print("Please enter your password");
		giveThreeTries("Incorrect password. ", true);
		//if the program makes it to these lines, that means valid credentials were given
		print("Welcome "+loggedInUser.getFName()+"! ");
		loggedIn();
	}
	
	//helper method for getting the username and password
	private void giveThreeTries(String toPrint, Boolean forPassword) throws IOException{
		//get their first input
		String input = br.readLine();
		//loop is a boolean that determines whether a valid input was given. This may be new syntax for you; it is essentially a one line if/else statement.
		//here is a generic example: <condition> ? <action1> : <action2> 
		//which is synonymous to: if <condition> then <action1> else <action2>
		Boolean loop = (forPassword ? loggedInUser.getPassword().equals(input) : usersMap.containsKey(input));
		int numTries = 1;
		//while the user hasn't run out of tries, and a valid input has not been provided
		while (!loop && numTries != 3){
			//print how many tries they have left
			print(toPrint+"You will be given "+ (3 - numTries)+" more "+(numTries == 2 ? "try" : "tries"));
			input = br.readLine();
			numTries++;
			//reset the loop variable since we were given a new input
			loop = (forPassword ? loggedInUser.getPassword().equals(input) : usersMap.containsKey(input));
		}
		//the loop could have broken because we got a valid input or because they ran out of tries
		//this if statement checks whether we still have an invalid input, and if so, reprint the main menu
		if (!loop) start();
		//if this loop was for retrieving the username, we need to set the loggedInUser variable
		if (!forPassword) loggedInUser = usersMap.get(input);
	}
	
	private void loggedIn() throws IOException{
		//prints the 6 menu options
		printLoggedInMenu();
		//gets the user's input 
		int input = getValidInput(validLoggedInInputs);
		
		switch(input){
			//log the user out by calling the start() method again
			case 5:
				print("You have been successfully logged out");
				start();
				break;
			case 6:
				print("Goodbye!");
				System.exit(0);
				break;
			case 2:
				searchMovies();
				break;
			//print the user's feed and then print the feed from all the user's they are following
			case 3: 
				print("Feed:", loggedInUser.toStringFeed());
				//get the list of usernames, and for each, get the user object from the usersMap and print their feed
				for (String following : loggedInUser.getFollowing()) System.out.print(usersMap.get(following).toStringFeed());
				System.out.println();
				//print the 'menu' which only allows the '0' input
				printSmallMenu();
				break;
			//print the user's profile information
			case 4:
				print(loggedInUser.toString());
				printSmallMenu();
				break;
			case 1:
				searchUsers();
				break;
		}	
	}
	
	//SEARCH MOVIES
	private void searchMovies() throws IOException{
		//print the search for movies menu
		printMovieMenu();
		//get a valid input from the user
		int input = getValidInput(validMovieMenuInputs);
		//if the user chooses to go back to the login menu, call the loggedIn() method again
		if (input == 4) loggedIn();
		//set which search choice they gave and then search for movies
		else{
			currentSearch = input;
			searchMoviesSpecific();
		}
		
	}
	
	//search by genre, actor, or title
	private void searchMoviesSpecific() throws IOException {
		//the message prints the search parameter they are using by getting the value from the movieMenu map
		print("Please enter the "+movieMenu.get(currentSearch)+" you wish to search for movies by");
		//get the search param
		String searchParam = br.readLine();
		//more complicated boolean logic: if the currentSearch is 1, search by actor, else, if the currentSearch is 3, search by genre, else, search by title
		Set<Movie> results = (currentSearch == 1 ? searchByActor(searchParam) : 
			(currentSearch == 3 ? searchByGenre(searchParam) : searchByTitle(searchParam)));
		
		//printResults() returns a boolean as to whether there is at least one title returned. If it returns true, print out the results
		if (printResults(results)) for (Movie mov : results) print(mov.getTitle());
		//print the menu that allows a user to search again or return to the login menu
		printMovieMiniMenu();
	}
	
	//SEARCH USERS
	private void searchUsers() throws IOException{
		print("Please enter the username you are searching for");
		Set<User> results = searchForUser(br.readLine());
		//if there is as least one result, print all the usernames that are returned
		if (printResults(results)) for (User user : results) print(user.getUsername());
		//print the menu that allows a user to search again or return to the login menu
		printUserMiniMenu();
	}
	
	//FUN HELPER METHODS
	//yay generics! You'll learn about them soon if you haven't already
	private Boolean printResults(Set<?> results){
		//if the results set is null, we know there were no results
		Boolean hasResults = results != null;
		//if the set is null, print 0 results, else print the number of results that were returned
		if (!hasResults) print("0 Results");
		else print(results.size()+" Results:");
		
		return hasResults;
	}
	
	//takes the set of valid inputs as a parameter, and loops in a while until a valid input is provided by the user, then returns the input
	private int getValidInput(Set<Integer> validInputs) throws IOException{
		Boolean validInput = false;
		String input = "";
		int menuNum = -1;
		//while we don't have a valid input
		while (!validInput){
			//get new input
			input = br.readLine();
			
			try{
				menuNum = Integer.parseInt(input);
				//if the set of valid inputs does not contain the given number, print error message
				if (!validInputs.contains(menuNum)) print("Invalid Entry. Please provide a valid number.");
				//else stop the loop
				else validInput = true;
			}
			//if the user provided a string value, print an error message
			catch(NumberFormatException nfe){
				print("Invalid entry. Please provide a number input.");
			}
		}
		
		return menuNum;
	}
	
	//PRINTING MENUS
	//print the menu with only the '0' option
	private void printSmallMenu() throws IOException{
		
		print("If you would like to return to the login menu, enter 0");
		getValidInput(zeroInput);
		//the only possible valid input that could be returned is '0', so once we get past the getValidInput() call, we know to return to the login menu
		loggedIn();
	}
	
	//print the mini menu after a search, for either users or movies
	private void printMiniMenu(String secondChoice) throws IOException{
		print(System.lineSeparator()+"Please choose from the options below"+System.lineSeparator(), "1.Back to Login Menu", secondChoice);
		//if the input returned was 1, return to the login menu, else, the parent method will handle the other input
		if (getValidInput(validMainMenuInputs) == 1) loggedIn();
	}
	
	//print the mini menu after a movie search
	private void printMovieMiniMenu() throws IOException{
		printMiniMenu("2.Back to Movie Menu");
		//if we passed the printMiniMenu() call, we know the user chose to search again rather than going back to the login menu
		searchMovies();	
	}
	
	//print the mini menu after a user search
	private void printUserMiniMenu() throws IOException{
		printMiniMenu("2.Search Again");
		//if we passed the printMiniMenu() call, we know the user chose to search again rather than going back to the login menu
		searchUsers();
	}
	
	//print the login menu
	private void printLoggedInMenu(){
		print("Please choose from the options below"+System.lineSeparator(), 
		"1.Search Users", "2.Search Movies", "3.View Feed", "4.View Profile", "5.Logout", "6.Exit");
	}
	
	//print the main menu
	private void printMainMenu(){
		print("Please choose from the options below", System.lineSeparator()+"1.Login", "2.Exit");
	}
	
	//print the movie search menu
	private void printMovieMenu(){
		print("Please choose from the options below"+System.lineSeparator(), "1.Search by Actor", "2.Search by Title", "3.Search by Genre","4.Back to Login Menu");
	}
	
	//method that prints multiple lines at once.
	//the syntax 'String ... toPrint' means that 0 or more String parameters can be passed to the print method
	//the parameters are treated as a collection, so I can iterate over them and print them all out on their own line
	private void print(String... toPrint) {for (String print : toPrint) System.out.println(print);}
}
