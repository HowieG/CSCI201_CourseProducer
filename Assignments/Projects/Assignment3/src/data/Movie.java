package data;

import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String title;
	private String image;
	private String director;
	private String description;
	private int year;
	private List<Actor> actors;
	private List<String> writers;
	private int numRatings;
	private long totalRating;
	private String genre;
	
	public Movie() {
		actors = new ArrayList<>();
		writers = new ArrayList<>();
	}
	
	//METHODS
	public void addActor(Actor actor){
		actors.add(actor);
	}
	
	public void addWriter(String actor){
		writers.add(actor);
	}
	
	public void incrementRatingCount(){
		numRatings++;
	}
	
	public void updateRatingTotal(int rating){
		totalRating += rating;
	}
	
	//GETTERS
	
	public long getAverageRating(){
		return (numRatings == 0 ? 0 : (totalRating/numRatings));
	}
	
	public long getTotalRating(){
		return totalRating;
	}
	
	public int getRatingCount(){
		return numRatings;
	}
	
	public String getGenre(){
		return genre;
	}
	
	public List<String> getWriters(){
		return writers;
	}

	public String getTitle() {
		return title;
	}
	
	public String getImage(){
		return image;
	}

	public String getDirector() {
		return director;
	}

	public String getDescription() {
		return description;
	}

	public int getYear() {
		return year;
	}
	public List<Actor> getActors() {
		return actors;
	}
	
	//SETTERS
	public void setTitle(String title) {
		this.title = title;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	public void setWriters(List<String> writers) {
		this.writers = writers;
	}

	public void setRatingTotal(long rating) {
		totalRating = rating;
	}
	
	public void setRatingCount(int num) {
		numRatings = num;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public void setImage(String image){
		this.image = image;
	}
}
