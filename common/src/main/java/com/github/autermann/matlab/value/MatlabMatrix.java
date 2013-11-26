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

/**
 * Represents a MATLAB value.
 *
 * @author Richard Jones
 *
 */
public class MatlabMatrix extends MatlabValue {
    private final double[][] value;

    /**
     * Creates a new <code>MLMatrix</code> instance from the given
     * <code>double</code> value.
     *
     * @param matrix the <code>double</code> value
     */
    public MatlabMatrix(double[][] matrix) {
        this.value = checkNotNull(matrix);
    }

    /**
     * Creates a new <code>MLMatrix</code> instance from the given
     * {@link Double} value.
     *
     * @param matrix the <code>Double</code> value
     */
    public MatlabMatrix(Double[][] matrix) {
        double[][] values = new double[checkNotNull(matrix).length][];
        for (int i = 0; i < matrix.length; i++) {
            values[i] = new double[checkNotNull(matrix[i]).length];
            for (int j = 0; j < matrix[i].length; j++) {
                values[i][j] = checkNotNull(matrix[i][j]).doubleValue();
            }
        }
        this.value = values;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public double[][] value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabMatrix) {
            MatlabMatrix other = (MatlabMatrix) o;
            return Arrays.deepEquals(value(), other.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(value());
    }

    @Override
    public void accept(MatlabValueVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ReturningMatlabValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public MatlabType getType() {
        return MatlabType.MATRIX;
    }
}
