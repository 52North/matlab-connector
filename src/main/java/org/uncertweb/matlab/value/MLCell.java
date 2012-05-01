package org.uncertweb.matlab.value;

/**
 * Represents a MATLAB cell.
 * 
 * @author Richard Jones
 */
public class MLCell extends MLValue {

	private MLValue[] cell;
	
	/**
	 * Creates a new <code>MLCell</code> instance from the given array of <code>MLValue</code> objects.
	 * 
	 * @param cell the cell, given as an array of <code>MLValue</code> objects
	 */
	public MLCell(MLValue[] cell) {
		this.cell = cell;
	}
	
	@Override
	public String toMLString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (MLValue value : cell) {
			sb.append(value.toMLString());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * Returns the cell.
	 * 
	 * @return the cell, as an array of <code>MLValue</code> objects
	 */
	public MLValue[] getCell() {
		return cell;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (MLValue value : cell) {
			if (value.isCell()) {
				sb.append(value.toString());
			}
			else {
				sb.append(value.toString());
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}

}
