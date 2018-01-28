package com.gasu.disteg.misc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageToArray {
	static File file;

//Convert image to Stream of Bytes
private byte[] getByteData(BufferedImage image)
{
  WritableRaster raster = image.getRaster();
  DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
  return buffer.getData();
}

//Read Image from file
private BufferedImage getImage(File file)
{
  BufferedImage image = null;
  try
  {
    image = ImageIO.read(file);

  }
  catch (Exception ex)
  {
    //throw alert failed to load image
  }
  return image;
}

private boolean saveImage(BufferedImage image, File file, String ext)
{
  try
  {
    file.delete();
    ImageIO.write(image, ext, file);
    return true;

  }
  catch (Exception e)
  {
    //alert file could not be saved to the alert window
	  return false;
  }
}

//Create a byte array for the message bits
private byte[] bitConversion(int i)
{
  byte byte3 = (byte)((i & 0xFF000000) >>> 24);	
  byte byte2 = (byte)((i & 0xFF0000) >>> 16);	
  byte byte1 = (byte)((i & 0xFF00) >>> 8);		
  byte byte0 = (byte)(i & 0xFF);				
  
  return new byte[] { byte3, byte2, byte1, byte0 };
}


//Converts the Image to Data Byte Array and Displays It
public static byte[] convert(File file) {
	BufferedImage image=null;
	try {
		image = ImageIO.read(file);
	} catch (IOException e1) {
		e1.printStackTrace();
	}
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
		ImageIO.write(image, "png", baos);
	} catch (IOException e) {
		e.printStackTrace();
	}
    byte[] res=baos.toByteArray();
//    for (int i = 0; i < res.length; i++) {
//    	if(i%8==0) System.out.print("\n");
//    	System.out.print(res[i]+ " ");;
//}
   return res;
}

public byte[] decode(File file)
{
  try
  {
    BufferedImage image = getImage(file);
    return decodeText(getByteData(image));
  }
  catch (Exception e)
  {
    e.printStackTrace();
    //Print Data Decoding Failed in Alert Window
  }
  return null;
}

private BufferedImage embedText(BufferedImage image, byte[] msg)
{
  byte[] img = getByteData(image);
  byte[] len = bitConversion(msg.length);
  
  try
  {
    encodeText(img, len, 0);
    
    encodeText(img, msg, 32);
  }
  catch (Exception e)
  {
    e.printStackTrace();
    

//    Main.getMainPane().alert("Target File cannot hold message!", "Error");
    return null;
  }
  return image;
}

public boolean encode(String fileName, String stegan, byte[] message)
{
  BufferedImage image_orig = getImage(fileName);
  
  BufferedImage image = userSpace(image_orig);
  image = embedText(image, message);
  if (image == null) {
    return false;
  }
  return saveImage(image, new File(stegan), "png");
}

private byte[] encodeText(byte[] image, byte[] addition, int offset)
{
//  if (addition.length + offset > image.length)
//  {
//    throw new IllegalArgumentException("File not long enough!");
//  }
//  
//
//  for (int i = 0; i < addition.length; i++)
//  {
//    int add = addition[i];
//    for (int bit = 7; bit >= 0; offset++)
//    {
//      int b = add >>> bit & 0x1;
//      
//      image[offset] = ((byte)(image[offset] & 0xFE | b));
//    }
	 return image;
  }


private byte[] decodeText(byte[] image)
{
  int length = 0;
  int offset = 32;
  
  for (int i = 0; i < 32; i++)
  {
    length = length << 1 | image[i] & 0x1;
  }
  
  byte[] result = new byte[length];
  
  for (int b = 0; b < result.length; b++)
  {

    for (int i = 0; i < 8; offset++)
    {

      result[b] = ((byte)(result[b] << 1 | image[offset] & 0x1));i++;
    }
  }
  return result;
}

public static void main(String[] args) throws IOException{
	file=new File("gasu.JPG");
	byte[] imgByte=convert(file);
	BufferedImage originalImage = ImageIO.read(new File("powerBtn.png"));

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	double [] block=new double[w*h];
	int[] rgb1 = new int[w*h];
	for (int i=0; i<baos.length; i++) {
	       block[i] =(double) (rgb1[i] & 0xFF);
	       System.out.print(imgByte[i]+"  ");
//		   System.out.print(block[i]+"  ");
		   if(i%8==0) System.out.println();
}
}
}