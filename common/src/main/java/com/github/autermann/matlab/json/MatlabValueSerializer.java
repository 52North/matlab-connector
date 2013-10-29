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
package com.github.autermann.matlab.json;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.value.MatlabValueVisitor;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MatlabValue} serializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabValueSerializer implements JsonSerializer<MatlabValue> {
    @Override
    public JsonElement serialize(MatlabValue value, Type type,
                                 JsonSerializationContext ctx) {
        return value == null ? new JsonNull() : value
                .accept(new VisitingSerializer(ctx)).value();
    }

    private class VisitingSerializer implements MatlabValueVisitor {
        private final JsonSerializationContext ctx;
        private JsonElement value;

        VisitingSerializer(JsonSerializationContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void visitArray(MatlabArray array) {
            this.value = ctx.serialize(array.value());
        }

        @Override
        public void visitBoolean(MatlabBoolean bool) {
            this.value = ctx.serialize(bool.value());
        }

        @Override
        public void visitCell(MatlabCell cell) {
            JsonObject object = new JsonObject();
            object.add(MatlabJSONConstants.CELL,
                       ctx.serialize(cell.value()));
            this.value = object;
        }

        @Override
        public void visitMatrix(MatlabMatrix matrix) {
            this.value = ctx.serialize(matrix.value());
        }

        @Override
        public void visitScalar(MatlabScalar scalar) {
            this.value = ctx.serialize(scalar.value());
        }

        @Override
        public void visitString(MatlabString string) {
            this.value = ctx.serialize(string.value());
        }

        @Override
        public void visitStruct(MatlabStruct struct) {
            JsonObject object = new JsonObject();
            JsonObject structobj = new JsonObject();
            for (Entry<MatlabString, MatlabValue> e : struct.value().entrySet()) {
                structobj.add(e.getKey().value(), serialize(e.getValue(), MatlabValue.class, ctx));
            }
            object.add(MatlabJSONConstants.STRUCT, structobj);
            this.value = object;
        }

        public JsonElement value() {
            return value;
        }

    }
}
