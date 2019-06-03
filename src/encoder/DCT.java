package encoder;

public class DCT {
	// Block Size
	public int N = 8;
	// Transformed Cosine Matrix
	public double cT[][] = new double[N][N];
	// Cosine Matrix
	public double cosM[][] = new double[N][N];
	static double C1 = 0.98078528, C2 = 0.923879532, C3 = 0.831469612, C4 = 0.707106781, C5 = 0.555570233,
			C6 = 0.382683432, C7 = 0.195090322;

	public DCT() {
		initMatrices();
	}

	private void initMatrices() {
		int i;
		int j;

		// INITIALIZE COSINE TRANSFORM MATRIX
		for (j = 0; j < N; j++) {
			double nn = (double) (N);
			cosM[0][j] = 1.0 / Math.sqrt(nn);
			cT[j][0] = cosM[0][j];
		}

		for (i = 1; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				double jj = (double) j;
				double ii = (double) i;
				cosM[i][j] = Math.sqrt(2.0 / 8.0) * Math.cos(((2.0 * jj + 1.0) * ii * Math.PI) / (2.0 * 8.0));
				cT[j][i] = cosM[i][j];
			}
		}
	}

	// This function performs forward DCT transformation with the result stored in
	// output matrix
	public short[][] forwardDCT(short[][] dctTmp) {
		short[][] output = new short[N][N];
		double temp[][] = new double[N][N];
		double temp1;
		int i;
		int j;
		int k;

		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				temp[i][j] = 0.0;
				for (k = 0; k < N; k++) {
					temp[i][j] += (((dctTmp[i][k])) * cT[k][j]);
				}
			}
		}

		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				temp1 = 0.0;

				for (k = 0; k < N; k++) {
					temp1 += (cosM[i][k] * temp[k][j]);
				}

				output[i][j] = (short) Math.round(temp1);
			}
		}

		return output;
	}

	private double[] c = new double[N];

	private void initializeCoefficients() {
		for (int i = 1; i < N; i++) {
			c[i] = 1;
		}
		c[0] = 1 / Math.sqrt(2.0);
	}

	public int[][] applyDCT(int[][] f) {
		initializeCoefficients();
		int[][] F = new int[N][N];
		for (int u = 0; u < N; u++) {
			for (int v = 0; v < N; v++) {
				double sum = 0.0;
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI)
								* Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * f[i][j];
					}
				}
				sum *= ((c[u] * c[v]) / 4.0);
				F[u][v] = (int) sum;
			}
		}
		return F;
	}

	public int[][] applyIDCT(int[][] F) {
		initializeCoefficients();
		int[][] f = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				double sum = 0.0;
				for (int u = 0; u < N; u++) {
					for (int v = 0; v < N; v++) {
						sum += (c[u] * c[v]) / 4.0 * Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI)
								* Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * F[u][v];
					}
				}
				f[i][j] = (int) Math.round(sum);
			}
		}
		return f;
	}

	public static double pi = 3.142857;

	/*
	 * Computes the discrete cosine transform
	 */
	int[][] dctExtremeTransform(int matrix[][]) {
		int i, j, k, l, N = 8;

		// dct will store the discrete cosine transform
		double[][] dct = new double[N][N];

		double ci, cj, dct1, sum;

		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				// ci and cj depends on frequency as well as
				// number of row and columns of specified matrix
				if (i == 0)
					ci = 1 / Math.sqrt(N);
				else
					ci = Math.sqrt(2) / Math.sqrt(N);

				if (j == 0)
					cj = 1 / Math.sqrt(N);
				else
					cj = Math.sqrt(2) / Math.sqrt(N);

				// sum will temporarily store the sum of
				// cosine signals
				sum = 0;
				for (k = 0; k < N; k++) {
					for (l = 0; l < N; l++) {
						dct1 = matrix[k][l] * Math.cos((2 * k + 1) * i * pi / (2 * N))
								* Math.cos((2 * l + 1) * j * pi / (2 * N));
						sum = sum + dct1;
					}
				}
				dct[i][j] = ci * cj * sum;
			}
		}

		int[][] op = new int[8][8];
		for (int m = 0; m < dct.length; m++) {
			for (int m2 = 0; m2 < dct.length; m2++) {
				op[m][m2] = (int) dct[m][m2];
			}
		}
		return op;
	}
}