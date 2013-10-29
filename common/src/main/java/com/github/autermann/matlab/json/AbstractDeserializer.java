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
 * AbstractDeserializer JSON serializing and deserializing methods.
 *
 * @author Richard Jones
 *
 */
public class AbstractDeserializer {
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
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return new MatlabString(primitive.getAsString());
            } else if (primitive.isBoolean()) {
                return MatlabBoolean.fromBoolean(primitive.getAsBoolean());
            } else {
                return new MatlabScalar(primitive.getAsDouble());
            }
        } else if (element.isJsonArray()) {
            // array or matrix
            JsonArray array = element.getAsJsonArray();

            // have a peek to check for matrix
            if (array.get(0).isJsonArray()) {
                // matrix
                double[][] values = new double[array.size()][array.get(0)
                        .getAsJsonArray().size()];
                for (int i = 0; i < array.size(); i++) {
                    JsonArray innerArray = array.get(i).getAsJsonArray();
                    for (int j = 0; j < innerArray.size(); j++) {
                        values[i][j] = innerArray.get(j).getAsDouble();
                    }
                }
                return new MatlabMatrix(values);
            } else {
                // array
                double[] values = new double[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    values[i] = array.get(i).getAsDouble();
                }
                return new MatlabArray(values);
            }
        } else if (element.isJsonObject()) {
            // potential cell
            JsonObject json = element.getAsJsonObject();
            if (json.has(MatlabJSONConstants.CELL)) {
                // definitely a cell
                JsonArray array = json.get(MatlabJSONConstants.CELL)
                        .getAsJsonArray();
                MatlabValue[] cell = new MatlabValue[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    cell[i] = deserializeValue(array.get(i));
                }
                return new MatlabCell(cell);
            } else if (json.has(MatlabJSONConstants.STRUCT)) {
                // definitely a struct
                JsonObject obj = json.get(MatlabJSONConstants.STRUCT)
                        .getAsJsonObject();
                MatlabStruct struct = new MatlabStruct();
                for (Entry<String, JsonElement> e : obj.entrySet()) {
                    struct.set(e.getKey(), deserializeValue(e.getValue()));
                }
                return struct;
            }
        }

        // should never get here
        throw new IllegalArgumentException();
    }
}
