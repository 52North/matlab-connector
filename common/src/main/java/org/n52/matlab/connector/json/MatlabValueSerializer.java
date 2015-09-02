/*
 * Copyright (C) 2012-2015 by it's authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.n52.matlab.connector.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import org.n52.matlab.connector.value.MatlabArray;
import org.n52.matlab.connector.value.MatlabBoolean;
import org.n52.matlab.connector.value.MatlabCell;
import org.n52.matlab.connector.value.MatlabDateTime;
import org.n52.matlab.connector.value.MatlabFile;
import org.n52.matlab.connector.value.MatlabMatrix;
import org.n52.matlab.connector.value.MatlabScalar;
import org.n52.matlab.connector.value.MatlabString;
import org.n52.matlab.connector.value.MatlabStruct;
import org.n52.matlab.connector.value.MatlabType;
import org.n52.matlab.connector.value.MatlabValue;
import org.n52.matlab.connector.value.ReturningMatlabValueVisitor;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link MatlabValue} serializer.
 *
 * @author Richard Jones
 *
 */
public class MatlabValueSerializer implements JsonSerializer<MatlabValue>,
                                              JsonDeserializer<MatlabValue> {
    @Override
    public JsonElement serialize(MatlabValue value, Type type,
                                 JsonSerializationContext ctx) {
        if (value == null) {
            return new JsonNull();
        }
        JsonObject object = new JsonObject();
        object.addProperty(MatlabJSONConstants.TYPE,
                           value.getType().toString());
        object.add(MatlabJSONConstants.VALUE,
                   value.accept(new VisitingSerializer(ctx)));
        return object;
    }

    @Override
    public MatlabValue deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context)
            throws JsonParseException {
        return deserializeValue(json);
    }

    /**
     * Deserializes an {@link MatlabValue} from a {@link JsonElement}.
     *
     * @param element the <code>JsonElement</code> containing a
     *                serialized <code>MatlabValue</code>
     *
     * @return the deserialized <code>MatlabValue</code>
     */
    public MatlabValue deserializeValue(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new JsonParseException("expected JSON object");
        }
        JsonObject json = element.getAsJsonObject();
        MatlabType type = getType(json);
        JsonElement value = json.get(MatlabJSONConstants.VALUE);
        switch (type) {
            case ARRAY:
                return parseMatlabArray(value);
            case BOOLEAN:
                return parseMatlabBoolean(value);
            case CELL:
                return parseMatlabCell(value);
            case FILE:
                return parseMatlabFile(value);
            case MATRIX:
                return parseMatlabMatrix(value);
            case SCALAR:
                return parseMatlabScalar(value);
            case STRING:
                return parseMatlabString(value);
            case STRUCT:
                return parseMatlabStruct(value);
            case DATE_TIME:
                return parseMatlabDateTime(value);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }

    private MatlabType getType(JsonObject json) throws JsonParseException {
        String type = json.get(MatlabJSONConstants.TYPE).getAsString();
        try {
            return MatlabType.fromString(type);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown type: " + type);
        }
    }

    private MatlabMatrix parseMatlabMatrix(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        double[][] values = new double[array.size()][array.get(0)
                .getAsJsonArray().size()];
        for (int i = 0; i < array.size(); i++) {
            JsonArray innerArray = array.get(i).getAsJsonArray();
            for (int j = 0; j < innerArray.size(); j++) {
                values[i][j] = innerArray.get(j).getAsDouble();
            }
        }
        return new MatlabMatrix(values);
    }

    private MatlabArray parseMatlabArray(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        double[] values = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return new MatlabArray(values);
    }

    private MatlabStruct parseMatlabStruct(JsonElement value) {
        MatlabStruct struct = new MatlabStruct();
        for (Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
            struct.set(e.getKey(), deserializeValue(e.getValue()));
        }
        return struct;
    }

    private MatlabCell parseMatlabCell(JsonElement value) {
        JsonArray array = value.getAsJsonArray();
        MatlabValue[] cell = new MatlabValue[array.size()];
        for (int i = 0; i < array.size(); i++) {
            cell[i] = deserializeValue(array.get(i));
        }
        return new MatlabCell(cell);
    }

    private MatlabScalar parseMatlabScalar(JsonElement value) {
        JsonPrimitive p = (JsonPrimitive) value;
        if (p.isString()) {
            return new MatlabScalar(Double.valueOf(p.getAsString()));
        } else {
            return new MatlabScalar(p.getAsDouble());
        }
    }

    private MatlabBoolean parseMatlabBoolean(JsonElement value) {
        return MatlabBoolean.fromBoolean(value.getAsBoolean());
    }

    private MatlabString parseMatlabString(JsonElement value) {
        return new MatlabString(value.getAsString());
    }

    private MatlabFile parseMatlabFile(JsonElement value) {
        return new MatlabFile(gunzip(value.getAsString()));
    }

    private byte[] gunzip(String value) {
        GZIPInputStream in = null;
        try {
            byte[] gzipped = BaseEncoding.base64().decode(value);
            in = new GZIPInputStream(new ByteArrayInputStream(gzipped));
            return ByteStreams.toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private String gzip(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream out = null;
        try {
            out = new GZIPOutputStream(baos);
            out.write(bytes);
            out.finish();
            return BaseEncoding.base64().encode(baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private MatlabDateTime parseMatlabDateTime(JsonElement value) {
        DateTime dt = ISODateTimeFormat.dateTime()
                .parseDateTime(value.getAsString());
        return new MatlabDateTime(dt);
    }

    private class VisitingSerializer implements
            ReturningMatlabValueVisitor<JsonElement> {
        private final JsonSerializationContext ctx;

        VisitingSerializer(JsonSerializationContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public JsonElement visit(MatlabArray array) {
            return ctx.serialize(array.value());
        }

        @Override
        public JsonElement visit(MatlabBoolean bool) {
            return ctx.serialize(bool.value());
        }

        @Override
        public JsonElement visit(MatlabCell cell) {
            return ctx.serialize(cell.value());
        }

        @Override
        public JsonElement visit(MatlabMatrix matrix) {
            return ctx.serialize(matrix.value());
        }

        @Override
        public JsonElement visit(MatlabScalar scalar) {
            if (Double.isNaN(scalar.value())||
                Double.isInfinite(scalar.value())) {
                return ctx.serialize(Double.toString(scalar.value()));
            } else {
                return ctx.serialize(scalar.value());
            }
        }

        @Override
        public JsonElement visit(MatlabString string) {
            return ctx.serialize(string.value());
        }

        @Override
        public JsonElement visit(MatlabStruct struct) {
            JsonObject object = new JsonObject();
            for (Entry<MatlabString, MatlabValue> e : struct.value().entrySet()) {
                object.add(e.getKey().value(),
                           serialize(e.getValue(), MatlabValue.class, ctx));
            }
            return object;
        }

        @Override
        public JsonElement visit(MatlabFile file) {
            try {
                return ctx.serialize(gzip(file.getContent()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public JsonElement visit(MatlabDateTime time) {
            return ctx.serialize(ISODateTimeFormat.dateTime()
                    .print(time.value()));
        }
    }

}
