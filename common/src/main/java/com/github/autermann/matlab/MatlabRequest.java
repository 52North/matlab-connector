/*
 * Copyright (C) 2012-2015 by it's authors.
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.github.autermann.matlab.value.MatlabType;
import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.value.MatlabValueVisitor;
import com.google.common.base.MoreObjects;

/**
 * Represents a MATLAB function execution request.
 *
 * @author Richard Jones
 *
 */
public class MatlabRequest {
    private static final AtomicLong ids = new AtomicLong();
    private final long id;
    private final String function;
    private final List<MatlabValue> parameters;
    private final Map<String, MatlabType> results;

    /**
     * Creates a new <code>MLRequest</code> instance for the given function
     * name.
     *
     * @param function the name of the function to execute
     */
    public MatlabRequest(String function) {
        this(ids.incrementAndGet(), function);
    }

    /**
     * Creates a new <code>MLRequest</code> instance for the given function
     * name.
     *
     * @param id       the id
     * @param function the name of the function to execute
     */
    public MatlabRequest(long id, String function) {
        checkArgument(function != null && !function.isEmpty());
        this.function = function;
        this.results = new LinkedHashMap<>();
        this.parameters = new LinkedList<>();
        this.id = ids.incrementAndGet();
    }

    public long getId() {
        return this.id;
    }

    /**
     * Adds a parameter {@link MatlabValue} to this request.
     *
     * @param parameter the parameter <code>MatlabValue</code> to add
     *
     * @return {@code this}
     */
    public MatlabRequest addParameter(MatlabValue parameter) {
        this.parameters.add(Objects.requireNonNull(parameter));
        return this;
    }

    public MatlabRequest addParameters(
            Iterable<? extends MatlabValue> parameters) {
        for (MatlabValue parameter : parameters) {
            addParameter(parameter);
        }
        return this;
    }

    /**
     * Clears all parameters from this request.
     *
     * @return{@code this}
     */
    public MatlabRequest clearParameters() {
        this.parameters.clear();
        return this;
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

    public MatlabRequest addResult(String name, MatlabType type) {
        checkArgument(name != null && !name.isEmpty());
        checkArgument(!this.results.containsKey(name));
        this.results.put(name, type);
        return this;
    }

    public MatlabRequest addResult(Map<String, MatlabType> results) {
        checkArgument(results != null);
        results.forEach(this::addResult);
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("function", getFunction())
                .add("results", getResults())
                .add("parameters", getParameters())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFunction(), getParameters());

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabRequest) {
            MatlabRequest other = (MatlabRequest) o;
            return Objects.equals(getFunction(), other.getFunction()) &&
                   Objects.equals(getParameters(), other.getParameters());
        }
        return false;

    }

    public Map<String, MatlabType> getResults() {
        return Collections.unmodifiableMap(results);
    }

    public void visitParameters(MatlabValueVisitor visitor) {
        getParameters().forEach(v -> v.accept(visitor));
    }
}
