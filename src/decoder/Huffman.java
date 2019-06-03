/*
 * Copyright (c) 2008-2010, Matthias Mann 
 */
package decoder; 
 
import java.io.IOException; 
import java.util.Arrays; 
 
/**
 * 
 * @author Matthias Mann 
 */ 
class Huffman { 
 
    static final int FAST_BITS = 9; 
    static final int FAST_MASK = (1 << FAST_BITS) - 1; 
 
    final byte[] fast; 
    final byte[] values; 
    final byte[] size; 
    final int[] maxCode; 
    final int[] delta; 
 
    public Huffman(int[] count) throws IOException { 
        int numSymbols = 0; 
        for(int i=0 ; i<16 ; i++) { 
            numSymbols += count[i]; 
        } 
 
        fast = new byte[1 << FAST_BITS]; 
        values = new byte[numSymbols]; 
        size = new byte[numSymbols]; 
        maxCode = new int[18]; 
        delta = new int[17]; 
 
        for(int i=0,k=0 ; i<16 ; i++) { 
            for(int j=0 ; j<count[i] ; j++) { 
                size[k++] = (byte)(i+1); 
            } 
        } 
 
        final int[] code = new int[256]; 
 
        int i = 1; 
        int k = 0; 
        for(int c=0 ; i<=16 ; i++) { 
            delta[i] = k - c; 
            if(k < numSymbols && size[k] == i) { 
                do { 
                    code[k++] = c++; 
                }while(k < numSymbols && size[k] == i); 
                if(c-1 >= (1<<i)) { 
                    throw new IOException("Bad code length"); 
                } 
            } 
            maxCode[i] = c << (16 - i); 
            c <<= 1; 
        } 
        maxCode[i] = Integer.MAX_VALUE; 
 
        Arrays.fill(fast, (byte)-1); 
        for(i=0 ; i<k ; i++) { 
            int s = size[i]; 
            if(s <= FAST_BITS) { 
                int c = code[i] << (FAST_BITS - s); 
                int m = 1 << (FAST_BITS - s); 
                for(int j=0 ; j<m ; j++) { 
                    fast[c+j] = (byte)i; 
                } 
            } 
        } 
    } 
 
    public int getNumSymbols() { 
        return values.length; 
    } 
}