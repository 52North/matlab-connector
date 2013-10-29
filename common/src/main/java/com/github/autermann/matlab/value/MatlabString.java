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

public class MatlabString extends MatlabValue implements Comparable<MatlabString>{

    private final String string;

    /**
     * Creates a new <code>MLString</code> instance from a given
     * <code>String</code>.
     *
     * @param string the <code>String</code>
     */
    public MatlabString(String string) {
        this.string = checkNotNull(string);
    }

    /**
     * Returns the string.
     *
     * @return the string
     */
    public String value() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabString) {
            MatlabString other = (MatlabString) o;
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
        checkNotNull(visitor).visitString(this);
        return visitor;
    }

    @Override
    public int compareTo(MatlabString o) {
        return value().compareTo(checkNotNull(o).value());
    }
}
