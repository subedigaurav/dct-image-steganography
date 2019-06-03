/*
 * Copyright (c) 2008-2012, Matthias Mann 
 */
package decoder;

import java.nio.ShortBuffer;

/**
 * 
 * @author Matthias Mann 
 */ 
public class Component { 
 
    final int id; 
 
    int dcPred; 
    Huffman huffDC; 
    Huffman huffAC; 
    byte[] dequant; 
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
        this.cmpBuffer= ShortBuffer.allocate(height*width);
        this.cmpBuffer.position(0);
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