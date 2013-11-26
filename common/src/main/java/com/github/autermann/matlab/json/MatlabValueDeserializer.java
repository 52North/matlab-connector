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
import com.github.autermann.matlab.value.MatlabFile;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabType;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * MatlabValueDeserializer JSON serializing and deserializing methods.
 *
 * @author Richard Jones
 *
 */
public class MatlabValueDeserializer implements JsonDeserializer<MatlabValue> {

    @Override
    public MatlabValue deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context)
            throws JsonParseException {
        return deserializeValue(json);
    }

    /**
     * Deserializes an {@link MatlabValue} from a {@link JsonElement}.
     *
     * @param element the <code>JsonElement</code> containing a
     *                serialized <code>MatlabValue</code>
     *
     * @return the deserialized <code>MatlabValue</code>
     */
    public MatlabValue deserializeValue(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new JsonParseException("expected JSON object");
        }
        JsonObject json = element.getAsJsonObject();
        MatlabType type = getType(json);
        JsonElement value = json.get(MatlabJSONConstants.VALUE);
        switch (type) {
            case ARRAY:
                return parseMatlabArray(value);
            case BOOLEAN:
                return parseMatlabBoolean(value);
            case CELL:
                return parseMatlabCell(value);
            case FILE:
                return parseMatlabFile(value);
            case MATRIX:
                return parseMatlabMatrix(value);
            case SCALAR:
                return parseMatlabScalar(value);
            case STRING:
                return parseMatlabString(value);
            case STRUCT:
                return parseMatlabStruct(value);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }

    private MatlabType getType(JsonObject json) throws JsonParseException {
        String type = json.get(MatlabJSONConstants.TYPE).getAsString();
        try {
            return MatlabType.fromString(type);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown type: " + type);
        }
    }

    private MatlabMatrix parseMatlabMatrix(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        double[][] values = new double[array.size()][array.get(0)
                .getAsJsonArray().size()];
        for (int i = 0; i < array.size(); i++) {
            JsonArray innerArray = array.get(i).getAsJsonArray();
            for (int j = 0; j < innerArray.size(); j++) {
                values[i][j] = innerArray.get(j).getAsDouble();
            }
        }
        return new MatlabMatrix(values);
    }

    private MatlabArray parseMatlabArray(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        double[] values = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return new MatlabArray(values);
    }

    private MatlabStruct parseMatlabStruct(JsonElement value) {
        MatlabStruct struct = new MatlabStruct();
        for (Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
            struct.set(e.getKey(), deserializeValue(e.getValue()));
        }
        return struct;
    }

    private MatlabCell parseMatlabCell(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        MatlabValue[] cell = new MatlabValue[array.size()];
        for (int i = 0; i < array.size(); i++) {
            cell[i] = deserializeValue(array.get(i));
        }
        return new MatlabCell(cell);
    }

    private MatlabScalar parseMatlabScalar(JsonElement value) {
        return new MatlabScalar(value.getAsDouble());
    }

    private MatlabBoolean parseMatlabBoolean(JsonElement value) {
        return MatlabBoolean.fromBoolean(value.getAsBoolean());
    }

    private MatlabString parseMatlabString(JsonElement value) {
        return new MatlabString(value.getAsString());
    }

    private MatlabFile parseMatlabFile(JsonElement value) {
        return new MatlabFile(BaseEncoding.base64().decode(value.getAsString()));
    }
}
