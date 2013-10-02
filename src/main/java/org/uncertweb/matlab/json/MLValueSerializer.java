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
    @Override
    public JsonElement serialize(MLValue value, Type type,
                                 JsonSerializationContext ctx) {
        if (value.isScalar()) {
            return ctx.serialize(value.getAsScalar().getScalar());
        } else if (value.isArray()) {
            return ctx.serialize(value.getAsArray().getArray());
        } else if (value.isMatrix()) {
            return ctx.serialize(value.getAsMatrix().getMatrix());
        } else if (value.isString()) {
            return ctx.serialize(value.getAsString().getString());
        } else if (value.isCell()) {
            JsonObject object = new JsonObject();
            object.add(JSONConstants.CELL, 
                       ctx.serialize(value.getAsCell() .getCell()));
            return object;
        } else if (value.isStruct()) {
            JsonObject object = new JsonObject();
            JsonObject structobj = new JsonObject();
            object.add(JSONConstants.STRUCT, structobj);
            MLStruct struct = value.getAsStruct();
            for (Entry<String, MLValue> e : struct.getStruct().entrySet()) {
                structobj.add(e.getKey(),
                              serialize(e.getValue(), MLValue.class, ctx));
            }
            return object;
        }

        // should never get here
        return null;
    }
}
