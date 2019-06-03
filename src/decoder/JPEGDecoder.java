/*
 * Copyright (c) 2018. Gaurav Subedi.
 * A Java Implementation of JPEG Decoder
 * Based on Code from Matthias Mann
 * All rights reserved. 
 * 
 */
package decoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;
import java.util.Arrays;

import encoder.AES;

/**
 * A pure Java JPEG decoder
 * 
 * Partly based on code from Sean Barrett
 * 
 * @author Matthias Mann
 */
public class JPEGDecoder {

	static final int MARKER_NONE = 0xFF;

	private final InputStream is;
	private final byte[] inputBuffer;
	private int inputBufferPos;
	private int inputBufferValid;
	private boolean ignoreIOerror;

	private boolean headerDecoded;
	private boolean insideSOS;
	private boolean foundEOI;
	private int currentMCURow;

	private final IDCT_2D idct2D;
	
	public int getMsgLength() {
		return msgLength;
	}

	private final short[] data;
	private final Huffman[] huffmanTables;
	private final byte[][] dequant;

	private Component[] components;
	private Component[] order;

	private int codeBuffer;
	private int codeBits;
	private int marker = MARKER_NONE;
	private int restartInterval;
	private int todo;
	private int mcuCountX;
	private int mcuCountY;
	private int imageWidth;
	private int imageHeight;
	private int imgVMax;
	private boolean nomore;
	static final char dezigzag[] = ("\0\1\10\20\11\2\3\12" + "\21\30\40\31\22\13\4\5" + "\14\23\32\41\50\60\51\42"
			+ "\33\24\15\6\7\16\25\34" + "\43\52\61\70\71\62\53\44" + "\35\26\17\27\36\45\54\63"
			+ "\72\73\64\55\46\37\47\56" + "\65\74\75\66\57\67\76\77" + "\77\77\77\77\77\77\77\77"
			+ "\77\77\77\77\77\77\77").toCharArray();

	private static String decPassword;
	private static int msgLength;
	
	public String getPass() {
		return decPassword;
	}

	/**
	 * Constructs a new JPEGDecoder for the specified InputStream. The input stream
	 * is not closed by this decoder and must be closed by the calling code. The
	 * JPEG header is only read when calling {@link #decodeHeader() } or
	 * {@link #startDecode() }
	 * 
	 * @param is the InputStream containing the JPG data
	 */
	public JPEGDecoder(InputStream is) {
		this.is = is;
		this.inputBuffer = new byte[4096];

		this.idct2D = new IDCT_2D();
		this.data = new short[64];
		this.huffmanTables = new Huffman[8];
		this.dequant = new byte[4][64];
	}

	public boolean isIgnoreIOerror() {
		return ignoreIOerror;
	}

	/**
	 * Controls the behavior on IO errors. This must be called before
	 * {@link #decodeHeader() }
	 * 
	 * @param ignoreIOerror if true IO errors are ignored
	 */
	public void setIgnoreIOerror(boolean ignoreIOerror) {
		if (headerDecoded) {
			throw new IllegalStateException("header already decoded");
		}
		this.ignoreIOerror = ignoreIOerror;
	}

	/**
	 * Decodes the JPEG header. This must be called before the image size can be
	 * queried.
	 * 
	 * @throws IOException if an IO error occurred
	 */
	public void decodeHeader() throws IOException {
		if (!headerDecoded) {
			headerDecoded = true;

			int m = getMarker();
			if (m != 0xD8) {
				throw new IOException("no SOI");
			}
			m = getMarker();
			while (m != 0xC0 && m != 0xC1) { // SOF
				processMarker(m);
				m = getMarker();
				while (m == MARKER_NONE) {
					m = getMarker();
				}
			}

			processSOF();
		}
	}

	/**
	 * Returns the width of the image. {@link #decodeHeader() } must be called
	 * before the image width can be queried.
	 * 
	 * @return the width of the JPEG.
	 */
	public int getImageWidth() {
		ensureHeaderDecoded();
		return imageWidth;
	}

	/**
	 * Returns the height of the image. {@link #decodeHeader() } must be called
	 * before the image height can be queried.
	 * 
	 * @return the height of the JPEG.
	 */
	public int getImageHeight() {
		ensureHeaderDecoded();
		return imageHeight;
	}

	/**
	 * Returns the number of color components. {@link #decodeHeader() } must be
	 * called before the color components can be queried.
	 * 
	 * @return 1 for gray scale, 3 for color
	 */
	public int getNumComponents() {
		ensureHeaderDecoded();
		return components.length;
	}

	/**
	 * Returns the informations about the specific color component.
	 * {@link #decodeHeader() } must be called before the color components can be
	 * queried.
	 * 
	 * @param idx the color component. Must be < then {@link #getNumComponents() }
	 * @return the component information
	 */
	public Component getComponent(int idx) {
		ensureHeaderDecoded();
		return components[idx];
	}

	/**
	 * Returns the height of a MCU row. This is the smallest granularity for the raw
	 * decode API. {@link #decodeHeader() } must be called before the MCU row height
	 * can be queried.
	 * 
	 * @return the height of an MCU row.
	 */
	public int getMCURowHeight() {
		ensureHeaderDecoded();
		return imgVMax * 8;
	}

	/**
	 * Returns the number of MCU rows. {@link #decodeHeader() } must be called
	 * before the number of MCU rows can be queried.
	 * 
	 * @return the number of MCU rows.
	 * @see #getMCURowHeight()
	 */
	public int getNumMCURows() {
		ensureHeaderDecoded();
		return mcuCountY;
	}

	/**
	 * Returns the number of MCU columns. {@link #decodeHeader() } must be called
	 * before the number of MCU columns can be queried.
	 * 
	 * @return the number of MCU columns.
	 * @see #decodeDCTCoeffs(java.nio.ShortBuffer[], int)
	 */
	public int getNumMCUColumns() {
		ensureHeaderDecoded();
		return mcuCountX;
	}

	/**
	 * Starts the decode process. This will advance the JPEG stream to the start of
	 * the image data. It also checks if that JPEG file can be decoded by this
	 * library.
	 * 
	 * @return true if the JPEG can be decoded.
	 * @throws IOException if an IO error occurred
	 */
	public boolean startDecode() throws IOException {
		if (insideSOS) {
			throw new IllegalStateException("decode already started");
		}
		if (foundEOI) {
			return false;
		}

		decodeHeader();
		int m = getMarker();
		while (m != 0xD9) { // EOI
			if (m == 0xDA) { // SOS
				processScanHeader();
				insideSOS = true;
				currentMCURow = 0;
				reset();
				return true;
			} else {
				processMarker(m);
			}
			m = getMarker();
		}

		foundEOI = true;
		return false;
	}

	/**
	 * Decodes the dequantizied DCT coefficients into a buffer per color component.
	 * The number of buffers must match the number of color channels. Each color
	 * channel can have a different sub sampling factor.
	 * 
	 * @param buffer     the ShortBuffers for each color component
	 * @param numMCURows the number of MCU rows to decode.
	 * @throws IOException                   if an IO error occurred
	 * @throws IllegalArgumentException      if numMCURows is invalid, or if the
	 *                                       number of buffers / strides is not
	 *                                       enough
	 * @throws IllegalStateException         if {@link #startDecode() } has not been
	 *                                       called
	 * @throws UnsupportedOperationException if the color components are not in the
	 *                                       same SOS chunk
	 * @see #getNumComponents()
	 * @see #getNumMCURows()
	 */
	public void decodeDCTCoeffs(ShortBuffer[] buffer, int numMCURows) throws IOException {
		if (!insideSOS) {
			throw new IllegalStateException("decode not started");
		}

		if (numMCURows <= 0 || currentMCURow + numMCURows > mcuCountY) {
			throw new IllegalArgumentException("numMCURows");
		}

		int scanN = order.length;
		if (scanN != components.length) {
			throw new UnsupportedOperationException("for RAW decode all components need to be decoded at once");
		}
		if (scanN > buffer.length) {
			throw new IllegalArgumentException("not enough buffers");
		}

		for (int compIdx = 0; compIdx < scanN; compIdx++) {
			order[compIdx].outPos = buffer[compIdx].position();
		}

		outer: for (int j = 0; j < numMCURows; j++) {
			++currentMCURow;
			for (int i = 0; i < mcuCountX; i++) {
				for (int compIdx = 0; compIdx < scanN; compIdx++) {
					Component c = order[compIdx];
					ShortBuffer sb = buffer[compIdx];
					int outStride = 64 * c.blocksPerMCUHorz * mcuCountX;
					int outPos = c.outPos + 64 * i * c.blocksPerMCUHorz + j * c.blocksPerMCUVert * outStride;

					for (int y = 0; y < c.blocksPerMCUVert; y++) {
						sb.position(outPos);
						for (int x = 0; x < c.blocksPerMCUHorz; x++) {
							try {
								decodeBlock(data, c);
							} catch (ArrayIndexOutOfBoundsException ex) {
								throwBadHuffmanCode();
							}
							sb.put(data);
						}
						outPos += outStride;
					}
				}
				if (--todo <= 0) {
					if (!checkRestart()) {
						break outer;
					}
				}
			}
		}

		checkDecodeEnd();

		for (int compIdx = 0; compIdx < scanN; compIdx++) {
			Component c = order[compIdx];
			int outStride = 64 * c.blocksPerMCUHorz * mcuCountX;
			buffer[compIdx].position(c.outPos + numMCURows * c.blocksPerMCUVert * outStride);
		}
	}

	private void checkDecodeEnd() throws IOException {
		if (currentMCURow >= mcuCountY || marker != MARKER_NONE) {
			insideSOS = false;
			if (marker == MARKER_NONE) {
				skipPadding();
			}
		}
	}

	private void fetch() throws IOException {
		try {
			inputBufferPos = 0;
			inputBufferValid = is.read(inputBuffer);

			if (inputBufferValid <= 0) {
				throw new EOFException();
			}
		} catch (IOException ex) {
			inputBufferValid = 2;
			inputBuffer[0] = (byte) 0xFF;
			inputBuffer[1] = (byte) 0xD9; // EOI

			if (!ignoreIOerror) {
				throw ex;
			}
		}
	}

	private void read(byte[] buf, int off, int len) throws IOException {
		while (len > 0) {
			int avail = inputBufferValid - inputBufferPos;
			if (avail == 0) {
				fetch();
				continue;
			}
			int copy = (avail > len) ? len : avail;
			System.arraycopy(inputBuffer, inputBufferPos, buf, off, copy);
			off += copy;
			len -= copy;
			inputBufferPos += copy;
		}
	}

	private int getU8() throws IOException {
		if (inputBufferPos == inputBufferValid) {
			fetch();
		}
		return inputBuffer[inputBufferPos++] & 255;
	}

	private int getU16() throws IOException {
		int t = getU8();
		return (t << 8) | getU8();
	}

	private void skip(int amount) throws IOException {
		while (amount > 0) {
			int inputBufferRemaining = inputBufferValid - inputBufferPos;
			if (amount > inputBufferRemaining) {
				amount -= inputBufferRemaining;
				fetch();
			} else {
				inputBufferPos += amount;
				return;
			}
		}
	}

	private void growBufferCheckMarker() throws IOException {
		int c = getU8();
		if (c != 0) {
			marker = c;
			nomore = true;
		}
	}

	private void growBufferUnsafe() throws IOException {
		do {
			int b = 0;
			if (!nomore) {
				b = getU8();
				if (b == 0xff) {
					growBufferCheckMarker();
				}
			}
			codeBuffer |= b << (24 - codeBits);
			codeBits += 8;
		} while (codeBits <= 24);
	}

	private int decode(Huffman h) throws IOException {
		if (codeBits < 16) {
			growBufferUnsafe();
		}
		int k = h.fast[codeBuffer >>> (32 - Huffman.FAST_BITS)] & 255;
		if (k < 0xFF) {
			int s = h.size[k];
			codeBuffer <<= s;
			codeBits -= s;
			return h.values[k] & 255;
		}
		return decodeSlow(h);
	}

	private int decodeSlow(Huffman h) throws IOException {
		int temp = codeBuffer >>> 16;
		int s = Huffman.FAST_BITS + 1;

		while (temp >= h.maxCode[s]) {
			s++;
		}

		int k = (temp >>> (16 - s)) + h.delta[s];
		codeBuffer <<= s;
		codeBits -= s;
		return h.values[k] & 255;
	}

	private int extendReceive(int n) throws IOException {
		if (codeBits < 24) {
			growBufferUnsafe();
		}

		int k = codeBuffer >>> (32 - n);
		codeBuffer <<= n;
		codeBits -= n;

		int limit = 1 << (n - 1);
		if (k < limit) {
			k -= limit * 2 - 1;
		}
		return k;
	}

	private void decodeBlock(short[] data, Component c) throws IOException {
		Arrays.fill(data, (short) 0);

		final byte[] dq = c.dequant;

		{
			int t = decode(c.huffDC);
			int dc = c.dcPred;
			if (t > 0) {
				dc += extendReceive(t);
				c.dcPred = dc;
			}

			data[0] = (short) (dc * (dq[0] & 0xFF));
		}

		final Huffman hac = c.huffAC;

		int k = 1;
		do {
			int rs = decode(hac);
			k += rs >> 4;
			int s = rs & 15;
			if (s != 0) {
//                int v = extendReceive(s) * (dq[k] & 0xFF); 
				int v = extendReceive(s);
				data[k] = (short) v;
			} else if (rs != 0xF0) {
				break;
			}
		} while (++k < 64);
	}

	private static void throwBadHuffmanCode() throws IOException {
		throw new IOException("Bad huffman code");
	}

	private int getMarker() throws IOException {
		int m = marker;
		if (m != MARKER_NONE) {
			marker = MARKER_NONE;
			return m;
		}
		m = getU8();
		if (m != 0xFF) {
			return MARKER_NONE;
		}
		do {
			m = getU8();
		} while (m == 0xFF);
		return m;
	}

	private void reset() {
		codeBits = 0;
		codeBuffer = 0;
		nomore = false;
		marker = MARKER_NONE;

		if (restartInterval != 0) {
			todo = restartInterval;
		} else {
			todo = Integer.MAX_VALUE;
		}

		for (Component c : components) {
			c.dcPred = 0;
		}
	}

	private boolean checkRestart() throws IOException {
		if (codeBits < 24) {
			growBufferUnsafe();
		}
		if (marker >= 0xD0 && marker <= 0xD7) {
			reset();
			return true;
		}
		return false;
	}

	private void processMarker(int marker) throws IOException {
		if (marker >= 0xE0 && (marker <= 0xEF)) {
			int l = getU16() - 2;
			if (l < 0) {
				throw new IOException("bad length");
			}
			skip(l);
			return;
		}

		switch (marker) {
		case MARKER_NONE:
			throw new IOException("Expected marker");

		case 0xC2: // SOF - progressive
			throw new IOException("Progressive JPEG not supported");

		case 0xFE:
			int len = getU16();
			int id = getU8();
			switch (id) {
				case 2:
					String key = "gadSteganograph7"; // 128 bit key
					String initVector = "stegoInit1257980"; // 16 bytes IV
					StringBuilder pass = new StringBuilder();
	
					for (int i = 0; i < len - 3; i++) {
						pass.append((char) getU8());
					}
					decPassword = AES.decrypt(key, initVector, pass.toString());
					break;
	
				case 1:
					msgLength = (getU8() << 24) | (getU8() << 16) | (getU8() << 8) | getU8();
					break;
				}
			break;

		case 0xDD: // DRI - specify restart interval
			if (getU16() != 4) {
				throw new IOException("bad DRI length");
			}
			restartInterval = getU16();
			break;

		case 0xDB: { // DQT - define dequant table
			int l = getU16() - 2;
			while (l >= 65) {
				int q = getU8();
				int p = q >> 4;
				int t = q & 15;
				if (p != 0) {
					throw new IOException("bad DQT type");
				}
				if (t > 3) {
					throw new IOException("bad DQT table");
				}
				read(dequant[t], 0, 64);
				l -= 65;
			}
			if (l != 0) {
				throw new IOException("bad DQT length");
			}
			break;
		}

		case 0xC4: { // DHT - define huffman table
			int l = getU16() - 2;
			while (l > 17) {
				int q = getU8();
				int tc = q >> 4;
				int th = q & 15;
				if (tc > 1 || th > 3) {
					throw new IOException("bad DHT header");
				}
				int[] tmp = idct2D.tmp2D; // reuse memory
				for (int i = 0; i < 16; i++) {
					tmp[i] = getU8();
				}
				Huffman h = new Huffman(tmp);
				int m = h.getNumSymbols();
				l -= 17 + m;
				if (l < 0) {
					throw new IOException("bad DHT length");
				}
				read(h.values, 0, m);
				huffmanTables[tc * 4 + th] = h;
			}
			if (l != 0) {
				throw new IOException("bad DHT length");
			}
			break;
		}

		default:
			throw new IOException("Unknown marker: " + Integer.toHexString(marker));
		}

	}

	private void skipPadding() throws IOException {
		int x;
		do {
			x = getU8();
		} while (x == 0);

		if (x == 0xFF) {
			marker = getU8();
		}
	}

	private void processScanHeader() throws IOException {
		int ls = getU16();
		int scanN = getU8();

		if (scanN < 1 || scanN > 4) {
			throw new IOException("bad SOS component count");
		}
		if (ls != 6 + 2 * scanN) {
			throw new IOException("bad SOS length");
		}

		order = new Component[scanN];
		for (int i = 0; i < scanN; i++) {
			int id = getU8();
			int q = getU8();
			for (Component c : components) {
				if (c.id == id) {
					int hd = q >> 4;
					int ha = q & 15;
					if (hd > 3 || ha > 3) {
						throw new IOException("bad huffman table index");
					}
					c.huffDC = huffmanTables[hd];
					c.huffAC = huffmanTables[ha + 4];
					if (c.huffDC == null || c.huffAC == null) {
						throw new IOException("bad huffman table index");
					}
					order[i] = c;
					break;
				}
			}
			if (order[i] == null) {
				throw new IOException("unknown color component");
			}
		}

		if (getU8() != 0) {
			throw new IOException("bad SOS");
		}
		getU8();
		if (getU8() != 0) {
			throw new IOException("bad SOS");
		}
	}

	private void processSOF() throws IOException {
		int lf = getU16();
		if (lf < 11) {
			throw new IOException("bad SOF length");
		}

		if (getU8() != 8) {
			throw new IOException("only 8 bit JPEG supported");
		}

		imageHeight = getU16();
		imageWidth = getU16();

		if (imageWidth <= 0 || imageHeight <= 0) {
			throw new IOException("Invalid image size");
		}

		int numComps = getU8();
		if (numComps != 3 && numComps != 1) {
			throw new IOException("bad component count");
		}

		if (lf != 8 + 3 * numComps) {
			throw new IOException("bad SOF length");
		}

		int hMax = 1;
		int vMax = 1;

		components = new Component[numComps];
		for (int i = 0; i < numComps; i++) {
			Component c = new Component(getU8());
			int q = getU8();
			int tq = getU8();

			c.blocksPerMCUHorz = q >> 4;
			c.blocksPerMCUVert = q & 15;

			if (c.blocksPerMCUHorz == 0 || c.blocksPerMCUHorz > 4) {
				throw new IOException("bad H");
			}
			if (c.blocksPerMCUVert == 0 || c.blocksPerMCUVert > 4) {
				throw new IOException("bad V");
			}
			if (tq > 3) {
				throw new IOException("bad TQ");
			}
			c.dequant = dequant[tq];

			hMax = Math.max(hMax, c.blocksPerMCUHorz);
			vMax = Math.max(vMax, c.blocksPerMCUVert);

			components[i] = c;
		}

		int mcuW = hMax * 8;
		int mcuH = vMax * 8;

		imgVMax = vMax;
		mcuCountX = (imageWidth + mcuW - 1) / mcuW;
		mcuCountY = (imageHeight + mcuH - 1) / mcuH;

		for (int i = 0; i < numComps; i++) {
			Component c = components[i];
			c.width = (imageWidth * c.blocksPerMCUHorz + hMax - 1) / hMax;
			c.height = (imageHeight * c.blocksPerMCUVert + vMax - 1) / vMax;
			c.minReqWidth = mcuCountX * c.blocksPerMCUHorz * 8;
			c.minReqHeight = mcuCountY * c.blocksPerMCUVert * 8;

			if (c.blocksPerMCUHorz < hMax) {
				c.upsampler |= 1;
			}
			if (c.blocksPerMCUVert < vMax) {
				c.upsampler |= 2;
			}
		}
	}

	private void ensureHeaderDecoded() throws IllegalStateException {
		if (!headerDecoded) {
			throw new IllegalStateException("need to decode header first");
		}
	}

}