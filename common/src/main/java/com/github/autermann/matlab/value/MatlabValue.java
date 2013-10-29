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

/**
 * Base class for representing a MATLAB value.
 *
 * @author Richard Jones
 *
 */
public abstract class MatlabValue {
    public abstract <T extends MatlabValueVisitor> T accept(T visitor);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    /**
     * Checks if this value is a scalar.
     *
     * @return <code>true</code> if this value is a scalar, <code>false</code>
     *         otherwise
     */
    public boolean isScalar() {
        return false;
    }

    /**
     * Checks if this value is a matrix.
     *
     * @return <code>true</code> if this value is a matrix, <code>false</code>
     *         otherwise
     */
    public boolean isMatrix() {
        return false;
    }

    /**
     * Checks if this value is an array.
     *
     * @return <code>true</code> if this value is an array, <code>false</code>
     *         otherwise
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Checks if this value is a string.
     *
     * @return <code>true</code> if this value is a string, <code>false</code>
     *         otherwise
     */
    public boolean isString() {
        return false;
    }

    /**
     * Checks if this value is a cell.
     *
     * @return <code>true</code> if this value is a cell, <code>false</code>
     *         otherwise
     */
    public boolean isCell() {
        return false;
    }

    /**
     * Checks if this value is a struct.
     *
     * @return <code>true</code> if this value is a struct, <code>false</code>
     *         otherwise
     */
    public boolean isStruct() {
        return false;
    }

    /**
     * Checks if this value is a boolean.
     *
     * @return <code>true</code> if this value is a boolean, <code>false</code>
     *         otherwise
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * Returns this value as a scalar. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a scalar.
     *
     * @return this value as a {@link MatlabScalar}
     *
     * @see #isScalar()
     */
    public MatlabScalar asScalar() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a matrix. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a matrix.
     *
     * @return this value as a {@link MatlabMatrix}
     *
     * @see #isMatrix()
     */
    public MatlabMatrix asMatrix() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a array. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a array.
     *
     * @return this value as a {@link MatlabArray}
     */
    public MatlabArray asArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a string. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a string.
     *
     * @return this value as a {@link MatlabString}
     */
    public MatlabString asString() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a cell. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a cell.
     *
     * @return this value as a {@link MatlabCell}
     */
    public MatlabCell asCell() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a struct. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a struct.
     *
     * @return this value as a {@link MatlabStruct}
     */
    public MatlabStruct asStruct() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this value as a boolean. Will throw a
     * {@link UnsupportedOperationException}
     * if this value is not a boolean.
     *
     * @return this value as a {@link MatlabBoolean}
     */
    public MatlabBoolean asBoolean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(),
                             StringVisitor.create().apply(this));
    }
}
