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
public class MLRequestDeserializer extends AbstractDeserializer implements
        JsonDeserializer<MLRequest> {

    @Override
    public MLRequest deserialize(JsonElement elem, Type type,
                                 JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject json = elem.getAsJsonObject();
        String function = json.get(JSONConstants.FUNCTION).getAsString();
        MLRequest request = new MLRequest(function);

        // add result count if it exists
        if (json.has(JSONConstants.RESULT_COUNT)) {
            request.setResultCount(json.get(JSONConstants.RESULT_COUNT).getAsInt());
        }

        // add parameters
        JsonArray parameters = json.get(JSONConstants.PARAMETERS).getAsJsonArray();
        for (JsonElement parameter : parameters) {
            request.addParameter(deserializeValue(parameter));
        }

        // return request
        return request;
    }
}
