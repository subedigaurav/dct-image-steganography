package com.gasu.disteg.algos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DctTest {
	static File file;
	static File infile;
    public static int n = 8;
    public static double pi = 3.142857;
    static BufferedImage[] smallImages;
    static ByteArrayOutputStream baos=new ByteArrayOutputStream();
    static byte[] imageInByte;
    
    File output=null;
    
    //Message to be sent
    String message="This is Stego Message!!!";
    byte[] messageByteArray=convertMessageToByte(message);
    private static byte[] convertMessageToByte(String message) {
        byte[] messageByteArray = message.getBytes();
        return messageByteArray;
    }
    
public static void main(String[] args) throws IOException {
	int N=8;
	infile=new File("lenaImg.jpg");
	BufferedImage imageIn=ImageIO.read(infile);
	int width=imageIn.getWidth();
	int height=imageIn.getHeight();
	BufferedImage toWrite=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Color c;
	Color newColor;
	   
	   
	if(width%8!=0 && height%8!=0) {
		System.out.println("The File Size is not applicable......Exiting......");
		return;
	}
	
    //Count the total number of sub-Images    
	int imageCounter = 0;
        for (int y = 0; y < height; y+=8) {
          for (int x = 0; x < width; x+=8) {
                imageIn.getSubimage(x, y, 8, 8);
                imageCounter++;
            }
        }
       System.out.println("Images created: " + imageCounter);

//	smallImages = new BufferedImage[width][rows];
	int y=0;
	while(y!=height) {
	for (int x = 0; x < width; x+=8) {
		BufferedImage subImg=imageIn.getSubimage(x, y, 8, 8);
		DctAlgo ag=new DctAlgo(25);
		
		int[][] blueArray=new int[8][8];
		double[][] temp= new double[8][8];
		int[][] temp1=new int[8][8];
		
		//GET ARRAY OF BLUE COLORS FROM THE BLOCK
		for (int k = 0; k < 8; k++) {
	    	for (int l = 0; l < 8; l++) {
	    		//get pixel value
			    int p = subImg.getRGB(k,l);
			    //get blue channel value
			    int b = p & 0xff;
			    blueArray[k][l]=b;
	    	}}
		
			//PRINT THE BLUE ARRAY
			System.out.println("Blue Array::");	
			for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						System.out.print(blueArray[i][j]+ " ");
					}
					System.out.println();
				    if(i%8==0) System.out.println();
				}	  
		
		
//			//ANOTHER IMPLEMENTATION
//			temp=forwardDCT(blueArray);
//			for (int i = 0; i < temp.length; i++) {
//				for (int j = 0; j < temp.length; j++) {
//					System.out.print(temp[i][j]-128+ " ");
//				}
//				System.out.println();
//			    if(i%8==0) System.out.println();
//			}
			    
			
			//FORWARD DCT ON 8*8 PIXEL BLOCK
			System.out.println("Forward DCT Aray::");
		    temp= ag.forwardDCT(blueArray);
		    
//		    for (int i = 0; i < temp.length; i++) {
//				for (int j = 0; j < temp.length; j++) {
//					System.out.print(temp[i][j]+ " ");
//				}
//				System.out.println();
//			    if(i%8==0) System.out.println();
//			}
			    
		    //INVERSE DCT ON 8*8 PIXEL BLOCK
		    System.out.println("Extracted Blue Array::");
		    temp1=ag.inverseDCT(temp);
		    int p;
		    for (int i = 0; i < temp1.length; i++) {
				for (int j = 0; j < temp1.length; j++) {
					System.out.print(temp1[i][j]-128+ " ");
//					//get alpha
//					p = subImg.getRGB(i,j);
//					int a = (p>>24) & 0xff;
//				    int r = (p>>16) & 0xff;
//				    int g = (p>>8) & 0xff;
//				    int b = p & 0xff;
////					p = (a<<24) | (r<<16) | (g<<8) | b;
//					Color color=new Color(r,g,b);
//					toWrite.setRGB(x+i, y+j, color.getRGB());
					
					c = new Color(subImg.getRGB(i, j));
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					newColor = new Color(red,green,blue);
					toWrite.setRGB(x+i, y+j, newColor.getRGB());
				}
				System.out.println();
			    if(i%8==0&i!=0) System.out.println();
			}
		    System.out.print(" ");
		    if(y%8==0&y!=0) System.out.println();
			}
		y+=8;
	}																//End of While Loop
	File fos = new File("stegoImage.jpg");
	ImageIO.write(toWrite, "jpg", fos);	
	}
}

//
//public static byte[] getbytesOfImage(BufferedImage image)
//{    
//	WritableRaster raster=image.getRaster();
//	DataBufferByte dbf=raster.getDataBuffer();
//	return dbf.getData();
//}
