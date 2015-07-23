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
package com.github.autermann.matlab.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class MatlabStruct extends MatlabValue {
    private final Map<MatlabString, MatlabValue> fields;

    /**
     * Creates a new <code>MLStruct</code> instance.
     *
     */
    public MatlabStruct() {
        this(Maps.<MatlabString, MatlabValue>newTreeMap());
    }

    public MatlabStruct(Map<MatlabString, MatlabValue> value) {
        if (checkNotNull(value) instanceof SortedMap) {
            this.fields = value;
        } else {
            TreeMap<MatlabString, MatlabValue> sorted = Maps.newTreeMap();
            sorted.putAll(value);
            this.fields = sorted;
        }
    }

    public MatlabStruct set(String field, MatlabValue value) {
        return set(new MatlabString(checkNotNull(field)), value);
    }

    public MatlabStruct set(MatlabString field, MatlabValue value) {
        fields.put(checkNotNull(field), checkNotNull(value));
        return this;
    }

    public MatlabStruct remove(String field) {
        return remove(new MatlabString(checkNotNull(field)));
    }

    public MatlabStruct remove(MatlabString field) {
        fields.remove(checkNotNull(field));
        return this;
    }

    public MatlabValue get(String field) {
        return get(new MatlabString(checkNotNull(field)));
    }

    public MatlabValue get(MatlabString field) {
        return fields.get(checkNotNull(field));
    }

    public Map<MatlabString, MatlabValue> value() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabStruct) {
            MatlabStruct other = (MatlabStruct) o;
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
    public MatlabType getType() {
        return MatlabType.STRUCT;
    }
}
