package org.uncertweb.matlab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.uncertweb.matlab.value.MLValue;

/**
 * Represents the result of a MATLAB function execution.
 * 
 * @author Richard Jones
 *
 */
public class MLResult implements Iterable<MLValue> {

	private final List<MLValue> results;

	/**
	 * Creates a new <code>MLResult</code> instance.
	 * 
	 */
	public MLResult() {
		results = new ArrayList<MLValue>();
	}
	
	/**
	 * Adds a result {@link MLValue}.
	 * @param result the result <code>MLValue</code>
	 */
	public void addResult(MLValue result) {
		results.add(result);
	}
	
	/**
	 * Returns a result {@link MLValue} at a given index.
	 * 
	 * @param index the index of the result (starts at 0)
	 * @return the result <code>MLValue</code> at the given index
	 */
	public MLValue getResult(int index) {
		return results.get(index);
	}
	
	/**
	 * Returns the number of result values.
	 * 
	 * @return the number of result values
	 */
	public int getResultCount() {
		return results.size();
	}
	
    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder("results = ");
		for (int i = 0; i < results.size(); i++) {
			builder.append("\n  ").append(results.get(i).toString());
		}
		return builder.toString();
	}

    @Override
    public Iterator<MLValue> iterator() {
        return this.results.iterator();
    }
	
}
