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
package com.github.autermann.matlab.json;

import java.lang.reflect.Type;

import com.github.autermann.matlab.MatlabException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MatlabException} serializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabExceptionSerializer
        implements JsonSerializer<MatlabException>,
                   JsonDeserializer<MatlabException> {

    @Override
    public JsonElement serialize(MatlabException e, Type type,
                                 JsonSerializationContext ctx) {
        JsonObject object = new JsonObject();
        object.add(MatlabJSONConstants.ID, ctx.serialize(e.getId()));
        object.add(MatlabJSONConstants.EXCEPTION, ctx.serialize(e.getMessage()));
        return object;
    }

    @Override
    public MatlabException deserialize(JsonElement elem, Type type,
                                       JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject o = elem.getAsJsonObject();
        String message = o.get(MatlabJSONConstants.EXCEPTION).getAsString();
        MatlabException exception = new MatlabException(message);
        exception.setId(o.get(MatlabJSONConstants.ID).getAsLong());
        return exception;
    }

}
