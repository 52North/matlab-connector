package org.uncertweb.matlab.value;

/**
 * Base class for representing a MATLAB value.
 * 
 * @author Richard Jones
 * 
 */
public abstract class MLValue {

	/**
	 * Returns a MATLAB string representation of this value.
	 * 
	 * @return a MATLAB string representation of this value
	 */
	public abstract String toMLString();

	/**
	 * Checks if this value is a scalar.
	 * 
	 * @return <code>true</code> if this value is a scalar, <code>false</code>
	 *         otherwise
	 */
	public boolean isScalar() {
		return (this instanceof MLScalar);
	}

	/**
	 * Checks if this value is a matrix.
	 * 
	 * @return <code>true</code> if this value is a matrix, <code>false</code>
	 *         otherwise
	 */
	public boolean isMatrix() {
		return (this instanceof MLMatrix);
	}

	/**
	 * Checks if this value is an array.
	 * 
	 * @return <code>true</code> if this value is an array, <code>false</code>
	 *         otherwise
	 */
	public boolean isArray() {
		return (this instanceof MLArray);
	}
	
	/**
	 * Checks if this value is a string.
	 * 
	 * @return <code>true</code> if this value is a string, <code>false</code>
	 *         otherwise
	 */
	public boolean isString() {
		return (this instanceof MLString);
	}
	
	/**
	 * Checks if this value is a cell.
	 * 
	 * @return <code>true</code> if this value is a cell, <code>false</code>
	 *         otherwise
	 */
	public boolean isCell() {
		return (this instanceof MLCell);
	}
	
	/**
	 * Checks if this value is a struct.
	 * 
	 * @return <code>true</code> if this value is a struct, <code>false</code>
	 *         otherwise
	 */
	public boolean isStruct() {
		return (this instanceof MLStruct);
	}

	/**
	 * Returns this value as a scalar. Will throw a {@link ClassCastException}
	 * if this value is not a scalar.
	 * 
	 * @return this value as a {@link MLScalar}
	 * @see #isScalar()
	 */
	public MLScalar getAsScalar() {
		return (MLScalar) this;
	}

	/**
	 * Returns this value as a matrix. Will throw a {@link ClassCastException}
	 * if this value is not a matrix.
	 * 
	 * @return this value as a {@link MLMatrix}
	 * @see #isMatrix()
	 */
	public MLMatrix getAsMatrix() {
		return (MLMatrix) this;
	}

	/**
	 * Returns this value as a array. Will throw a {@link ClassCastException}
	 * if this value is not a array.
	 * 
	 * @return this value as a {@link MLArray}
	 */
	public MLArray getAsArray() {
		return (MLArray) this;
	}
	
	/**
	 * Returns this value as a string. Will throw a {@link ClassCastException}
	 * if this value is not a string.
	 * 
	 * @return this value as a {@link MLString}
	 */
	public MLString getAsString() {
		return (MLString) this;
	}
	
	/**
	 * Returns this value as a cell. Will throw a {@link ClassCastException}
	 * if this value is not a cell.
	 * 
	 * @return this value as a {@link MLCell}
	 */
	public MLCell getAsCell() {
		return (MLCell) this;
	}
	
	/**
	 * Returns this value as a struct. Will throw a {@link ClassCastException}
	 * if this value is not a struct.
	 * 
	 * @return this value as a {@link MLStruct}
	 */
	public MLStruct getAsStruct() {
		return (MLStruct) this;
	}

}
