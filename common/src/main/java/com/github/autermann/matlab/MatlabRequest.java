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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.value.StringVisitor;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

/**
 * Represents a MATLAB function execution request.
 *
 * @author Richard Jones
 *
 */
public class MatlabRequest {
    private static final Function<Integer, String> RESULT_FUNCTION
            = new Function<Integer, String>() {
                @Override
                public String apply(Integer input) {
                    return "result" + input;
                }
            };
    private static final Joiner JOINER = Joiner.on(", ");
    private final String function;
    private int resultCount;
    private final List<MatlabValue> parameters;

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
        this.function = function;
        this.resultCount = 1;
        this.parameters = new LinkedList<MatlabValue>();
    }

    /**
     * Creates a new <code>MLRequest</code> instance for the given function name
     * and expected/requested number of
     * results.
     *
     * @param function    the name of the function
     * @param resultCount the expected/requested number of results
     */
    public MatlabRequest(String function, int resultCount) {
        this.function = function;
        this.resultCount = resultCount;
        this.parameters = new LinkedList<MatlabValue>();
    }

    /**
     * Adds a parameter {@link MatlabValue} to this request.
     *
     * @param parameter the parameter <code>MatlabValue</code> to add
     */
    public void addParameter(MatlabValue parameter) {
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
     * Returns a string representation of this request that is compatible with
     * MATLAB's <a
     * href="http://www.mathworks.com/help/techdoc/ref/eval.html">eval
     * function</a>.
     *
     * @return the eval string
     */
    public String toEvalString() {
        StringBuilder sb = new StringBuilder().append('[');
        JOINER.appendTo(sb, Iterables.transform(resultRange(), RESULT_FUNCTION));
        sb.append("] = feval('").append(getFunction());
        sb.append('\'');
        if (!parameters.isEmpty()) {
            JOINER.appendTo(sb.append(", "),
                            Iterables.transform(parameters,
                                                StringVisitor.create()));
        }
        sb.append(')');
        return sb.toString();
    }

    private ContiguousSet<Integer> resultRange() {
        return ContiguousSet.create(Range.closed(1, getResultCount()),
                                    DiscreteDomain.integers());
    }

    @Override
    public String toString() {
        return "MatlabRequest[" + toEvalString() + "]";
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
}
