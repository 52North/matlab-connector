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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.base.Objects;

/**
 * Represents a MATLAB function execution request.
 *
 * @author Richard Jones
 *
 */
public class MatlabRequest {
    private static final String DEFAULT_RESULT_NAME = "result";
    private final String function;
    private final List<MatlabValue> parameters;
    private final List<String> results;

    /**
     * Creates a new <code>MLRequest</code> instance for the given function
     * name. This constructor will assume
     * you are only expecting/requesting a single result value - use
     * {@link #MLRequest(String, int)} or
     * {@link #setResultCount(int)} if you wish for more.
     *
     * @param function the name of the function to execute
     */
    public MatlabRequest(String function) {
        checkArgument(function != null && !function.isEmpty());
        this.function = function;
        this.results = new LinkedList<String>();
        this.parameters = new LinkedList<MatlabValue>();
    }

    /**
     * Adds a parameter {@link MatlabValue} to this request.
     *
     * @param parameter the parameter <code>MatlabValue</code> to add
     */
    public void addParameter(MatlabValue parameter) {
        this.parameters.add(checkNotNull(parameter));
    }

    public void addParameters(Iterable<? extends MatlabValue> parameters) {
        for (MatlabValue parameter : parameters) {
            addParameter(parameter);
        }
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
     * Returns a parameter {@link MatlabValue} at a given index.
     *
     * @param index the index of the parameter (starts at 0)
     *
     * @return the parameter <code>MatlabValue</code> at the given index
     */
    public MatlabValue getParameter(int index) {
        return this.parameters.get(index);
    }

    public List<MatlabValue> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Returns the number of parameters.
     *
     * @return the number of parameters
     */
    public int getParameterCount() {
        return this.parameters.size();
    }

    public MatlabRequest addResult(String name) {
        checkArgument(name != null && !name.isEmpty());
        checkArgument(!this.results.contains(name));
        this.results.add(name);
        return this;
    }
    public MatlabRequest addResult(Iterable<? extends String> names) {
        checkArgument(names != null);
        for (String name : names) {
            addResult(name);
        }
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("function", getFunction())
                .add("results", getResults())
                .add("parameters", getParameters())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFunction(), getParameters());

    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabRequest) {
            MatlabRequest other = (MatlabRequest) o;
            return Objects.equal(getFunction(), other.getFunction()) &&
                   Objects.equal(getParameters(), other.getParameters());
        }
        return false;

    }

    public List<String> getResults() {
        if (results.isEmpty()) {
            return Collections.singletonList(DEFAULT_RESULT_NAME);
        }
        return Collections.unmodifiableList(results);
    }
}
