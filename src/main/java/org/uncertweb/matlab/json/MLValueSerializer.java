package org.uncertweb.matlab.json;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.uncertweb.matlab.value.MLStruct;
import org.uncertweb.matlab.value.MLValue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MLValue} serializer.
 * 
 * @author Richard Jones
 *
 */
public class MLValueSerializer implements JsonSerializer<MLValue> {

	public JsonElement serialize(MLValue src, Type typeOfSrc, JsonSerializationContext context) {
		if (src.isScalar()) {
			return context.serialize(src.getAsScalar().getScalar());
		}
		else if (src.isArray()) {
			return context.serialize(src.getAsArray().getArray());
		}
		else if (src.isMatrix()) {
			return context.serialize(src.getAsMatrix().getMatrix());
		}
		else if (src.isString()) {
			return context.serialize(src.getAsString().getString()); 
		}
		else if (src.isCell()) {
			JsonObject object = new JsonObject();
			object.add("cell", context.serialize(src.getAsCell().getCell()));
			return object;
		}
		else if (src.isStruct()) {
			JsonObject object = new JsonObject();
			JsonObject structobj = new JsonObject();
			object.add("struct", structobj);
			MLStruct struct = (MLStruct)src.getAsStruct();
			for (Entry<String, MLValue> e : struct.getStruct().entrySet()) {
				structobj.add(e.getKey(), this.serialize(e.getValue(), MLValue.class, context));
			}
			return object;
		}
		
		// should never get here
		return null;
	}

	
	
}
