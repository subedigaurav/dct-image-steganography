package encoder;

public class Quantization {

	public static final int[][] baseQM= {
			{16,    11,    10,    16,    24,    40,    51,    61},
		    {12,    12,    14,    19,    26,    58,    60,    55},
		    {14,    13,    16,    24,   40,    57,    69,    56},
		    {14,    17,    22,    29,    51,    87,    80,    62},
		    {18,    22,    37,    56,    68,   109,   103,    77},
		    {24,    35,    55,    64,    81,   104,   113,    92},
		    {49,    64,    78,    87,   103,   121,   120,   101},
		    {72,    92,    95,    98,   112,   100,   103,    99},
	};
	
	public final int[][] qMatrix = new int[8][8];

	Quantization(int quality) {
		initqMatrix(quality);
	}

	private void initqMatrix(int Q) {
		if (Q == 100) {
			for (int i = 0; i < baseQM.length; i++) {
				for (int j = 0; j < baseQM.length; j++) {
					qMatrix[i][j] = 1;
				}
			}
		}
		int S;
		if (Q < 50)
			S = (5000 / Q);
		else
			S = (200 - 2 * Q);
		for (int i = 0; i < qMatrix.length; i++) {
			for (int j = 0; j < qMatrix.length; j++) {
				qMatrix[i][j] = (int) Math.floor((S * baseQM[i][j] + 50) / 100);
				if (qMatrix[i][j] == 0)
					qMatrix[i][j] = 1;
			}
		}
	}
}