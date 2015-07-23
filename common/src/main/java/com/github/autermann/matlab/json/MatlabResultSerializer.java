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
import java.util.Map.Entry;

import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MatlabResult} deserializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabResultSerializer implements JsonDeserializer<MatlabResult>,
                                               JsonSerializer<MatlabResult> {

    @Override
    public MatlabResult deserialize(JsonElement elem, Type type,
                                    JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject json = elem.getAsJsonObject();
        MatlabResult mlresult = new MatlabResult(json.get(MatlabJSONConstants.ID).getAsLong());
        JsonObject results = json.get(MatlabJSONConstants.RESULTS).getAsJsonObject();

        for (Entry<String, JsonElement> result : results.entrySet()) {
            MatlabValue value = ctx.deserialize(result.getValue(),
                                                MatlabValue.class);
            mlresult.addResult(result.getKey(), value);
        }
        return mlresult;
    }

    @Override
    public JsonElement serialize(MatlabResult src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject o = new JsonObject();
        JsonObject results = new JsonObject();
        for (Entry<String, MatlabValue> result : src.getResults().entrySet()) {
            results.add(result.getKey(), context.serialize(result.getValue()));
        }
        o.add(MatlabJSONConstants.RESULTS, results);

        return o;
    }
}
