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

import com.google.common.base.Objects;
import com.google.common.primitives.Doubles;

/**
 * Represents a MATLAB scalar.
 *
 * @author Richard Jones
 *
 */
public class MatlabScalar extends MatlabValue implements Comparable<MatlabScalar>{
    private final double scalar;

    /**
     * Creates a new <code>MLScalar</code> instance from a given
     * <code>double</code> scalar.
     *
     * @param scalar the <code>double</code> scalar
     */
    public MatlabScalar(double scalar) {
        this.scalar = scalar;
    }

    /**
     * Creates a new <code>MLScalar</code> instance from a given {@link Double}
     * scalar.
     *
     * @param scalar the <code>Double</code> scalar
     */
    public MatlabScalar(Double scalar) {
        this(checkNotNull(scalar).doubleValue());
    }

    /**
     * Returns the scalar.
     *
     * @return the scalar
     */
    public double value() {
        return scalar;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabScalar) {
            MatlabScalar other = (MatlabScalar) o;
            return Objects.equal(value(), other.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value());
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
    public int compareTo(MatlabScalar o) {
        return Doubles.compare(value(), checkNotNull(o).value());
    }

    @Override
    public MatlabScalar asScalar() {
        return this;
    }

    @Override
    public boolean isScalar() {
        return true;
    }
}
