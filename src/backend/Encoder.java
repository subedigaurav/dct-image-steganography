package backend;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Encoder {
	private static BufferedImage bufImg;

	
	//get the pixel value from image
	public int[] getPixels() {
		int[] pixel = null;
		for (int y = 0; y < bufImg.getHeight(); y++) {
			for (int x = 0; x < bufImg.getWidth(); x++) {
				pixel = bufImg.getRaster().getPixel(x, y, new int[3]);
			}
		}
		return pixel;
	}

	public static void main(String[] args) throws IOException {
		bufImg = ImageIO.read(new File("D:\\lena.jpg"));
	}
}
