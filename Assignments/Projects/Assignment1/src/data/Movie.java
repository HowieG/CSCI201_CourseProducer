package data;

import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String title;
	private String director;
	private String description;
	private int year;
	private List<String> actors;
	private List<String> writers;
	private double rating;
	private String genre;
	
	public Movie() {
		actors = new ArrayList<>();
		writers = new ArrayList<>();
	}
	
	//METHODS
	public void addActor(String actor){
		actors.add(actor);
	}
	
	public void addWriter(String actor){
		writers.add(actor);
	}
	
	//GETTERS
	public String getGenre(){
		return genre;
	}

	public String getTitle() {
		return title;
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
	public List<String> getActors() {
		return actors;
	}

	public double getRating() {
		return rating;
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

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public void setWriters(List<String> writers) {
		this.writers = writers;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
}
