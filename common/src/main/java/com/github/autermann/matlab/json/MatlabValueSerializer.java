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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.joda.time.format.ISODateTimeFormat;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabDateTime;
import com.github.autermann.matlab.value.MatlabFile;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.value.ReturningMatlabValueVisitor;
import com.google.common.io.BaseEncoding;
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
        if (value == null) {
            return new JsonNull();
        }
        JsonObject object = new JsonObject();
        object.addProperty(MatlabJSONConstants.TYPE,
                           value.getType().toString());
        object.add(MatlabJSONConstants.VALUE,
                   value.accept(new VisitingSerializer(ctx)));
        return object;
    }

    private class VisitingSerializer implements
            ReturningMatlabValueVisitor<JsonElement> {
        private final JsonSerializationContext ctx;

        VisitingSerializer(JsonSerializationContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public JsonElement visit(MatlabArray array) {
            return ctx.serialize(array.value());
        }

        @Override
        public JsonElement visit(MatlabBoolean bool) {
            return ctx.serialize(bool.value());
        }

        @Override
        public JsonElement visit(MatlabCell cell) {
            return ctx.serialize(cell.value());
        }

        @Override
        public JsonElement visit(MatlabMatrix matrix) {
            return ctx.serialize(matrix.value());
        }

        @Override
        public JsonElement visit(MatlabScalar scalar) {
            return ctx.serialize(scalar.value());
        }

        @Override
        public JsonElement visit(MatlabString string) {
            return ctx.serialize(string.value());
        }

        @Override
        public JsonElement visit(MatlabStruct struct) {
            JsonObject object = new JsonObject();
            for (Entry<MatlabString, MatlabValue> e : struct.value().entrySet()) {
                object.add(e.getKey().value(),
                           serialize(e.getValue(), MatlabValue.class, ctx));
            }
            return object;
        }

        @Override
        public JsonElement visit(MatlabFile file) {
            if (!file.isLoaded()) {
                try {
                    file.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return ctx
                    .serialize(BaseEncoding.base64().encode(file.getContent()));
        }

        @Override
        public JsonElement visit(MatlabDateTime time) {
            return ctx.serialize(ISODateTimeFormat.dateTime().print(time.value()));
        }
    }
}
