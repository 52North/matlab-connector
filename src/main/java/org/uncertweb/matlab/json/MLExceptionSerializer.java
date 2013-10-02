package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MLException} serializer.
 * 
 * @author Richard Jones
 *
 */
public class MLExceptionSerializer implements JsonSerializer<MLException> {

    @Override
	public JsonElement serialize(MLException e, Type type, JsonSerializationContext ctx) {
		JsonObject object = new JsonObject();
		object.add(JSONConstants.EXCEPTION, ctx.serialize(e.getMessage()));
		return object;
	}

}
