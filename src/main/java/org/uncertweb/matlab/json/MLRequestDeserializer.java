package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * {@link MLRequest} deserializer.
 *  
 * @author Richard Jones
 *
 */
public class MLRequestDeserializer implements JsonDeserializer<MLRequest> {

    @Override
	public MLRequest deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		// get object
		JsonObject object = arg0.getAsJsonObject();
		
		// get function name and create request
		String function = object.get("function").getAsString();
		MLRequest request = new MLRequest(function);
		
		// add result count if it exists
		if (object.has("resultCount")) {
			request.setResultCount(object.get("resultCount").getAsInt());
		}
		
		// add parameters
		JsonArray parameters = object.get("parameters").getAsJsonArray();
		for (JsonElement parameter : parameters) {
			request.addParameter(Common.deserializeValue(parameter));	
		}
		
		// return request
		return request;
	}
	
}
