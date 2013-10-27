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

import java.util.Arrays;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Represents a MATLAB cell.
 *
 * @author Richard Jones
 */
public class MatlabCell extends MatlabValue {

    private final MatlabValue[] cell;

    /**
     * Creates a new <code>MLCell</code> instance from the given array of
     * <code>MatlabValue</code> objects.
     *
     * @param cell the cell, given as an array of <code>MatlabValue</code>
     *             objects
     */
    public MatlabCell(MatlabValue... cell) {
        this.cell = cell;
    }

    @Override
    public String toMatlabString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        Joiner.on(", ").appendTo(sb, Iterables
                .transform(Arrays.asList(cell), TO_MATLAB_STRING));
        sb.append(" }");
        return sb.toString();
    }

    /**
     * Returns the cell.
     *
     * @return the cell, as an array of <code>MatlabValue</code> objects
     */
    public MatlabValue[] getCell() {
        return cell;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabCell) {
            MatlabCell other = (MatlabCell) o;
            return Arrays.equals(getCell(), other.getCell());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getCell());
    }
}
