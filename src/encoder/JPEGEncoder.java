package encoder;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.plugins.jpeg.*;

public class JPEGEncoder {
	BufferedOutputStream outStream;
	int bufferPutBits, bufferPutBuffer, PutBuffer, PutBits;
	encoderInfo jpgObj;
	int imgHeight, imgWidth;
	private int mcuCountX, mcuCountY, mcuCount;
	int quality;
	Huffman[] huffmanTables;
	Quantization qs;
	JPEGImageReadParam as = new JPEGImageReadParam();
	BufferedImage image;
	byte[][][] cmpArr;
	short[][] dctTmp = new short[8][8];
	short[] zzS = new short[64];
	int[] zz = new int[64];
	Component[] cmp = new Component[3];
	byte msgBit;
	byte msgCount = 0;
	ByteBuffer byteBuffer;
	private int b;
	private int remainMsgBits;
	private int cmpBuffPos;
	private String msg;
	private String password;
	private int patterns[][] = { { 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 },
			{ 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25 },
			{ 28, 27, 26, 25, 29, 30, 31, 32, 33, 34, 35, 36, 40, 39, 38, 37 },
			{ 25, 26, 27, 28, 36, 35, 34, 33, 32, 31, 30, 29, 37, 38, 39, 40 } };

	// The Constructor of The Encoder Class
	public JPEGEncoder(int quality, BufferedImage image, BufferedOutputStream out, String message, String password) {
		this.quality = quality;
		this.outStream = out;
		qs = new Quantization(quality);
		this.image = image;
		jpgObj = new encoderInfo(image);
		this.imgHeight = image.getHeight();
		this.imgWidth = image.getWidth();
		this.cmpArr = new byte[3][imgHeight][imgWidth];
		for (int i = 0; i < 3; i++) {
			cmp[i] = new Component(i, imgHeight, imgWidth);
		}
		this.huffmanTables = new Huffman[4];
		this.mcuCountX = (imgWidth) / 8;
		this.mcuCountY = (imgHeight) / 8;
		this.mcuCount = this.mcuCountX * this.mcuCountY;
		this.msg = message;
		this.password = password;
	}

	public int nextmsgBit() throws IOException {
		if (msgCount == 0) {
			b = byteBuffer.get();
			if (b < 0) {
				throw new IOException("Couldn't read message!");
			}
			msgCount = 8;
		}
		final int bit = (b >> 7) & 0x01;
		msgCount--;
		b <<= 1;
		remainMsgBits--;
		if (remainMsgBits == 0) {
		}
		return bit;
	}

	// write EOI marker to the output stream
	public void writeEOI() {
		byte[] EOI = { (byte) 0xFF, (byte) 0xD9 };
		writeMarker(EOI, outStream);
	}

	// write the JFIF header to the output stream
	public void writeJFIF() {
		// The JFIF(JPEG Interchange Format Header)
		byte[] jfif = new byte[18];
		jfif[0] = (byte) 0xFF; // marker for JFIF
		jfif[1] = (byte) 0xE0;
		jfif[2] = (byte) 0x00; // length
		jfif[3] = (byte) 0x10;
		jfif[4] = (byte) 0x4A; // JFIF (null terminated)
		jfif[5] = (byte) 0x46;
		jfif[6] = (byte) 0x49;
		jfif[7] = (byte) 0x46;
		jfif[8] = (byte) 0x00;
		jfif[9] = (byte) 0x01; // JFIF Version 1.02
		jfif[10] = (byte) 0x02;
		jfif[11] = (byte) 0x00; // units for X and Y Densities 0:no unit
		jfif[12] = (byte) 0x01; // Horizontal Pixel Density
		jfif[13] = (byte) 0x00;
		jfif[14] = (byte) 0x01; // Vertical Pixel Density
		jfif[15] = (byte) 0x00;
		jfif[16] = (byte) 0x00; // Thumbnail Horizontal Pixel Count
		jfif[17] = (byte) 0x00; // Thumbnail Vertical Pixel Count
		writeArray(jfif, outStream);
	}

	// procedure to write the marker only (2 bytes)
	private void writeMarker(byte[] marker, BufferedOutputStream imgStream) {
		try {
			imgStream.write(marker);
		} catch (IOException e) {
			System.out.println("Image File Write Error: " + e.getMessage());
		}
	}

	/**
	 * Procedure to write the JPEG Headers SOI, JFIF, Comment, DQT, DHT, SOF and SOS
	 * headers are written in sequence to the OutputStream
	 * 
	 * @throws IOException
	 */
	public void writeHeaders() throws IOException {
		// The SOI(Start of Image) Marker
		byte[] SOI = { (byte) 0xFF, (byte) 0xD8 };
		writeMarker(SOI, outStream);
		writeJFIF();
		writeComment();
		writeMsgCmnt();
		writePassCmnt();
//		printQMatrix();
		writeDQT();
		writeSOF();
		writeDHT();
		writeSOS();
		forwardDCT();
//		printcmpBuf();
		encodeMessage(msg);
		writeCompressedData();
	}

	private void writePassCmnt() {
		String key = "gadSteganograph7"; // 128 bit key
		String initVector = "stegoInit1257980"; // 16 bytes IV

		String encryptedPassword = AES.encrypt(key, initVector, password);

		int len = encryptedPassword.length() + 1;
		byte[] pswrdBytes = encryptedPassword.getBytes();
		byte CMNTPSW[] = new byte[len + 5]; // one byte for the identifier
		CMNTPSW[0] = (byte) 0xFF;
		CMNTPSW[1] = (byte) 0xFE;
		CMNTPSW[2] = (byte) ((len >> 8) & 0xFF); // write length of the marker
		CMNTPSW[3] = (byte) (len & 0xFF);
		CMNTPSW[4] = (byte) (2 & 0xFF); // identifier for the encrypted password
		int offset = 5;
		for (int i = 0; i < pswrdBytes.length; i++) {
			CMNTPSW[offset] = pswrdBytes[i];
			offset++;
		}
		writeArray(CMNTPSW, outStream);
	}

	// Procedure to Write SOF header
	private void writeSOF() throws IOException {
		int index;
		byte[] SOF = new byte[19];
		SOF[0] = (byte) 0xFF;
		SOF[1] = (byte) 0xC0; // Baseline DCT
		SOF[2] = (byte) 0x00;
		SOF[3] = (byte) 17; // Length of the Header
		SOF[4] = (byte) 0x08; // Sample Precision
		SOF[5] = (byte) ((imgHeight >> 8) & 0xFF);
		SOF[6] = (byte) (imgHeight & 0xFF); // Y:No. of Lines
		SOF[7] = (byte) ((imgWidth >> 8) & 0xFF);
		SOF[8] = (byte) (imgWidth & 0xFF); // X:No. of Samples Per Line
		SOF[9] = (byte) jpgObj.getNoOfComponents();
		index = 10;

		// Write Component ID, Horizontal and Vertical Sampling Factors and QTable ID
		// for each Component
		for (int i = 0; i < SOF[9]; i++) {
			SOF[index++] = (byte) jpgObj.compID[i];
			SOF[index++] = (byte) ((jpgObj.hSampFactor[i] << 4) + jpgObj.vSampFactor[i]);
			SOF[index++] = (byte) jpgObj.qTableNum[i];
		}

		// Write The SOF header to OutputStream
		outStream.write(SOF);
	}

	// Procedure to Write SOS(Start of Scan) Header
	private void writeSOS() throws IOException {
		int index;
		byte[] SOS = new byte[14];

		SOS[0] = (byte) 0xFF;
		SOS[1] = (byte) 0xDA; // SOS Marker
		SOS[2] = (byte) 0x00;
		SOS[3] = (byte) 12; // Length of SOS Header
		SOS[4] = (byte) jpgObj.getNoOfComponents(); // Ns
		index = 5;

		for (int i = 0; i < SOS[4]; i++) {
			SOS[index++] = (byte) jpgObj.compID[i]; // Cs
			SOS[index++] = (byte) ((jpgObj.dcTableNum[i] << 4) + jpgObj.acTableNum[i]); // Tdi | Tai
		}
		SOS[index++] = (byte) jpgObj.Ss;
		SOS[index++] = (byte) jpgObj.Se;
		SOS[index] = (byte) ((jpgObj.Ah << 4) + jpgObj.Al);

		// Write SOS Header to OutputStream
		outStream.write(SOS);
	}

	// procedure to write comment as 0xFFEE marker
	private void writeMsgCmnt() {
		int len = msg.length();
		byte CMNTMSG[] = new byte[9];
		CMNTMSG[0] = (byte) 0xFF;
		CMNTMSG[1] = (byte) 0xFE;
		CMNTMSG[2] = (byte) (0 & 0xFF);
		CMNTMSG[3] = (byte) (7 & 0xFF);
		CMNTMSG[4] = (byte) (1 & 0xFF); // identifier for the message length
		CMNTMSG[5] = (byte) ((len >> 24) & 0xFF);
		CMNTMSG[6] = (byte) ((len >> 16) & 0xFF);
		CMNTMSG[7] = (byte) ((len >> 8) & 0xFF);
		CMNTMSG[8] = (byte) (len & 0xFF);
		writeArray(CMNTMSG, outStream);
	}

	public void encodeMessage(String msg) throws IOException {
		byte[] msgBytes = msg.getBytes();
		System.out.println("Length of Message:" + msgBytes.length);
		int embeddCapacity = (((mcuCount * 16) * 2) / 8);
		System.out.println("Payload Capacity (in Bytes):" + embeddCapacity);
		System.out.println("Payload Capacity (in KB):" + embeddCapacity / 1024);
		remainMsgBits = msgBytes.length * 8;

		int reqmscu = (int) Math.ceil(remainMsgBits / 16.0);
		System.out.println("Required MCUs:" + reqmscu);

		// select the Cb and Cr Components
		// The Cb Component
		// reset buffer positions
		for (int i = 0; i < cmp.length; i++) {
			cmp[i].cmpBuffer.position(0);
		}

		Component c = cmp[1];

		// store the message in bytebuffer
		byteBuffer = ByteBuffer.wrap(msgBytes);
		// ZZS contains the mcu data in zigzag form
		cmpBuffPos = 0;
		int PatternClass;
		int mcuid = 0;
		
		for (int i = 0; i < reqmscu; i++) {
			c.cmpBuffer.get(zzS);
			// encode data in ZZS

			// determine the pattern class based on the mcu number
			PatternClass = mcuid % 4;
			mcuid++;

			for (int j = 0; j < 16 && remainMsgBits != 0; j++) {
				int bit = nextmsgBit();
				// if the LSBs of the message bit and that of the bufferbit are not equal, then
				// encode message
				if ((zzS[patterns[PatternClass][j]] & 0x1) != bit) {
					if ((zzS[patterns[PatternClass][j]] & 0x1) == 0) {
						zzS[patterns[PatternClass][j]] += 1;
					} else {
						zzS[patterns[PatternClass][j]] -= 1;
					}
				}
			}
			c.cmpBuffer.position(cmpBuffPos);
			c.cmpBuffer.put(zzS);
			cmpBuffPos += 64;
		}
	}

	/**
	 * The Function to write the compressed data from the buffer that was created
	 * for YCbCr color space.
	 */

	private void writeCompressedData() {
		short[] zzB = new short[64];
		genHuffmanTables();
		// set dc and ac huffman tables for each component
		cmp[0].dcTable = 0;
		cmp[0].acTable = 1;
		cmp[1].dcTable = 2;
		cmp[1].acTable = 3;
		cmp[2].dcTable = 2;
		cmp[2].acTable = 3;

		// reset the buffer position
		for (int i = 0; i < cmp.length; i++) {
			cmp[i].cmpBuffer.position(0);
		}

		// Writes an MCU of Data
		for (int m = 1; m <= mcuCount; m++) {
			for (int i = 0; i < cmp.length; i++) {
				// get the byte array from the buffer
				Component c = cmp[i];
				c.cmpBuffer.get(zzB);

				// convert byte array to zigzag array
//				for (int j = 0; j < zzB.length; j++) {
//					zz[j]=(int) zzB[j];
//				}
				int prec = cmp[i].dcPred;
				// modify the coefficients here to write the stego message
				writeBlock(zzB, prec, cmp[i].dcTable, cmp[i].acTable);
				cmp[i].dcPred = zzB[0];
			}
		}
		flushBuffer();
	}

	// generate ehufco and ehufsi required for encoding
	private void genHuffmanTables() {
		for (int i = 0; i < huffmanTables.length; i++) {
			huffmanTables[i].encode();
		}
	}

	private void writeBlock(short[] zz, int prec, int dcCodeTable, int acCodeTable) {
		int temp, temp2, nbits;

		// The DC portion
		temp = temp2 = zz[0] - prec;
		if (temp < 0) {
			temp = -temp;
			temp2--;
		}

		nbits = 0;
		// find the class of the DC Coefficient
		while (temp != 0) {
			nbits++;
			temp >>= 1;
		}

		bufferIt(huffmanTables[dcCodeTable].getehufco(nbits), huffmanTables[dcCodeTable].getehufsi(nbits));
		// Write the additional bits
		if (nbits != 0) {
			bufferIt(temp2, nbits);
		}

		// The AC portion
		int r = 0, k;
		for (k = 1; k < 64; k++) {
			if ((temp = zz[k]) == 0) {
				if (k == 63) {
//					System.out.println("EOB Encountered"+Integer.toBinaryString(huffmanTables[acCodeTable].ehufco[0]));
					bufferIt(huffmanTables[acCodeTable].getehufco(0), huffmanTables[acCodeTable].getehufsi(0));
					return;
				} else {
					r++;
				}
			} else {
				while (r > 15) {
					bufferIt(huffmanTables[acCodeTable].getehufco(0xF0), huffmanTables[acCodeTable].getehufsi(0xF0));
					r -= 16;
				}
				temp2 = temp;
				if (temp < 0) {
					temp = -temp;
					temp2--;
				}
				nbits = 1;
				while ((temp >>= 1) != 0) {
					nbits++;
				}
				int rs = (r << 4) + nbits;
				bufferIt(huffmanTables[acCodeTable].getehufco(rs), huffmanTables[acCodeTable].getehufsi(rs));
				bufferIt(temp2, nbits);
				r = 0;
			}
		}
	}

	// Uses an integer long (32 bits) buffer to store the Huffman encoded bits
	// and sends them to outStream by the byte.
	void bufferIt(int code, int size) {
		PutBuffer = code;
		PutBits = bufferPutBits;

		PutBuffer &= (1 << size) - 1;
		PutBits += size;
		PutBuffer <<= 24 - PutBits;
		PutBuffer |= bufferPutBuffer;

		while (PutBits >= 8) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
			if (c == 0xFF) {
				try {
					outStream.write(0);
				} catch (IOException e) {
					System.out.println("IO Error: " + e.getMessage());
				}
			}
			PutBuffer <<= 8;
			PutBits -= 8;
		}
		bufferPutBuffer = PutBuffer;
		bufferPutBits = PutBits;
	}

	void flushBuffer() {
		int PutBuffer = bufferPutBuffer;
		int PutBits = bufferPutBits;
		while (PutBits >= 8) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
			if (c == 0xFF) {
				try {
					outStream.write(0);
				} catch (IOException e) {
					System.out.println("IO Error: " + e.getMessage());
				}
			}
			PutBuffer <<= 8;
			PutBits -= 8;
		}
		if (PutBits > 0) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
		}
	}

	private void writeDHT() throws IOException {
		System.out.println("Writing DHT Header...");
		JPEGHuffmanTable huffTable;
		int[] count = new int[16];
		int numOfSymbols;
		short[] lengths;
		short[] huffValS;
		byte[] huffVal;

		/**
		 * initialize the Huffman Tables for each Component
		 */
		/*
		 * Initialize Luminance Components Initialize the DC Table
		 */
		huffTable = JPEGHuffmanTable.StdDCLuminance;
		lengths = huffTable.getLengths();
		for (int i = 0; i < 16; i++) {
			count[i] = (int) lengths[i];
		}
		huffmanTables[0] = new Huffman(count);
		numOfSymbols = huffmanTables[0].getNumSymbols();
		huffValS = new short[numOfSymbols];
		huffVal = new byte[numOfSymbols];
		huffValS = huffTable.getValues();

		for (int i = 0; i < huffVal.length; i++) {
			huffVal[i] = (byte) (huffValS[i] & 0xFF);
		}
		huffmanTables[0].setHuffVal(huffVal);
		// write the Luminance's Standard DC Huffman Table
		writeDHTHeader(huffmanTables[0], 0);

		/* Initialize the AC Table */
		huffTable = JPEGHuffmanTable.StdACLuminance;
		lengths = huffTable.getLengths();
		for (int i = 0; i < 16; i++) {
			count[i] = (int) lengths[i];
		}
		huffmanTables[1] = new Huffman(count);
		numOfSymbols = huffmanTables[1].getNumSymbols();
		huffValS = new short[numOfSymbols];
		huffVal = new byte[numOfSymbols];
		huffValS = huffTable.getValues();

		for (int i = 0; i < huffVal.length; i++) {
			huffVal[i] = (byte) (huffValS[i] & 0xFF);
		}
		huffmanTables[1].setHuffVal(huffVal);
		// write the Luminance's Standard AC Huffman Table
		writeDHTHeader(huffmanTables[1], 16);

		/*
		 * Initialize Chrominance Components Initialize the DC Table
		 */
		huffTable = JPEGHuffmanTable.StdDCChrominance;
		lengths = huffTable.getLengths();
		for (int i = 0; i < 16; i++) {
			count[i] = (int) lengths[i];
		}
		huffmanTables[2] = new Huffman(count);
		numOfSymbols = huffmanTables[2].getNumSymbols();
		huffValS = new short[numOfSymbols];
		huffVal = new byte[numOfSymbols];
		huffValS = huffTable.getValues();

		for (int i = 0; i < huffVal.length; i++) {
			huffVal[i] = (byte) (huffValS[i] & 0xFF);
		}
		huffmanTables[2].setHuffVal(huffVal);
		// write the Chrominance Standard DC Huffman Table
		writeDHTHeader(huffmanTables[2], 1); // 16=00010000 for DC of Chrominance

		/* Initialize the AC Table */
		huffTable = JPEGHuffmanTable.StdACChrominance;
		lengths = huffTable.getLengths();
		for (int i = 0; i < 16; i++) {
			count[i] = (int) lengths[i];
		}
		huffmanTables[3] = new Huffman(count);
		numOfSymbols = huffmanTables[3].getNumSymbols();
		huffValS = new short[numOfSymbols];
		huffVal = new byte[numOfSymbols];
		huffValS = huffTable.getValues();

		for (int i = 0; i < huffVal.length; i++) {
			huffVal[i] = (byte) (huffValS[i] & 0xFF);
		}
		huffmanTables[3].setHuffVal(huffVal);
		// write the Chrominance's Standard AC Huffman Table
		writeDHTHeader(huffmanTables[3], 17); // 17=00010001 for the AC of Chrominance

	}

	// Procedure to Write DHT(Define Huffman Tables) Header
	public void writeDHTHeader(Huffman huff, int a) throws IOException {
		byte[] DHT1, DHT2, DHT3, DHT4;
		int index, bytes = 0, olIndex, intermIndex;

		index = 5;
		olIndex = 5;
		DHT1 = new byte[17];
		DHT4 = new byte[5];
		DHT4[0] = (byte) 0xFF;
		DHT4[1] = (byte) 0xC4;

		for (int i = 1; i < 17; i++) {
			DHT1[index++ - olIndex] = (byte) huff.getBits(i);
		}

		bytes = huff.getNumSymbols();
		System.out.println("BYTES:" + bytes);
		System.out.println(huff.huffval.length);
		intermIndex = index;
		DHT2 = new byte[bytes];

		for (int i = 0; i < bytes; i++) {
			DHT2[index++ - intermIndex] = (byte) huff.getHuffVal(i);
		}

		DHT3 = new byte[index];
		System.arraycopy(DHT4, 0, DHT3, 0, olIndex);
		System.arraycopy(DHT1, 0, DHT3, olIndex, 16);
		System.arraycopy(DHT2, 0, DHT3, olIndex + 16, bytes);

		DHT4 = DHT3;
		olIndex = index;

		DHT4[2] = (byte) (((index - 2) >> 8) & 0xFF);
		DHT4[3] = (byte) ((index - 2) & 0xFF);
		DHT4[4] = (byte) (a & 0xFF);
		outStream.write(DHT4);
	}

	// procedure to write the DQT header for both Luminance and Chrominance
	// Components
	private void writeDQT() {
		System.out.println("Writing DQT Header...");
		int[] tmpArray = ZigZag.toZigZag(qs.qMatrix);
		byte[] DQT = new byte[69];
		DQT[0] = (byte) 0xFF;
		DQT[1] = (byte) 0xDB;
		DQT[2] = (byte) 0x00;
		DQT[3] = (byte) 0x43;
		int offset = 4;
		int pq = 0; // precision value is 0 (for 8-bit value)
		// write DQT marker for Luminance and Chrominance
		for (int i = 0; i < 2; i++) {
			offset = 4;
			DQT[offset++] = (byte) ((pq >> 4) + i);
			for (int j = 0; j < 64; j++) {
				DQT[offset++] = (byte) tmpArray[j];
			}
			writeArray(DQT, outStream);
		}
		System.out.println("DQT Header Written Successfully!");
	}

	// procedure to write comment as 0xFFEE marker
	private void writeComment() {
		String comment = new String();
		comment = jpgObj.getComment();
		int len = comment.length();
		byte COMMENT[] = new byte[len + 4];
		len = len + 2;
		COMMENT[0] = (byte) 0xFF;
		COMMENT[1] = (byte) 0xFE;
		COMMENT[2] = (byte) ((len >> 8) & 0xFF);
		COMMENT[3] = (byte) (len & 0xFF);
		java.lang.System.arraycopy(jpgObj.comment.getBytes(), 0, COMMENT, 4, jpgObj.comment.length());
		writeArray(COMMENT, outStream);
	}

	// procedure to write an array of bytes to the output stream
	void writeArray(byte[] data, BufferedOutputStream out) {
		int length;
		try {
			length = (((int) (data[2] & 0xFF)) << 8) + (int) (data[3] & 0xFF) + 2;
//			System.out.println(length);
			out.write(data, 0, length);
		} catch (IOException e) {
			System.out.println("Image Write Error: " + e.getMessage());
		}
	}

	// decompose pixel integer into constituent rgb values
	public byte[] getrgb(int pixel) {
		byte[] rgb = new byte[3];
		rgb[0] = (byte) ((pixel >> 16) & 0xFF);
		rgb[1] = (byte) ((pixel >> 8) & 0xFF);
		rgb[2] = (byte) (pixel & 0xFF);
		return rgb;
	}

	/**
	 * Convert RGB [0..256] byte values and place them into a YCbCr [0..256] byte
	 * array.
	 * 
	 * @return The Converted Values are placed in the YCbCr byte array
	 */
	public void toYcbCr(byte[] rgb, byte[] yCbCr) {
		int r = rgb[0] & 0xFF;
		int g = rgb[1] & 0xFF;
		int b = rgb[2] & 0xFF;

		int y = (int) ((0.299 * r) + (0.587 * g) + (0.114 * b));
		int Cb = (int) ((-0.16875 * r) - (0.33126 * g) + (0.500 * b) + 128);
		int Cr = (int) ((0.500 * r) - (0.41869 * g) - (0.08131 * b) + 128);

//    	int y=16+ (((r<<6) + (r<<1) + (g<<7) + g + (b<<4) + (b<<3) + b)>>8);
//    	int Cb=128+((-((r<<5) + (r<<2)+ (r<<1)) - ((g<<6) + (g<<3) + (g<<1))+ (b<<7) - (b<<4))>>8);
//    	int Cr=128+ (((r<<7) - (r<<4) - ((g<<6) + (g<<5)- (g<<1)) - ((b<<4)+ (b<<1)))>>8);

		if (y > 255)
			yCbCr[0] = (byte) 255;
		else if (y < 0)
			yCbCr[0] = 0;
		else
			yCbCr[0] = (byte) y;

		if (Cb > 255)
			yCbCr[1] = (byte) 255;
		else if (Cb < 0)
			yCbCr[1] = 0;
		else
			yCbCr[1] = (byte) Cb;

		if (Cr > 255)
			yCbCr[2] = (byte) 255;
		else if (Cr < 0)
			yCbCr[2] = 0;
		else
			yCbCr[2] = (byte) Cr;
	}

	public void getYCbCr() {
		int pixel;
		byte[] rgb = new byte[3];
		byte[] ycbcr = new byte[3];

		for (int i = 0; i < imgHeight; i++) {
			for (int j = 0; j < imgWidth; j++) {
				pixel = image.getRGB(j, i);
				rgb = getrgb(pixel);
				toYcbCr(rgb, ycbcr);
				for (int m = 0; m < ycbcr.length; m++) {
					cmpArr[0][i][j] = ycbcr[0];
					cmpArr[1][i][j] = ycbcr[1];
					cmpArr[2][i][j] = ycbcr[2];
				}
			}
//			System.out.println();
		}
//		for (int i = 0; i < imgHeight; i++) {
//			for (int j = 0; j < imgWidth; j++) {
//				System.out.print("Y:"+(this.cmpArr[0][i][j] & 0xFF)+"Cb"+(this.cmpArr[1][i][j] & 0xFF)+"Cr"+(this.cmpArr[2][i][j] & 0xFF));
//			}
//			System.out.println();
//		}
	}

	public void printcmpBuf() {
		System.out.println();
		System.out.println("Printing from Buffer::");
		for (int i = 0; i < cmp.length; i++) {
			for (int j = 0; j < imgHeight * imgWidth; j++) {
				System.out.print(cmp[i].cmpBuffer.get(j) + " ");
			}
			System.out.println();
		}
	}

	public void printQMatrix() {
		System.out.println();
		System.out.println("Printing Q Matrix:");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print(qs.qMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void forwardDCT() {
		// print the Quantization Matrix
//		System.out.println("The Quantization Matrix:");
//		for (int k = 0; k < qs.qMatrix.length; k++) {
//			for (int k2 = 0; k2 < qs.qMatrix.length; k2++) {
//				System.out.print(qs.qMatrix[k][k2]+" ");
//			}
//			System.out.println();
//		}

		// do for each component

		for (int cmpId = 0; cmpId < cmpArr.length; cmpId++) {
//			System.out.println("For Component ID:"+cmpId);
//			System.out.println();
			for (int i = 0; i < imgHeight; i += 8) {
				for (int j = 0; j < imgWidth; j += 8) {

					// extract the block from the array
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							dctTmp[k][l] = (short) ((cmpArr[cmpId][i + k][j + l]) & 0xFF);

							// perfornm level shifting for FDCT
							dctTmp[k][l] -= 128;
						}
					}
					DCT d = new DCT();
					dctTmp = d.forwardDCT(dctTmp);
//					System.out.println("After FDCT and Quantization:");
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							dctTmp[k][l] = (short) Math.round(dctTmp[k][l] / qs.qMatrix[k][l]);
//							System.out.print((int) dctTmp[k][l]+" ");
						}
					}
					zz = ZigZag.toZigZag(dctTmp);
					// convert zz to short
					for (int k = 0; k < zz.length; k++) {
						zzS[k] = (short) zz[k];
					}
					// put the quantized temp array into respective buffer of the component
					cmp[cmpId].cmpBuffer.put(zzS);
				}
			}
		}
	}
}
