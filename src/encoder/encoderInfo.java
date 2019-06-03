package encoder;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class encoderInfo {

	String comment="Digital Image Steganography by GADS @ 2018.";
	public Image imgObj;
	public int imgHeight;
	public int imgWidth;
	public int blockWidth[];
	public int blockHeight[];
	
	//set 8 bit precision
	public int precision=8;
	public int noOfComponents=3;
	Component[] components;
	public int[] compID= {1,2,3};
	
	//set Horizontal and Vertical Sampling Factors
	public int[] hSampFactor= {1,1,1};
	public int[] vSampFactor= {1,1,1};
	
	//DC and AC Quantization Table Numbers
	public int[] qTableNum= {0,1,1};
	public int[] dcTableNum= {0,1,1};
	public int[] acTableNum= {0,1,1};
	
	//various Image Parameters for baseline JPEG
	public int Ss=0;
	public int Se=63;
	public int Ah=0;
	public int Al=0;
	
	encoderInfo(BufferedImage image){
		this.imgHeight=image.getHeight();
		this.imgWidth=image.getWidth();
	}

	public String getComment() {
		return comment;
	}
	
	public int getNoOfComponents() {
		return noOfComponents;
	}
}
