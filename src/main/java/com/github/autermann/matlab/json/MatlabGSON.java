/*
 * Copyright (C) 2012-2013 by it's authors.
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
package com.github.autermann.matlab.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.github.autermann.matlab.MatlabEncoding;
import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabDateTime;
import com.github.autermann.matlab.value.MatlabFile;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Handles MATLAB requests/responses. See
 * {@link #sendRequest(String, int, MLRequest)} for how to send
 * a request to a MATLAB server.
 *
 * @author Richard Jones
 *
 */
public class MatlabGSON implements MatlabEncoding {
    private static final Class<?>[] VALUE_CLASSES = new Class<?>[] {
        MatlabValue.class,
        MatlabArray.class,
        MatlabBoolean.class,
        MatlabCell.class,
        MatlabFile.class,
        MatlabMatrix.class,
        MatlabScalar.class,
        MatlabString.class,
        MatlabStruct.class,
        MatlabDateTime.class
    };

    private MatlabResponse toResponse(JsonElement json)
            throws JsonParseException {
        try {
            return getGson().fromJson(json, MatlabResult.class);
        } catch (JsonParseException e1) {
            try {
                return getGson().fromJson(json, MatlabException.class);
            } catch (JsonParseException e2) {
                throw e1;
            }
        }
    }

    private MatlabRequest toRequest(JsonElement json) {
        return getGson().fromJson(json, MatlabRequest.class);
    }

    private JsonElement decode(InputStream is) {
        return decode(new InputStreamReader(is, Charsets.UTF_8));
    }

    private JsonElement decode(Reader is) {
        return new JsonParser().parse(is);
    }

    private JsonElement decode(String json) {
        return new JsonParser().parse(json);
    }

    private void encode(Object o, Writer os) {
        getGson().toJson(o, os);
    }

    private void encode(Object o, OutputStream os) {
        encode(o, new OutputStreamWriter(os, Charsets.UTF_8));
    }

    private String encode(Object o) {
        return getGson().toJson(o);
    }

    public Gson getGson() {
        return Holder.GSON;
    }

    @Override
    public MatlabRequest decodeRequest(InputStream is) {
        return toRequest(decode(is));
    }

    @Override
    public MatlabResponse decodeResponse(InputStream is) {
        return toResponse(decode(is));
    }

    @Override
    public void encodeRequest(MatlabRequest request, OutputStream os) {
        encode(request, os);
    }

    @Override
    public void encodeResponse(MatlabResponse response, OutputStream out) {
        encode(response, out);
    }

    @Override
    public MatlabRequest decodeRequest(String request) {
        return toRequest(decode(request));
    }

    @Override
    public MatlabResponse decodeResponse(String response) {
        return toResponse(decode(response));
    }

    @Override
    public String encodeRequest(MatlabRequest request) {
        return encode(request);
    }

    @Override
    public String encodeResponse(MatlabResponse response) {
        return encode(response);
    }

    @Override
    public MatlabRequest decodeRequest(Reader is) {
        return toRequest(decode(is));
    }

    @Override
    public MatlabResponse decodeResponse(Reader is) {
        return toResponse(decode(is));
    }

    @Override
    public void encodeRequest(MatlabRequest request, Writer os) {
        encode(request, os);
    }

    @Override
    public void encodeResponse(MatlabResponse response, Writer os) {
        encode(response, os);
    }

    private static class Holder {
        private static final Gson GSON = create();

        private static Gson create() {
            MatlabValueSerializer valueSerializer = new MatlabValueSerializer();
            GsonBuilder builder = new GsonBuilder();

            builder.registerTypeAdapter(MatlabException.class,
                                        new MatlabExceptionSerializer())
                    .registerTypeAdapter(MatlabRequest.class,
                                         new MatlabRequestSerializer())
                    .registerTypeAdapter(MatlabResult.class,
                                         new MatlabResultSerializer());

            for (Class<?> c : VALUE_CLASSES) {
                builder.registerTypeAdapter(c, valueSerializer);
            }

            return builder
                    .disableHtmlEscaping()
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
    }
}
