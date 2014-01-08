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

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabDateTime extends MatlabValue {
    private final DateTime time;

    public MatlabDateTime(DateTime time) {
        this.time = Preconditions.checkNotNull(time);
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
        return MatlabType.DATE_TIME;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabDateTime) {
            MatlabDateTime that = (MatlabDateTime) o;
            return this.value().equals(that.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    public DateTime value() {
        return time;
    }
}
