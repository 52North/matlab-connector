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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * Represents the result of a MATLAB function execution.
 *
 * @author Richard Jones
 *
 */
public class MatlabResult implements Iterable<MatlabValue>, MatlabResponse {
    private static final MapJoiner JOINER = Joiner.on(", ").withKeyValueSeparator(" = ");
    private final LinkedHashMap<String, MatlabValue> results;
    private final long id;

    /**
     * Creates a new <code>MatlabResult</code> instance.
     *
     * @param id the request id
     */
    public MatlabResult(long id) {
        this.results = Maps.newLinkedHashMap();
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Adds a result {@link MatlabValue}.
     *
     * @param name
     * @param result the result <code>MatlabValue</code>
     * @return this
     */
    public MatlabResult addResult(String name, MatlabValue result) {
        checkNotNull(name);
        checkNotNull(result);
        this.results.put(name, result);
        return this;
    }

    public Map<String, MatlabValue> getResults() {
        return Collections.unmodifiableMap(results);
    }

    public MatlabValue getResult(String name) {
        return getResults().get(name);
    }

    /**
     * Returns the number of result values.
     *
     * @return the number of result values
     */
    public int getResultCount() {
        return getResults().size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("MatlabResult[");
        return JOINER.appendTo(sb, getResults()).append(']').toString();
    }

    @Override
    public Iterator<MatlabValue> iterator() {
        return getResults().values().iterator();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResults());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabResult) {
            MatlabResult other = (MatlabResult) o;
            return Objects.equal(getResults(), other.getResults());
        }
        return false;
    }

}
