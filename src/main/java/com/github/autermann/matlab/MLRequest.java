/*
 * Copyright (C) 2012-2013 by it's authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.autermann.matlab;

import java.util.LinkedList;
import java.util.List;

import com.github.autermann.matlab.value.MLValue;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Represents a MATLAB function execution request.
 * 
 * @author Richard Jones
 *
 */
public class MLRequest {

	private final String function;
	private int resultCount;
	private final List<MLValue> parameters;
	
	/**
	 * Creates a new <code>MLRequest</code> instance for the given function name. This constructor will assume 
	 * you are only expecting/requesting a single result value - use {@link #MLRequest(String, int)} or 
	 * {@link #setResultCount(int)} if you wish for more.
	 * 
	 * @param function the name of the function to execute
	 */
	public MLRequest(String function) {
		this.function = function;
        this.resultCount = 1;
		this.parameters = new LinkedList<MLValue>();
	}

	/**
	 * Creates a new <code>MLRequest</code> instance for the given function name and expected/requested number of 
	 * results.
	 * 
	 * @param function the name of the function
	 * @param resultCount the expected/requested number of results
	 */
	public MLRequest(String function, int resultCount) {
		this.function = function;
		this.resultCount = resultCount;
		this.parameters = new LinkedList<MLValue>();
	}

	/**
	 * Adds a parameter {@link MLValue} to this request.
	 * 
	 * @param parameter the parameter <code>MLValue</code> to add
	 */
	public void addParameter(MLValue parameter) {
		this.parameters.add(parameter);
	}
	
	/**
	 * Clears all parameters from this request.
	 * 
	 */
	public void clearParameters() {
		this.parameters.clear();
	}
	
	/**
	 * Returns the name of the function.
	 * 
	 * @return the name of the function to execute
	 */
	public String getFunction() {
		return this.function;
	}

	/**
	 * Returns a parameter {@link MLValue} at a given index.
	 * 
	 * @param index the index of the parameter (starts at 0)
	 * @return the parameter <code>MLValue</code> at the given index
	 */
	public MLValue getParameter(int index) {
		return this.parameters.get(index);
	}
	
	/**
	 * Returns the number of parameters.
	 * 
	 * @return the number of parameters
	 */
	public int getParameterCount() {
		return this.parameters.size();
	}
	
	/**
	 * Returns the number of expected/requested results.
	 * 
	 * @return the number of expected/requested results
	 */
	public int getResultCount() {
		return this.resultCount;
	}

	/**
	 * Sets the number of expected/requested results.
	 * 
	 * @param resultCount the number of expected/requested results
	 */
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	/**
	 * Returns a string representation of this request that is compatible with MATLAB's <a href="http://www.mathworks.com/help/techdoc/ref/eval.html">eval function</a>.
	 * 
	 * @return the eval string 
	 */
	public String toEvalString() {
		StringBuilder sb = new StringBuilder('[');
		for (int i = 1; i <= resultCount; i++) {
			sb.append("result").append(i);
			if (i < resultCount) {
				sb.append(',');
			}
		}
		sb.append("] = feval('");
        sb.append(getFunction());
        sb.append('\'');
        Joiner.on(",").appendTo(sb, Iterables.transform(parameters, MLValue.TO_MATLAB_STRING));
		sb.append(')');
		return sb.toString();
	}
}
