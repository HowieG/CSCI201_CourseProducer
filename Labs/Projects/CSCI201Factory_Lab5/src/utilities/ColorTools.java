package utilities;

import java.util.Random;

public class ColorTools {
	
	private int r;
	private int g;
	private int b;
//	private boolean useWhite;
	
	public ColorTools() {
		makeRandomRGB();
	}
	
	public void makeRandomRGB() {
		Random rand = new Random();
		
		//create numbers from 0 - 255
		Double rDouble = (Math.floor(rand.nextFloat()*100))%256;
		Double gDouble = (Math.floor(rand.nextFloat()*100))%256;
		Double bDouble = (Math.floor(rand.nextFloat()*100))%256;
		
		//convert these doubles to ints that we need for the CSS - rgb(int, int, int)
		this.r = rDouble.intValue();
		this.g = gDouble.intValue();
		this.b = bDouble.intValue();
	}
	
/*
 * Cool stuff that might be used in later labs	
 */

//	//return a number 0 (black) to 255 (white) representing the brightness of our color
//	public int getBrightness()
//	{
//	   return (int)Math.sqrt(
//	      this.r * this.r * .241 + 
//	      this.g * this.g * .691 + 
//	      this.b * this.b * .068);
//	}
//	
//	//determines if the color is dark enough to warrant using white text
//	public boolean useWhite()
//	{
//		useWhite = (getBrightness() < 130) ? false : true;
//		return useWhite;
//	}

	public int getRed() {
		return r;
	}
	
	public int getGreen() {
		return g;
	}
	
	public int getBlue() {
		return b;
	}
	
}
