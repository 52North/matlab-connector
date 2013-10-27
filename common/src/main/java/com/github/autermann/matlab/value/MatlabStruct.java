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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class MatlabStruct extends MatlabValue {
    private static final MapJoiner JOINER
            = Joiner.on(", ").withKeyValueSeparator(", ");
    private static final Function<Entry<String, MatlabValue>, Entry<String, String>> TRANSFORMER
            = new Function<Entry<String, MatlabValue>, Entry<String, String>>() {
                @Override
                public Entry<String, String> apply(
                        Entry<String, MatlabValue> input) {
                    return Maps.immutableEntry(
                            "'" + input.getKey() + "'",
                            input.getValue().toMatlabString());
                }
            };

    private final Map<String, MatlabValue> fields;

    /**
     * Creates a new <code>MLStruct</code> instance.
     *
     */
    public MatlabStruct() {
        this.fields = Maps.newHashMap();
    }

    public void setField(String field, MatlabValue value) {
        fields.put(field, value);
    }

    public MatlabValue getField(String field) {
        return fields.get(field);
    }

    public Map<String, MatlabValue> getFields() {
        return fields;
    }

    @Override
    public String toMatlabString() {
        StringBuilder sb = new StringBuilder().append("struct(");
        JOINER.appendTo(sb, Iterables.transform(fields.entrySet(), TRANSFORMER));
        return sb.append(')').toString();
    }
}
