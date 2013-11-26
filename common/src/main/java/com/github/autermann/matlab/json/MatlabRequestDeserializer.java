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

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * {@link MatlabRequest} deserializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabRequestDeserializer implements
        JsonDeserializer<MatlabRequest> {

    @Override
    public MatlabRequest deserialize(JsonElement elem, Type type,
                                     JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject json = elem.getAsJsonObject();
        String function = json.get(MatlabJSONConstants.FUNCTION).getAsString();
        MatlabRequest request = new MatlabRequest(function);

        if (json.has(MatlabJSONConstants.RESULTS)) {
            JsonElement results = json.get(MatlabJSONConstants.RESULTS);
            if (results.isJsonPrimitive()) {
                request.addResult(results.getAsJsonPrimitive().getAsString());
            } else if (results.isJsonArray()) {
                for (JsonElement e : results.getAsJsonArray()) {
                    request.addResult(e.getAsString());
                }
            }
        }

        // add parameters
        JsonArray parameters = json.get(MatlabJSONConstants.PARAMETERS)
                .getAsJsonArray();
        for (JsonElement parameter : parameters) {
            MatlabValue value = ctx
                    .deserialize(parameter, MatlabValue.class);
            request.addParameter(value);
        }

        // return request
        return request;
    }
}
