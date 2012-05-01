package org.uncertweb.matlab.value;

/**
 * Represents a MATLAB scalar.
 * 
 * @author Richard Jones
 *
 */
public class MLScalar extends MLValue {

	private double scalar;
	
	/**
	 * Creates a new <code>MLScalar</code> instance from a given <code>double</code> scalar.
	 * 
	 * @param scalar the <code>double</code> scalar
	 */
	public MLScalar(double scalar) {
		this.scalar = scalar;
	}
	
	/**
	 * Creates a new <code>MLScalar</code> instance from a given {@link Double} scalar.
	 * 
	 * @param scalar the <code>Double</code> scalar
	 */
	public MLScalar(Double scalar) {
		this.scalar = scalar;
	}
	
	/**
	 * Returns the scalar.
	 * 
	 * @return the scalar
	 */
	public double getScalar() {
		return scalar;
	}
	
	public String toMLString() {
		return String.valueOf(scalar);
	}
	
	public String toString() {
		return String.valueOf(scalar);
	}
	
}
