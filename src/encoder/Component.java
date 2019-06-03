package encoder;

import java.nio.ShortBuffer;

public class Component {

	/*
	 * identifier for each component 1=Y(Luminance) 2=Cb(Chrominance Blue)
	 * 3=Cr(Chrominance Red)
	 */
	final int id;
	short dcPred;
	int dcTable;
	int acTable;
	Huffman huffDC;
	Huffman huffAC;
	byte[] qtable;
	ShortBuffer cmpBuffer;
	int blocksPerMCUVert;
	int blocksPerMCUHorz;
	int width;
	int height;
	int minReqWidth;
	int minReqHeight;
	int outPos;
	int upsampler;

	Component(int id) {
		this.id = id;
	}

	Component(int id, int width, int height) {
		this.id = id;
		this.cmpBuffer = ShortBuffer.allocate(height * width);
	}

	public int getID() {
		return id;
	}

	public int getMinReqWidth() {
		return minReqWidth;
	}

	public int getMinReqHeight() {
		return minReqHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBlocksPerMCUHorz() {
		return blocksPerMCUHorz;
	}

	public int getBlocksPerMCUVert() {
		return blocksPerMCUVert;
	}
}
