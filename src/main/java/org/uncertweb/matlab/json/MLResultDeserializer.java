package org.uncertweb.matlab.json;

import java.lang.reflect.Type;

import org.uncertweb.matlab.MLResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link MLResult} deserializer.
 *
 * @author Richard Jones
 *
 */
public class MLResultDeserializer extends AbstractDeserializer implements
        JsonDeserializer<MLResult> {

    @Override
    public MLResult deserialize(JsonElement elem, Type type,
                                JsonDeserializationContext ctx)
            throws JsonParseException {
        MLResult mlresult = new MLResult();
        JsonArray results = elem.getAsJsonObject()
                .get(JSONConstants.RESULTS).getAsJsonArray();
        for (JsonElement result : results) {
            mlresult.addResult(deserializeValue(result));
        }
        return mlresult;
    }
}
