package org.uncertweb.matlab.value;

public class MLString extends MLValue {

	private String string;
	
	/**
	 * Creates a new <code>MLString</code> instance from a given <code>String</code>.
	 * 
	 * @param string the <code>String</code>
	 */
	public MLString(String string) {
		this.string = string;
	}
		
	/**
	 * Returns the string.
	 * 
	 * @return the string
	 */
	public String getString() {
		return string;
	}
	
	public String toMLString() {
		return "'" + string + "'";
	}
	
	public String toString() {
		return string;
	}

}
