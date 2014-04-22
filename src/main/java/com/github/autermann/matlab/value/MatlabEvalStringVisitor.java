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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

import org.joda.time.DateTimeZone;

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
public class MatlabEvalStringVisitor
        implements ReturningMatlabValueVisitor<String>,
                   Function<MatlabValue, String> {
    private static final String BOOL_FALSE = "logical(0)";
    private static final String BOOL_TRUE = "logical(1)";
    private static final String NaN = "NaN";
    private static final String POSITIVE_INFINITY = "Inf";
    private static final String NEGATIVE_INFINITY = "-Inf";
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final MapJoiner STRUCT_JOINER = COMMA_JOINER
            .withKeyValueSeparator(", ");
    private final MapTransformer mapTransformer = new MapTransformer();

    private MatlabEvalStringVisitor() {
    }

    @Override
    public String visit(MatlabArray value) {
        StringBuilder sb = new StringBuilder("[ ");
        COMMA_JOINER.appendTo(sb, toString(value.value()));
        return sb.append(" ]").toString();
    }

    @Override
    public String visit(MatlabBoolean value) {
        return value.value() ? BOOL_TRUE : BOOL_FALSE;
    }

    @Override
    public String visit(MatlabCell value) {
        StringBuilder sb = new StringBuilder().append("{ ");
        COMMA_JOINER.appendTo(sb, Iterables.transform(value.value(), this));
        return sb.append(" }").toString();
    }

    @Override
    public String visit(MatlabMatrix value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        double[][] matrix = value.value();
        for (int i = 0; i < matrix.length; ++i) {
            COMMA_JOINER.appendTo(builder, toString(matrix[i]));
            if (i < matrix.length - 1) {
                builder.append("; ");
            }
        }
        builder.append(" ]");
        return builder.toString();
    }

    private Iterable<String> toString(double[] values) {
        return Iterables.transform(Doubles.asList(values),
                                   new Function<Double, String>() {
            @Override
            public String apply(Double input) {
                return MatlabEvalStringVisitor.this.toString(input);
            }
        });
    }

    private String toString(Double v) {
        if (v.isNaN()) {
            return NaN;
        } else if (v.isInfinite()) {
            return v < 0 ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
        } else {
            return v.toString();
        }
    }

    @Override
    public String visit(MatlabScalar value) {
        return toString(value.value());
    }

    @Override
    public String visit(MatlabString value) {
        return toString(value.value());
    }

    private String toString(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    @Override
    public String visit(MatlabStruct value) {
        StringBuilder builder = new StringBuilder();
        builder.append("struct(");
        STRUCT_JOINER.appendTo(builder, Iterables
                .transform(value.value().entrySet(), mapTransformer()));
        builder.append(')');
        return builder.toString();
    }

    @Override
    public String apply(MatlabValue input) {
        return input.accept(this);
    }

    @Override
    public String visit(MatlabFile file) {
        if (file.isLoaded()) {
            try {
                return Arrays.toString(file.getContent());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return toString(file.getFile().getAbsolutePath());
        }
    }

    public MapTransformer mapTransformer() {
        return mapTransformer;
    }

    @Override
    public String visit(MatlabDateTime time) {
        return String.valueOf(time.value().toDateTime(DateTimeZone.UTC).getMillis());
    }

    public static MatlabEvalStringVisitor create() {
        return new MatlabEvalStringVisitor();
    }

    private class MapTransformer implements
            Function<Entry<? extends MatlabValue, ? extends MatlabValue>, Entry<String, String>> {
        @Override
        public Entry<String, String> apply(
                Entry<? extends MatlabValue, ? extends MatlabValue> input) {
            return Maps.immutableEntry(
                    input.getKey().accept(MatlabEvalStringVisitor.this),
                    input.getValue().accept(MatlabEvalStringVisitor.this));
        }
    }
}
