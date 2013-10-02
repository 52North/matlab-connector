package org.uncertweb.matlab.value;

import java.util.Arrays;

import com.google.common.base.Joiner;

/**
 * Represents a MATLAB array.
 * 
 * @author Richard Jones
 *
 */
// TODO: add support for struct
public class MLArray extends MLValue {

	private final double[] array;
	
	/**
	 * Creates a new <code>MLArray</code> instance from the given <code>double</code> array.
	 * 
	 * @param array the <code>double</code> array
	 */
	public MLArray(double[] array) {
		this.array = array;
	}
	
	/**
	 * Creates a new <code>MLArray</code> instance from the given {@link Double} array.
	 * 
	 * @param array the <code>Double</code> array
	 */
	public MLArray(Double[] array) {
		double[] values = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = array[i];
		}
		this.array = values;
	}
	
	/**
	 * Returns the array.
	 * 
	 * @return the array
	 */
	public double[] getArray() {
		return array;
	}

    @Override
    public String toMLString() {
        return Joiner.on(",").appendTo(new StringBuilder('['), Arrays
                .asList(array)).append(']').toString();
    }
	
    @Override
	public String toString() {
		return Arrays.toString(array);
	}
	
}
