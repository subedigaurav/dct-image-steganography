package encoder;

public class ZigZag {
	
	private static final boolean FORWARD = true;
	private static final boolean BACKWARD = false;

	public static int[][] deZigZag(int[] res, int width, int height)
	  {
	    int[][] buffer = new int[height][width];

	    for (int i= 0; i < height; i++)
	      for (int j = 0; j < width; j++)
	        buffer[i][j] = 11;

	    boolean direction = ZigZag.FORWARD;
	    int xindex = 0, yindex = 0, dataindex = 0;

	    while (xindex < width && yindex < height && dataindex < res.length)
	      {
	        buffer[yindex][xindex] = res[dataindex];
	        dataindex++;

	        if (direction == ZigZag.FORWARD)
	          {
	            if (yindex == 0 || xindex == (width - 1))
	              {
	                direction = ZigZag.BACKWARD;
	                if (xindex == (width - 1))
	                  yindex++;
	                else
	                  xindex++;
	              }
	            else
	              {
	                yindex--;
	                xindex++;
	              }
	          }
	        else
	          {
	            if (xindex == 0 || yindex == (height - 1))
	              {
	                direction = ZigZag.FORWARD;
	                if (yindex == (height - 1))
	                  xindex++;
	                else
	                  yindex++;
	              }
	            else
	              {
	                yindex++;
	                xindex--;
	              }
	          }
	      }
	    return (buffer);
	  }
	
	public static short[][] deZigZag(short[] res, int width, int height)
	  {
	    short[][] buffer = new short[height][width];

	    for (int i= 0; i < height; i++)
	      for (int j = 0; j < width; j++)
	        buffer[i][j] = 11;

	    boolean direction = ZigZag.FORWARD;
	    int xindex = 0, yindex = 0, dataindex = 0;

	    while (xindex < width && yindex < height && dataindex < res.length)
	      {
	        buffer[yindex][xindex] = res[dataindex];
	        dataindex++;

	        if (direction == ZigZag.FORWARD)
	          {
	            if (yindex == 0 || xindex == (width - 1))
	              {
	                direction = ZigZag.BACKWARD;
	                if (xindex == (width - 1))
	                  yindex++;
	                else
	                  xindex++;
	              }
	            else
	              {
	                yindex--;
	                xindex++;
	              }
	          }
	        else
	          {
	            if (xindex == 0 || yindex == (height - 1))
	              {
	                direction = ZigZag.FORWARD;
	                if (yindex == (height - 1))
	                  xindex++;
	                else
	                  yindex++;
	              }
	            else
	              {
	                yindex++;
	                xindex--;
	              }
	          }
	      }
	    return (buffer);
	  }
	
	/*
	 * ZigZag encode a matrix of equal height and width into a byte array
	 * @returns a byte array containing the matrix elements in zigzag order
	 * 
	 */
	public static int[] toZigZag(short[][] dctTmp) {
        if (dctTmp == null || dctTmp.length == 0) {
            return null;
        }

        int m = dctTmp.length;
        int n = dctTmp[0].length;
        int[] result = new int[n * m];
        int t = 0;

        for (int i = 0; i < n + m - 1; i++) {
            if (i % 2 == 1) {
                // down left
                int x = i < n ? 0 : i - n + 1;
                int y = i < n ? i : n - 1;
                while (x < m && y >= 0) {
                    result[t++] = dctTmp[x++][y--];
                }
            } else {
                // up right
                int x = i < m ? i : m - 1;
                int y = i < m ? 0 : i - m + 1;
                while (x >= 0 && y < n) {
                    result[t++] = dctTmp[x--][y++];
                }
            }
        }
        return result;
    }
	
	/*
	 * ZigZag encode a matrix of equal height and width into a byte array
	 * @returns a byte array containing the matrix elements in zigzag order
	 * 
	 */
	public static int[] toZigZag(int[][] dctTmp) {
        if (dctTmp == null || dctTmp.length == 0) {
            return null;
        }

        int m = dctTmp.length;
        int n = dctTmp[0].length;
        int[] result = new int[n * m];
        int t = 0;

        for (int i = 0; i < n + m - 1; i++) {
            if (i % 2 == 1) {
                // down left
                int x = i < n ? 0 : i - n + 1;
                int y = i < n ? i : n - 1;
                while (x < m && y >= 0) {
                    result[t++] = dctTmp[x++][y--];
                }
            } else {
                // up right
                int x = i < m ? i : m - 1;
                int y = i < m ? 0 : i - m + 1;
                while (x >= 0 && y < n) {
                    result[t++] = dctTmp[x--][y++];
                }
            }
        }
        return result;
    }
}
