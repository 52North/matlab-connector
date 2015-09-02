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
package org.n52.matlab.connector.json;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.n52.matlab.connector.MatlabRequest;

import org.n52.matlab.connector.value.MatlabType;
import org.n52.matlab.connector.value.MatlabValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MatlabRequest} deserializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabRequestSerializer implements JsonSerializer<MatlabRequest>,
                                                JsonDeserializer<MatlabRequest> {

    @Override
    public MatlabRequest deserialize(JsonElement elem, Type type,
                                     JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject json = elem.getAsJsonObject();
        String function = json.get(MatlabJSONConstants.FUNCTION).getAsString();
        long id = json.get(MatlabJSONConstants.ID).getAsLong();
        MatlabRequest request = new MatlabRequest(id, function);

        JsonObject results = json.get(MatlabJSONConstants.RESULTS).getAsJsonObject();
        for (Entry<String, JsonElement> result : results.entrySet()) {
            request.addResult(result.getKey(), parseType(result.getValue()));
        }

        JsonArray parameters = json.get(MatlabJSONConstants.PARAMETERS)
                .getAsJsonArray();
        for (JsonElement parameter : parameters) {
            MatlabValue value = ctx
                    .deserialize(parameter, MatlabValue.class);
            request.addParameter(value);
        }

        return request;
    }

    private MatlabType parseType(JsonElement json) throws JsonParseException {
        String type = json.getAsString();
        try {
            return MatlabType.fromString(type);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown type: " + type);
        }
    }

    @Override
    public JsonElement serialize(MatlabRequest req, Type type,
                                 JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty(MatlabJSONConstants.ID, req.getId());
        o.addProperty(MatlabJSONConstants.FUNCTION, req.getFunction());
        o.add(MatlabJSONConstants.PARAMETERS, serializeParameters(req, ctx));
        o.add(MatlabJSONConstants.RESULTS, serializeResults(req));
        return o;
    }

    private JsonObject serializeResults(MatlabRequest req) {
        JsonObject results = new JsonObject();
        for (Entry<String, MatlabType> result : req.getResults().entrySet()) {
            results.add(result.getKey(),
                        new JsonPrimitive(result.getValue().toString()));
        }
        return results;
    }

    private JsonArray serializeParameters(MatlabRequest req,
                                          JsonSerializationContext ctx) {
        JsonArray parameters = new JsonArray();
        for (MatlabValue parameter : req.getParameters()) {
            parameters.add(ctx.serialize(parameter));
        }
        return parameters;

    }
}
