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

import com.google.common.base.Function;

/**
 * Base class for representing a MATLAB value.
 *
 * @author Richard Jones
 *
 */
public abstract class MatlabValue {
    public static final Function<MatlabValue, String> TO_MATLAB_STRING
            = new Function<MatlabValue, String>() {
                @Override
                public String apply(MatlabValue input) {
                    return input == null ? null : input.toMatlabString();
                }
            };

    /**
     * Returns a MATLAB string representation of this value.
     *
     * @return a MATLAB string representation of this value
     */
    public abstract String toMatlabString();

    /**
     * Checks if this value is a scalar.
     *
     * @return <code>true</code> if this value is a scalar, <code>false</code>
     *         otherwise
     */
    public boolean isScalar() {
        return (this instanceof MatlabScalar);
    }

    /**
     * Checks if this value is a matrix.
     *
     * @return <code>true</code> if this value is a matrix, <code>false</code>
     *         otherwise
     */
    public boolean isMatrix() {
        return (this instanceof MatlabMatrix);
    }

    /**
     * Checks if this value is an array.
     *
     * @return <code>true</code> if this value is an array, <code>false</code>
     *         otherwise
     */
    public boolean isArray() {
        return (this instanceof MatlabArray);
    }

    /**
     * Checks if this value is a string.
     *
     * @return <code>true</code> if this value is a string, <code>false</code>
     *         otherwise
     */
    public boolean isString() {
        return (this instanceof MatlabString);
    }

    /**
     * Checks if this value is a cell.
     *
     * @return <code>true</code> if this value is a cell, <code>false</code>
     *         otherwise
     */
    public boolean isCell() {
        return (this instanceof MatlabCell);
    }

    /**
     * Checks if this value is a struct.
     *
     * @return <code>true</code> if this value is a struct, <code>false</code>
     *         otherwise
     */
    public boolean isStruct() {
        return (this instanceof MatlabStruct);
    }

    /**
     * Checks if this value is a boolean.
     *
     * @return <code>true</code> if this value is a boolean, <code>false</code>
     *         otherwise
     */
    public boolean isBoolean() {
        return (this instanceof MatlabBoolean);
    }

    /**
     * Returns this value as a scalar. Will throw a {@link ClassCastException}
     * if this value is not a scalar.
     *
     * @return this value as a {@link MatlabScalar}
     *
     * @see #isScalar()
     */
    public MatlabScalar asScalar() {
        return (MatlabScalar) this;
    }

    /**
     * Returns this value as a matrix. Will throw a {@link ClassCastException}
     * if this value is not a matrix.
     *
     * @return this value as a {@link MatlabMatrix}
     *
     * @see #isMatrix()
     */
    public MatlabMatrix asMatrix() {
        return (MatlabMatrix) this;
    }

    /**
     * Returns this value as a array. Will throw a {@link ClassCastException}
     * if this value is not a array.
     *
     * @return this value as a {@link MatlabArray}
     */
    public MatlabArray asArray() {
        return (MatlabArray) this;
    }

    /**
     * Returns this value as a string. Will throw a {@link ClassCastException}
     * if this value is not a string.
     *
     * @return this value as a {@link MatlabString}
     */
    public MatlabString asString() {
        return (MatlabString) this;
    }

    /**
     * Returns this value as a cell. Will throw a {@link ClassCastException}
     * if this value is not a cell.
     *
     * @return this value as a {@link MatlabCell}
     */
    public MatlabCell asCell() {
        return (MatlabCell) this;
    }

    /**
     * Returns this value as a struct. Will throw a {@link ClassCastException}
     * if this value is not a struct.
     *
     * @return this value as a {@link MatlabStruct}
     */
    public MatlabStruct asStruct() {
        return (MatlabStruct) this;
    }

    /**
     * Returns this value as a boolean. Will throw a {@link ClassCastException}
     * if this value is not a boolean.
     *
     * @return this value as a {@link MatlabBoolean}
     */
    public MatlabBoolean asBoolean() {
        return (MatlabBoolean) this;
    }

    @Override
    public String toString() {
        return String
                .format("%s[%s]", getClass().getSimpleName(), toMatlabString());
    }
}
