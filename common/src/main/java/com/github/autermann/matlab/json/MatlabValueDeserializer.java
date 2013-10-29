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

import java.util.Map.Entry;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * MatlabValueDeserializer JSON serializing and deserializing methods.
 *
 * @author Richard Jones
 *
 */
public class MatlabValueDeserializer {
    /**
     * Deserializes an {@link MatlabValue} from a {@link JsonElement}.
     *
     * @param element the <code>JsonElement</code> containing a
     *                serialized <code>MatlabValue</code>
     *
     * @return the deserialized <code>MatlabValue</code>
     */
    public MatlabValue deserializeValue(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return deserializePrimitive(element.getAsJsonPrimitive());
        } else if (element.isJsonArray()) {
            return deserializeArray(element.getAsJsonArray());
        } else if (element.isJsonObject()) {
            return deserializeObject(element.getAsJsonObject());
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected MatlabValue deserializeObject(JsonObject json) {
        if (json.has(MatlabJSONConstants.CELL)) {
            return parseMatlabCell(json.get(MatlabJSONConstants.CELL).getAsJsonArray());
        } else if (json.has(MatlabJSONConstants.STRUCT)) {
            return parseMatlabStruct(json.get(MatlabJSONConstants.STRUCT).getAsJsonObject());
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected MatlabValue deserializeArray(JsonArray array) {
        // have a peek to check for matrix
        if (array.get(0).isJsonArray()) {
            return parseMatlabMatrix(array);
        } else {
            return parseMatlabArray(array);
        }
    }

    protected MatlabValue deserializePrimitive(JsonPrimitive primitive) {
        if (primitive.isString()) {
            return parseMatlabString(primitive);
        } else if (primitive.isBoolean()) {
            return parseMatlabBoolean(primitive);
        } else {
            return parseMatlabScalar(primitive);
        }
    }

    protected MatlabMatrix parseMatlabMatrix(JsonArray array) {
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

    protected MatlabArray parseMatlabArray(JsonArray array) {
        double[] values = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return new MatlabArray(values);
    }

    protected MatlabStruct parseMatlabStruct(JsonObject json) {
        MatlabStruct struct = new MatlabStruct();
        for (Entry<String, JsonElement> e : json.entrySet()) {
            struct.set(e.getKey(), deserializeValue(e.getValue()));
        }
        return struct;
    }

    protected MatlabCell parseMatlabCell(JsonArray array) {
        MatlabValue[] cell = new MatlabValue[array.size()];
        for (int i = 0; i < array.size(); i++) {
            cell[i] = deserializeValue(array.get(i));
        }
        return new MatlabCell(cell);
    }

    protected MatlabScalar parseMatlabScalar(JsonPrimitive primitive) {
        return new MatlabScalar(primitive.getAsDouble());
    }

    protected MatlabBoolean parseMatlabBoolean(JsonPrimitive primitive) {
        return MatlabBoolean.fromBoolean(primitive.getAsBoolean());
    }

    protected MatlabString parseMatlabString(JsonPrimitive primitive) {
        return new MatlabString(primitive.getAsString());
    }
}
