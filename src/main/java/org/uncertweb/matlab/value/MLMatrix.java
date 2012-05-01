package org.uncertweb.matlab.value;

import java.util.Arrays;

/**
 * Represents a MATLAB matrix.
 * 
 * @author Richard Jones
 *
 */
public class MLMatrix extends MLValue {

	private double[][] matrix;
	
	/**
	 * Creates a new <code>MLMatrix</code> instance from the given <code>double</code> matrix.
	 * 
	 * @param matrix the <code>double</code> matrix
	 */
	public MLMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * Creates a new <code>MLMatrix</code> instance from the given {@link Double} matrix.
	 * 
	 * @param matrix the <code>Double</code> matrix
	 */
	public MLMatrix(Double[][] matrix) {
		double[][] values = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				values[i][j] = matrix[i][j];
			}
		}
		this.matrix = values;
	}
	
	/**
	 * Returns the matrix.
	 * 
	 * @return the matrix
	 */
	public double[][] getMatrix() {
		return matrix;
	}

	public String toMLString() {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				builder.append(matrix[i][j]);
				if (j < matrix[i].length - 1) {
					builder.append(",");
				}
			}
			if (i < matrix.length - 1) {
				builder.append(";");
			}
		}
		builder.append("]");
		return builder.toString();
	}
	
	public String toString() {
		return Arrays.deepToString(matrix);
	}
	
}
