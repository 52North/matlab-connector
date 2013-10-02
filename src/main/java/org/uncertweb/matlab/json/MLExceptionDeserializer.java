package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link MLException} deserializer.
 *
 * @author Richard Jones
 *
 */
public class MLExceptionDeserializer implements JsonDeserializer<MLException> {

    @Override
    public MLException deserialize(JsonElement elem, Type type,
                                   JsonDeserializationContext ctx)
            throws JsonParseException {
        return new MLException(elem.getAsJsonObject()
                .get(JSONConstants.EXCEPTION).getAsString());
    }
}
