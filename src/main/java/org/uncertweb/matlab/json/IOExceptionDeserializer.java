package org.uncertweb.matlab.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * {@link MLProxyException} deserializer.
 * 
 * @author Richard Jones
 *
 */
public class IOExceptionDeserializer implements JsonDeserializer<IOException> {

    @Override
	public IOException deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		// get object
		JsonObject object = arg0.getAsJsonObject();
		
		// create and return error
		String message = object.get("ioexception").getAsString();
		return new IOException(message);
	}
	
}
