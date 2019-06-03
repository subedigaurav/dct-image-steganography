package encoder;

import java.io.IOException;

public class Huffman {

	int numSymbols;
	byte[] bits = new byte[17];
	int[] huffsize;
	int[] huffcode;
	static int lastk;
	byte[] huffval;
	int[] ehufco;
	int[] ehufsi;
	int MASK_BIT;

	int[] mincode;
	int[] maxcode;
	int[] valptr;

	// count the total number of symbols encoded in DHT
	public Huffman(int[] count) throws IOException {
		this.numSymbols = 0;
		for (int i = 0; i < 16; i++) {
			this.numSymbols += count[i];
			this.bits[i + 1] = (byte) count[i];
		}
		this.huffval = new byte[numSymbols];
		this.huffsize = new int[numSymbols + 1];
		this.huffcode = new int[numSymbols + 1];
		this.ehufco = new int[255];
		this.ehufsi = new int[255];
		this.mincode = new int[17];
		this.maxcode = new int[17];
		this.valptr = new int[17];
	}

	// HUFFSIZE: Contains a list of code lengths
	//DONE
	public void generateSizeTable() {
		int k = 0, i = 1, j = 1;
		do {
			while (j <= bits[i]) {
				huffsize[k] = i;
				k = k + 1;
				j = j + 1;
			} 
				i = i + 1;
				j = 1;
		} while (i <= 16);
		huffsize[k] = 0;
		lastk = k;
	}

	//generate HUFFCODE
	//DONE
	public void generateCodeTable() {
		int k = 0;
		short code = 0;
		int si = huffsize[0];
		do {
			huffcode[k] = code;
			code = (short) (code + 1);
			k = k + 1;
			if (huffsize[k] == si) continue;
				
			if (huffsize[k] == 0)return;
					do {
						code = (short) (code << 1);
						si = si + 1;
					} while (huffsize[k] != si);
		} while (huffsize[k] == si);
	}

	//generate EHUFCO and EHUFSI
	public void orderCodes() {
		int k = 0;
		int i;
		do {
			i = huffval[k] & 0xFF;	//don't mess with this line
			ehufco[i] = huffcode[k];
			ehufsi[i] = huffsize[k];
			k = k + 1;
		} while (k < lastk);
	}

	// generate maxcode, mincode and valptr
	public void genDecoderTables() {
		int i = 0, j = 0;
		do {
			i++;
			if (i > 16)
				return;

			if (bits[i] == 0) {
				maxcode[i] = -1;
				continue;
			}

			valptr[i] = j;
			mincode[i] = huffcode[j];
			j = j + bits[i] - 1;
			maxcode[i] = huffcode[j];
			j = j + 1;
		} while (true);
	}

	@SuppressWarnings("unused")
	private void display() {
//		System.out.println("BITS[1...16] ::Contains No. of Codes of Each Size");
//		for (int i = 0; i < bits.length; i++) {
//			System.out.print(bits[i] + "\t");
//		}
//		System.out.println();

//		System.out.println("HUFFVAL ::Contains Symbol values to be associated with the codes");
		int pos = 0;
		for (int i = 0; i < bits.length; i++) {
			int count = bits[i];
			if (count == 0) {
				// System.out.println();
				continue;
			}
			for (int j = 0; j < count; j++) {
				// System.out.print(Integer.toHexString(huffval[pos] & 0xFF)+" ");
				pos++;
			}
			System.out.println();
		}

//		System.out.println("HUFFCODE");
		for (int j = 0; j < huffcode.length - 1; j++) {
			int i = huffcode[j];
			MASK_BIT = huffsize[j];
			// select N bits from LSB
			i = i << -MASK_BIT >>> -MASK_BIT;
			huffcode[j] = i;
			// System.out.print((Integer.toHexString(huffval[j] &
			// 0xFF))+":"+Integer.toBinaryString(i)+"\t");
		}

//		System.out.println("\nOrdered Huffman Codes::");
		for (int i = 0; i < 255; i++) {
			if (i != 0 & ehufco[i] == 0)
				continue;
			int m = ehufco[i];
			MASK_BIT = ehufsi[i];
			// select N bits from LSB
			m = m << -MASK_BIT >>> -MASK_BIT;
			// System.out.print(Integer.toBinaryString(m)+"\t");
		}

		// System.out.println("\nOrdered Huffman Code Sizes");
		// for (int i = 0; i < 255; i++) {
		// if (ehufsi[i]==0) continue;
		// System.out.print((ehufsi[i])+"\t");
		// }
	}

	public int getNumSymbols() {
		return numSymbols;
	}

	public int getMaxCode(int i) {
		return maxcode[i];
	}

	public int getValPtr(int i) {
		return valptr[i];
	}

	public int getMinCode(int i) {
		return mincode[i];
	}

	public int getHuffVal(int j) throws IOException {
		return huffval[j];
	}

	public void displayHuffValues() {
		System.out.println("Huffman Valus:");
		for (int i = 0; i < huffval.length; i++) {
			System.out.print(huffval[i] + " ");
		}
	}

	public void setHuffVal(byte[] b) {
		huffval = b;
	}

	public int getBits(int i) {
		return bits[i];
	}

	public int getehufco(int nbits) {
		return ehufco[nbits];
	}

	public int getehufsi(int nbits) {
		return ehufsi[nbits];
	}

	public void encode() {
		generateSizeTable();
		generateCodeTable();
		orderCodes();
		
		genDecoderTables();
//		display();
//		System.out.println("MAXCODE");
//		for (int i = 1; i < maxcode.length; i++) {
//			System.out.print(Integer.toBinaryString(maxcode[i]) + " ");
//		}
//		System.out.println();
//		System.out.println("MINCODE");
//		for (int i = 0; i < mincode.length; i++) {
//			System.out.print(Integer.toBinaryString(mincode[i]) + " ");
//		}
	}

}
