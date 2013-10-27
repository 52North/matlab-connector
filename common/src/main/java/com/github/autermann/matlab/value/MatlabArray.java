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

import com.google.common.base.Joiner;
import com.google.common.primitives.Doubles;

/**
 * Represents a MATLAB array.
 *
 * @author Richard Jones
 *
 */
// TODO: add support for struct
public class MatlabArray extends MatlabValue {

    private final double[] array;

    /**
     * Creates a new <code>MLArray</code> instance from the given
     * <code>double</code> array.
     *
     * @param array the <code>double</code> array
     */
    public MatlabArray(double[] array) {
        this.array = array;
    }

    /**
     * Creates a new <code>MLArray</code> instance from the given {@link Double}
     * array.
     *
     * @param array the <code>Double</code> array
     */
    public MatlabArray(Double[] array) {
        double[] values = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            values[i] = array[i];
        }
        this.array = values;
    }

    /**
     * Returns the array.
     *
     * @return the array
     */
    public double[] getArray() {
        return array;
    }

    @Override
    public String toMatlabString() {
        StringBuilder sb = new StringBuilder("[");
        Joiner joiner = Joiner.on(",");
        joiner.appendTo(sb, Doubles.asList(array));
        return sb.append(']').toString();
    }
}
