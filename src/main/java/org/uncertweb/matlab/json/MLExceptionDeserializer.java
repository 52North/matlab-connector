package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * {@link MLException} deserializer.
 * 
 * @author Richard Jones
 *
 */
public class MLExceptionDeserializer implements JsonDeserializer<MLException> {

	public MLException deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		// get object
		JsonObject object = arg0.getAsJsonObject();
		
		// create and return error
		String message = object.get("exception").getAsString();
		return new MLException(message);
	}
	
}
