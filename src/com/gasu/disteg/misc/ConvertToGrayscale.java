package com.gasu.disteg.misc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ConvertToGrayscale {
	static File file;
	
	ConvertToGrayscale(File f){
		file=f;
	}

	public static void main(String[] args) throws IOException {
		BufferedImage img = null;
	    file = null;

	    //read image
	    try{
	      file = new File("lenaImg.jpg");
	      img = ImageIO.read(file);
	    }catch(IOException e){
	      System.out.println(e);
	    }

	    //get image width and height
	    int width = img.getWidth();
	    int height = img.getHeight();
	    
	    
	    for (int x = 0; x < height; x++) {
	    	for (int y = 0; y < width; y++) {
	    		//get pixel value
//			    int p = img.getRGB(x,y);

			    Color c = new Color(img.getRGB(x, y));
				int red = (int) (c.getRed() * 0.299);
				int green = (int) (c.getGreen() * 0.587);
				int blue = (int) (c.getBlue() * 0.114);
//				System.out.println(red+" ");
				Color newColor = new Color(red + green + blue,

				red + green + blue, red + green + blue);

				img.setRGB(x, y, newColor.getRGB());
			    
			    
//			    int a=(p>>24)& 0xff;
//			    int r=(p>>16)& 0xff;
//			    int g=(p>>8)& 0xff;
//			    int b=(p>>0)& 0xff;

//			    //CONVERT TO GRAYSCALE
//			    p = (a<<24) | (r<<16) | (g<<8) | b;
//		        img.setRGB(x, y, p);
			}
		}
	   
	    file=new File("lennaGray.jpg");
	    ImageIO.write(img, "jpg", file);
	}
}
