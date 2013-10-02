package org.uncertweb.matlab.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link MLProxyException} deserializer.
 *
 * @author Richard Jones
 *
 */
public class IOExceptionDeserializer implements JsonDeserializer<IOException> {

    @Override
    public IOException deserialize(JsonElement elem, Type type,
                                   JsonDeserializationContext ctx)
            throws JsonParseException {
        return new IOException(elem.getAsJsonObject().get(JSONConstants.IOEXCEPTION)
                .getAsString());
    }
}
