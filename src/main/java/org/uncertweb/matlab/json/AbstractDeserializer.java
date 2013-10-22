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
package org.uncertweb.matlab.json;

import java.util.Map.Entry;

import org.uncertweb.matlab.value.MLArray;
import org.uncertweb.matlab.value.MLCell;
import org.uncertweb.matlab.value.MLMatrix;
import org.uncertweb.matlab.value.MLScalar;
import org.uncertweb.matlab.value.MLString;
import org.uncertweb.matlab.value.MLStruct;
import org.uncertweb.matlab.value.MLValue;

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
     * Deserializes an {@link MLValue} from a {@link JsonElement}.
     *
     * @param element the <code>JsonElement</code> containing a
     *                serialized <code>MLValue</code>
     *
     * @return the deserialized <code>MLValue</code>
     */
    public MLValue deserializeValue(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                // string
                return new MLString(primitive.getAsString());
            } else {
                // scalar
                return new MLScalar(primitive.getAsDouble());
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
                return new MLMatrix(values);
            } else {
                // array
                double[] values = new double[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    values[i] = array.get(i).getAsDouble();
                }
                return new MLArray(values);
            }
        } else if (element.isJsonObject()) {
            // potential cell
            JsonObject json = element.getAsJsonObject();
            if (json.has(JSONConstants.CELL)) {
                // definitely a cell
                JsonArray array = json.get(JSONConstants.CELL).getAsJsonArray();
                MLValue[] cell = new MLValue[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    cell[i] = deserializeValue(array.get(i));
                }
                return new MLCell(cell);
            } else if (json.has(JSONConstants.STRUCT)) {
                // definitely a struct
                JsonObject obj = json.get(JSONConstants.STRUCT)
                        .getAsJsonObject();
                MLStruct struct = new MLStruct();
                for (Entry<String, JsonElement> e : obj.entrySet()) {
                    struct.setField(e.getKey(), deserializeValue(e.getValue()));
                }
                return struct;
            }
        }

        // should never get here
        return null;
    }
}
