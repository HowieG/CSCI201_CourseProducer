package data;

public class Actor {
	private String fname;
	private String lname;
	private String image;
	
	//getters
	public String getFName(){
		return fname;
	}
	
	public String getLName(){
		return lname;
	}
	
	public String getImage(){
		return image;
	}
	
	public String getFullName(){
		return fname + " "+lname;
	}
	
	//setters
	public void setImage(String image){
		this.image = image;
	}
	
	public void setLName(String lname){
		this.lname = lname;
	}
	
	public void setFName(String fname){
		this.fname = fname;
	}
	
}
