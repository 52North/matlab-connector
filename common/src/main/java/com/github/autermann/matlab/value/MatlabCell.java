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
package com.github.autermann.matlab.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Represents a MATLAB value.
 *
 * @author Richard Jones
 */
public class MatlabCell extends MatlabValue {
    private final List<MatlabValue> value;

    /**
     * Creates a new <code>MLCell</code> instance from the given array of
     * <code>MatlabValue</code> objects.
     *
     * @param cell the value, given as an array of <code>MatlabValue</code>
     *             objects
     */
    public MatlabCell(MatlabValue... cell) {
        this(Arrays.asList(checkNotNull(cell)));
    }

    public MatlabCell(Iterable<? extends MatlabValue> values) {
        this.value = Lists.newArrayList(checkNotNull(values));
    }

    public MatlabCell add(MatlabValue value) {
        this.value.add(checkNotNull(value));
        return this;
    }

    /**
     * Returns the value.
     *
     * @return the value, as an array of <code>MatlabValue</code> objects
     */
    public List<MatlabValue> value() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabCell) {
            MatlabCell other = (MatlabCell) o;
            return Objects.equal(value(), other.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value());
    }

    @Override
    public <T extends MatlabValueVisitor> T accept(T visitor) {
        checkNotNull(visitor).visitCell(this);
        return visitor;
    }

    @Override
    public MatlabCell asCell() {
        return this;
    }

    @Override
    public boolean isCell() {
        return true;
    }
}
