package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * {@link MLResult} deserializer.
 * 
 * @author Richard Jones
 *
 */
public class MLResultDeserializer implements JsonDeserializer<MLResult> {

    @Override
	public MLResult deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		// get object
		JsonObject object = arg0.getAsJsonObject();
		
		// create
		MLResult mlresult = new MLResult();
		
		// add results
		JsonArray results = object.get("results").getAsJsonArray();
		for (JsonElement result : results) {
			mlresult.addResult(Common.deserializeValue(result));		
		}
		
		// return result
		return mlresult;
	}
	
}
