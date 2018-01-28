package com.gasu.disteg.algos;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class DctAlgo {
	//Block Size
	public int N=8;
	//Quality
	public int quality=25;
	//Transformed Cosine Matrix
	public double cT[][] = new double[N][N];
	//Cosine Matrix
	public double cosM[][] = new double[N][N];
	//Quantization Matrix
	public int[][] quantMatrix=new int[N][N];
	//Dimensions of the Image
	public int rows=320;
	public int cols=240;
	//Zigzag Matrix with 65 rows and 2 cols (for 0 and 1)
	public int zigZag[][] = new int[64][2];
	//The resultant DCT Matrix
	public int resDCT[][] = new int[rows][cols];
	
	public byte[] getbytesOfImage(BufferedImage image)
	{    
		WritableRaster raster=image.getRaster();
		DataBufferByte dbf=(DataBufferByte) raster.getDataBuffer();
		return dbf.getData();
	}
	
	public DctAlgo(int quality)
    {
        initZigZag();
        initQuantMatrix(quality);
    }
	
	//Initialize the Quantization Matrix based on quality supplied
	private void initQuantMatrix(int quality)
    {
        int i;
        int j;

        for (i = 0; i < N; i++)
        {
            for (j = 0; j < N; j++)
            {
               quantMatrix[i][j] = (1 + ((1 + i + j) * quality));
            }
        }
        
        //INITIALIZE COSINE TRANSFORM MATRIX
        for (j = 0; j < N; j++)
        {
            double nn = (double)(N);
            cosM[0][j]  = 1.0 / Math.sqrt(nn);
            cT[j][0] = cosM[0][j];
        }

        for (i = 1; i < 8; i++)
        {
            for (j = 0; j < 8; j++)
            {
                double jj = (double)j;
                double ii = (double)i;
                cosM[i][j]  = Math.sqrt(2.0/8.0) * Math.cos(((2.0 * jj + 1.0) * ii * Math.PI) / (2.0 * 8.0));
                cT[j][i] = cosM[i][j];
            }
        }
	    }
	
	//Initialize the zigzag matrix with the higher values stored at the end
	private void initZigZag()
    {
        zigZag[0][0] = 0; // 0,0
        zigZag[0][1] = 0;
        zigZag[1][0] = 0; // 0,1
        zigZag[1][1] = 1;
        zigZag[2][0] = 1; // 1,0
        zigZag[2][1] = 0;
        zigZag[3][0] = 2; // 2,0
        zigZag[3][1] = 0;
        zigZag[4][0] = 1; // 1,1
        zigZag[4][1] = 1;
        zigZag[5][0] = 0; // 0,2
        zigZag[5][1] = 2;
        zigZag[6][0] = 0; // 0,3
        zigZag[6][1] = 3;
        zigZag[7][0] = 1; // 1,2
        zigZag[7][1] = 2;
        zigZag[8][0] = 2; // 2,1
        zigZag[8][1] = 1;
        zigZag[9][0] = 3; // 3,0
        zigZag[9][1] = 0;
        zigZag[10][0] = 4; // 4,0
        zigZag[10][1] = 0;
        zigZag[11][0] = 3; // 3,1
        zigZag[11][1] = 1;
        zigZag[12][0] = 2; // 2,2
        zigZag[12][1] = 2;
        zigZag[13][0] = 1; // 1,3
        zigZag[13][1] = 3;
        zigZag[14][0] = 0; // 0,4
        zigZag[14][1] = 4;
        zigZag[15][0] = 0; // 0,5
        zigZag[15][1] = 5;
        zigZag[16][0] = 1; // 1,4
        zigZag[16][1] = 4;
        zigZag[17][0] = 2; // 2,3
        zigZag[17][1] = 3;
        zigZag[18][0] = 3; // 3,2
        zigZag[18][1] = 2;
        zigZag[19][0] = 4; // 4,1
        zigZag[19][1] = 1;
        zigZag[20][0] = 5; // 5,0
        zigZag[20][1] = 0;
        zigZag[21][0] = 6; // 6,0
        zigZag[21][1] = 0;
        zigZag[22][0] = 5; // 5,1
        zigZag[22][1] = 1;
        zigZag[23][0] = 4; // 4,2
        zigZag[23][1] = 2;
        zigZag[24][0] = 3; // 3,3
        zigZag[24][1] = 3;
        zigZag[25][0] = 2; // 2,4
        zigZag[25][1] = 4;
        zigZag[26][0] = 1; // 1,5
        zigZag[26][1] = 5;
        zigZag[27][0] = 0; // 0,6
        zigZag[27][1] = 6;
        zigZag[28][0] = 0; // 0,7
        zigZag[28][1] = 7;
        zigZag[29][0] = 1; // 1,6
        zigZag[29][1] = 6;
        zigZag[30][0] = 2; // 2,5
        zigZag[30][1] = 5;
        zigZag[31][0] = 3; // 3,4
        zigZag[31][1] = 4;
        zigZag[32][0] = 4; // 4,3
        zigZag[32][1] = 3;
        zigZag[33][0] = 5; // 5,2
        zigZag[33][1] = 2;
        zigZag[34][0] = 6; // 6,1
        zigZag[34][1] = 1;
        zigZag[35][0] = 7; // 7,0
        zigZag[35][1] = 0;
        zigZag[36][0] = 7; // 7,1
        zigZag[36][1] = 1;
        zigZag[37][0] = 6; // 6,2
        zigZag[37][1] = 2;
        zigZag[38][0] = 5; // 5,3
        zigZag[38][1] = 3;
        zigZag[39][0] = 4; // 4,4
        zigZag[39][1] = 4;
        zigZag[40][0] = 3; // 3,5
        zigZag[40][1] = 5;
        zigZag[41][0] = 2; // 2,6
        zigZag[41][1] = 6;
        zigZag[42][0] = 1; // 1,7
        zigZag[42][1] = 7;
        zigZag[43][0] = 2; // 2,7
        zigZag[43][1] = 7;
        zigZag[44][0] = 3; // 3,6
        zigZag[44][1] = 6;
        zigZag[45][0] = 4; // 4,5
        zigZag[45][1] = 5;
        zigZag[46][0] = 5; // 5,4
        zigZag[46][1] = 4;
        zigZag[47][0] = 6; // 6,3
        zigZag[47][1] = 3;
        zigZag[48][0] = 7; // 7,2
        zigZag[48][1] = 2;
        zigZag[49][0] = 7; // 7,3
        zigZag[49][1] = 3;
        zigZag[50][0] = 6; // 6,4
        zigZag[50][1] = 4;
        zigZag[51][0] = 5; // 5,5
        zigZag[51][1] = 5;
        zigZag[52][0] = 4; // 4,6
        zigZag[52][1] = 6;
        zigZag[53][0] = 3; // 3,7
        zigZag[53][1] = 7;
        zigZag[54][0] = 4; // 4,7
        zigZag[54][1] = 7;
        zigZag[55][0] = 5; // 5,6
        zigZag[55][1] = 6;
        zigZag[56][0] = 6; // 6,5
        zigZag[56][1] = 5;
        zigZag[57][0] = 7; // 7,4
        zigZag[57][1] = 4;
        zigZag[58][0] = 7; // 7,5
        zigZag[58][1] = 5;
        zigZag[59][0] = 6; // 6,6
        zigZag[59][1] = 6;
        zigZag[60][0] = 5; // 5,7
        zigZag[60][1] = 7;
        zigZag[61][0] = 6; // 6,7
        zigZag[61][1] = 7;
        zigZag[62][0] = 7; // 7,6
        zigZag[62][1] = 6;
        zigZag[63][0] = 7; // 7,7
        zigZag[63][1] = 7;
    }
	
	//This function performs forward DCT transformation with the result stored in output matrix
	public double[][] forwardDCT(int[][] blueArray)
    {
        double output[][] = new double[N][N];
        double temp[][] = new double[N][N];
        double temp1;
        int i;
        int j;
        int k;

        for (i = 0; i < N; i++)
        {
            for (j = 0; j < N; j++)
            {
                temp[i][j] = 0.0;
                for (k = 0; k < N; k++)
                {
                    temp[i][j] += (((blueArray[i][k])) * cT[k][j]);
                }
            }
        }

        for (i = 0; i < N; i++)
        {
            for (j = 0; j < N; j++)
            {
                temp1 = 0.0;

                for (k = 0; k < N; k++)
                {
                    temp1 += (cosM[i][k] * temp[k][j]);
                }

                output[i][j] = temp1;
            }
        }

        return output;
    }
	
	//Function to quantize image
	public int[][] quantitizeImage(byte inputData[][], boolean zigzag)
    {
        int outputData[][] = new int[N][N];
        int i = 0;
        int j = 0;

        int row;
        int col;

        double result;

        if (zigzag)
        {
            for (i = 0; i < (N*N); i++)
            {
                row = zigZag[i][0];
                col = zigZag[i][1];
                result = (inputData[row][col] / quantMatrix[row][col]);
                outputData[row][col] = (int)(Math.round(result));
            }

        }

        else
        {
            for (i=0; i<N; i++)
            {
                for (j=0; j<N; j++)
                {
                    result = inputData[i][j] / quantMatrix[i][j];
                    outputData[i][j] = (int)(Math.round(result));
                }
            }
        }

        return outputData;
    }

	//Function to dequantize image
	public int[][] dequantitizeImage(int[][] inputData, boolean zigzag)
    {
        int i = 0;
        int j = 0;
        int row;
        int col;
        int outputData[][] = new int[N][N];

        double result;

        if (zigzag)
        {
            for (i=0; i<(N*N); i++)
            {
                row = zigZag[i][0];
                col = zigZag[i][1];

                result = inputData[row][col] * quantMatrix[row][col];
                outputData[row][col] = (int)(Math.round(result));
            }
        }

        else
        {
            for (i=0; i<8; i++)
            {
                for (j=0; j<8; j++)
                {
                    result = inputData[i][j] * quantMatrix[i][j];
                    outputData[i][j] = (int)(Math.round(result));
                }
            }
        }

        return outputData;
    }
	
	//Function to Compress Image
	public int[] compressImage(int[] QDCT, boolean log)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int temp = 0;
        int runCounter = 0;
        int imageLength = rows*cols;

        int pixel[] = new int[rows*cols];

        while((i<imageLength))
        {
            temp = QDCT[i];

            while((i < imageLength) && (temp == QDCT[i]))
            {
                runCounter++;
                i++;
            }

            if (runCounter > 4)
            {
                pixel[j] = 255;
                j++;
                pixel[j] = temp;
                j++;
                pixel[j] = runCounter;
                j++;
            }
            else
            {
                for (k=0; k<runCounter; k++)
                {
                    pixel[j] = temp;
                    j++;
                }
            }

            if (log)
            {
                System.out.print("." + "\r");
            }

            runCounter = 0;
            //i++;
        }

        return pixel;
    }
	
	//Function to decompress image
	public int[] decompressImage(int[] DCT, boolean log)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int temp = 0;
        int imageLength = rows*cols;
        int pixel[] = new int[rows*cols];

        while (i < imageLength)
        {
            temp = DCT[i];

            if (k < imageLength)
            {
                if (temp == 255)
                {
                    i++;
                    int value = DCT[i];
                    i++;
                    int length = DCT[i];

                    for(j=0; j<length; j++)
                    {
                        pixel[k] = value;
                        k++;
                    }
                }

                else
                {
                    pixel[k] = temp;
                    k++;
                }
            }
            if (log)
            {
                System.out.print(".." + "\r");
            }

            i++;
        }

        for (int a = 0; a < 80; a++)
        {
            System.out.print(pixel[a] + " ");
        }
        System.out.println();
        for (int a = 0; a < 80; a++)
        {
            System.out.print(DCT[a] + " ");
        }

        return pixel;
    }

	//INVERSE DCT TRANSFORM
	public int[][] inverseDCT(double[][] temp2)
    {
        int output[][] = new int[N][N];
        double temp[][] = new double[N][N];
        double temp1;
        int i;
        int j;
        int k;

        for (i=0; i<N; i++)
        {
            for (j=0; j<N; j++)
            {
                temp[i][j] = 0.0;

                for (k=0; k<N; k++)
                {
                    temp[i][j] += temp2[i][k] * cosM[k][j];
                }
            }
        }

        for (i=0; i<N; i++)
        {
            for (j=0; j<N; j++)
            {
                temp1 = 0.0;
                for (k=0; k<N; k++)
                {
                    temp1 += cT[i][k] * temp[k][j];
                }

                temp1 += 128.0;
                if (temp1 < 0)
                {
                    output[i][j] = 0;
                }
                else if (temp1 > 255)
                {
                    output[i][j] = 255;
                }
                else
                {
                     output[i][j] = (int)Math.round(temp1);
                }
            }
        }
        return output;
    }
}
