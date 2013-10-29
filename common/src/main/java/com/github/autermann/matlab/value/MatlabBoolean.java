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

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabBoolean extends MatlabValue {
    private static final MatlabBoolean TRUE = new MatlabBoolean(true, "1");
    private static final MatlabBoolean FALSE = new MatlabBoolean(false, "0");
    private final String string;
    private final boolean value;

    private MatlabBoolean(boolean value, String string) {
        this.string = string;
        this.value = value;
    }

    public boolean value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value());
    }

    @Override
    public <T extends MatlabValueVisitor> T accept(T visitor) {
        checkNotNull(visitor).visitBoolean(this);
        return visitor;
    }

    public static MatlabBoolean yes() {
        return TRUE;
    }

    public static MatlabBoolean no() {
        return FALSE;
    }

    public static MatlabBoolean fromBoolean(boolean bool) {
        return bool ? TRUE : FALSE;
    }
}
