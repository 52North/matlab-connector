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

/**
 * Represents a MATLAB matrix.
 *
 * @author Richard Jones
 *
 */
public class MatlabMatrix extends MatlabValue {

    private final double[][] matrix;

    /**
     * Creates a new <code>MLMatrix</code> instance from the given
     * <code>double</code> matrix.
     *
     * @param matrix the <code>double</code> matrix
     */
    public MatlabMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Creates a new <code>MLMatrix</code> instance from the given
     * {@link Double} matrix.
     *
     * @param matrix the <code>Double</code> matrix
     */
    public MatlabMatrix(Double[][] matrix) {
        double[][] values = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                values[i][j] = matrix[i][j];
            }
        }
        this.matrix = values;
    }

    /**
     * Returns the matrix.
     *
     * @return the matrix
     */
    public double[][] getMatrix() {
        return matrix;
    }

    @Override
    public String toMatlabString() {
        StringBuilder builder = new StringBuilder('[');
        final Joiner joiner = Joiner.on(",");
        for (int i = 0; i < matrix.length; i++) {
            joiner.appendTo(builder, Arrays.asList(matrix[i]));
            if (i < matrix.length - 1) {
                builder.append(';');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public String toString() {
        return Arrays.deepToString(matrix);
    }

}
