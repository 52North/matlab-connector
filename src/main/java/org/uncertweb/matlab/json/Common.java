package org.uncertweb.matlab.json;

import org.uncertweb.matlab.value.MLArray;
import org.uncertweb.matlab.value.MLCell;
import org.uncertweb.matlab.value.MLMatrix;
import org.uncertweb.matlab.value.MLScalar;
import org.uncertweb.matlab.value.MLString;
import org.uncertweb.matlab.value.MLValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Common JSON serializing and deserializing methods.
 * 
 * @author Richard Jones
 *
 */
public class Common {

	/**
	 * Deserializes an {@link MLValue} from a {@link JsonElement}.
	 * 
	 * @param element the <code>JsonElement</code> containing a serialized <code>MLValue</code>
	 * @return the deserialized <code>MLValue</code>
	 */
	public static MLValue deserializeValue(JsonElement element) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive(); 
			if (primitive.isString()) {
				// string
				return new MLString(primitive.getAsString());
			}
			else {
				// scalar
				return new MLScalar(primitive.getAsDouble());
			}
		}
		else if (element.isJsonArray()) {
			// array or matrix
			JsonArray array = element.getAsJsonArray();
			
			// have a peek to check for matrix
			if (array.get(0).isJsonArray()) {
				// matrix
				double[][] values = new double[array.size()][array.get(0).getAsJsonArray().size()];
				for (int i = 0; i < array.size(); i++) {
					JsonArray innerArray = array.get(i).getAsJsonArray();
					for (int j = 0; j < innerArray.size(); j++) {
						values[i][j] = innerArray.get(j).getAsDouble();
					}
				}
				return new MLMatrix(values);
			}
			else {
				// array
				double[] values = new double[array.size()];
				for (int i = 0; i < array.size(); i++) {
					values[i] = array.get(i).getAsDouble();
				}
				return new MLArray(values);
			}				
		}
		else if (element.isJsonObject()) {
			// potential cell
			JsonObject object = element.getAsJsonObject();
			if (object.has("cell")) {
				// definitely a cell
				JsonArray array = object.get("cell").getAsJsonArray();
				MLValue[] cell = new MLValue[array.size()];
				for (int i = 0; i < array.size(); i++) {
					cell[i] = Common.deserializeValue(array.get(i));
				}
				return new MLCell(cell);
			}
		}
		
		// should never get here
		return null;
	}
	
}
