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

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class StringVisitor implements MatlabValueVisitor,
                                      Function<MatlabValue, String> {
    private static final String BOOL_FALSE = "0";
    private static final String BOOL_TRUE = "1";
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final MapJoiner STRUCT_JOINER = COMMA_JOINER.withKeyValueSeparator(", ");
    String value;

    private StringVisitor() {
    }

    @Override
    public void visitArray(MatlabArray value) {
        StringBuilder sb = new StringBuilder("[ ");
        Joiner joiner = Joiner.on(", ");
        joiner.appendTo(sb, Doubles.asList(value.value()));
        this.value = sb.append(" ]").toString();
    }

    @Override
    public void visitBoolean(MatlabBoolean value) {
        this.value = value.value() ? BOOL_TRUE : BOOL_FALSE;
    }


    @Override
    public void visitCell(MatlabCell value) {
        StringBuilder sb = new StringBuilder().append("{ ");
        COMMA_JOINER.appendTo(sb, Iterables.transform(value.value(), this));
        this.value = sb.append(" }").toString();
    }

    @Override
    public void visitMatrix(MatlabMatrix value) {
        StringBuilder builder = new StringBuilder().append("[ ");
        double[][] matrix = value.value();
        for (int i = 0; i < matrix.length; ++i) {
            COMMA_JOINER.appendTo(builder, Doubles.asList(matrix[i]));
            if (i < matrix.length - 1) {
                builder.append("; ");
            }
        }
        this.value = builder.append(" ]").toString();
    }

    @Override
    public void visitScalar(MatlabScalar value) {
        this.value = String.valueOf(value.value());
    }

    @Override
    public void visitString(MatlabString value) {
        this.value = "'" + value.value().replace("'", "''") + "'";
    }

    @Override
    public void visitStruct(MatlabStruct value) {
        StringBuilder sb = new StringBuilder().append("struct(");
        STRUCT_JOINER.appendTo(sb, Iterables.transform(value.value().entrySet(),
                            new Function<Entry<MatlabString, MatlabValue>, Entry<String, String>>() {
                @Override
                public Entry<String, String> apply(Entry<MatlabString, MatlabValue> input) {
                            return Maps.immutableEntry(
                                    StringVisitor.this.apply(input.getKey()),
                                    StringVisitor.this.apply(input.getValue()));
                        }
            }));
        this.value = sb.append(')').toString();
    }

    public String value() {
        return this.value;
    }

    @Override
    public String apply(MatlabValue input) {
        return input.accept(this).value();
    }

    public static StringVisitor create() {
        return new StringVisitor();
    }
}
