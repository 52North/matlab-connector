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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.autermann.matlab.value.MLValue;

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
     *
     * @param result the result <code>MLValue</code>
     */
    public void addResult(MLValue result) {
        results.add(result);
    }

    /**
     * Returns a result {@link MLValue} at a given index.
     *
     * @param index the index of the result (starts at 0)
     *
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
