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

import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.gson.JsonElement;
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
        if (value.isScalar()) {
            return ctx.serialize(value.asScalar().getScalar());
        } else if (value.isArray()) {
            return ctx.serialize(value.asArray().getArray());
        } else if (value.isMatrix()) {
            return ctx.serialize(value.asMatrix().getMatrix());
        } else if (value.isString()) {
            return ctx.serialize(value.asString().getString());
        } else if (value.isCell()) {
            JsonObject object = new JsonObject();
            object.add(MatlabJSONConstants.CELL,
                       ctx.serialize(value.asCell().getCell()));
            return object;
        } else if (value.isStruct()) {
            JsonObject object = new JsonObject();
            JsonObject structobj = new JsonObject();
            object.add(MatlabJSONConstants.STRUCT, structobj);
            MatlabStruct struct = value.asStruct();
            for (Entry<String, MatlabValue> e : struct.getStruct().entrySet()) {
                structobj.add(e.getKey(),
                              serialize(e.getValue(), MatlabValue.class, ctx));
            }
            return object;
        }

        // should never get here
        return null;
    }
}
