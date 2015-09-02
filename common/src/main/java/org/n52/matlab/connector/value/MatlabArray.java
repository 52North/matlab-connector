/*
 * Copyright (C) 2012-2015 by it's authors.
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
package org.n52.matlab.connector.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.joda.time.DateTime;

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
        this.array = checkNotNull(array);
    }

    /**
     * Creates a new <code>MLArray</code> instance from the given {@link Double}
     * array.
     *
     * @param array the <code>Double</code> array
     */
    public MatlabArray(Double[] array) {
        checkNotNull(array);
        double[] values = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            values[i] = checkNotNull(array[i]);
        }
        this.array = values;
    }

    /**
     * Returns the array.
     *
     * @return the array
     */
    public double[] value() {
        return array;
    }

    public int size() {
        return array.length;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabArray) {
            MatlabArray other = (MatlabArray) o;
            return Arrays.equals(value(), other.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value());
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
        return MatlabType.ARRAY;
    }

    public MatlabDateTime toDateTime() {
        if (value().length != 6) {
            throw new UnsupportedOperationException("invalid array length");
        }
        int year = (int) array[0];
        int month = (int) array[1];
        int day = (int) array[2];
        int hour = (int) array[3];
        int minute = (int) array[4];
        int second = (int) Math.floor(array[5]);
        int millis = (int) ((array[5] - second) * 1000);
        return new MatlabDateTime(new DateTime(year, month, day, hour, minute, second, millis));
    }
}
