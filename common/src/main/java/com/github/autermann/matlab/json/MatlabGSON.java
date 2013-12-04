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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

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

    @Override
    public MatlabRequest decodeRequest(InputStream is) {
        return getGson().fromJson(decode(is), MatlabRequest.class);
    }

    @Override
    public MatlabResponse decodeResponse(InputStream is) {
        JsonElement json = decode(is);
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

    @Override
    public void encodeRequest(MatlabRequest request, OutputStream os) {
        print(request, os);
    }

    @Override
    public void encodeResponse(MatlabResponse response, OutputStream out) {
        print(response, out);
    }

    private JsonElement decode(InputStream is) throws JsonParseException {
        return new JsonParser().parse(new InputStreamReader(is, Charsets.UTF_8));
    }

    private void print(Object o, OutputStream os) {
        new PrintWriter(os, true).println(getGson().toJson(o));
    }

    public Gson getGson() {
        return Holder.GSON;
    }

    private static class Holder {
        private static final Gson GSON = create();

        private static Gson create() {
            MatlabValueSerializer valueSerializer = new MatlabValueSerializer();
            MatlabValueDeserializer valueDeserializer
                    = new MatlabValueDeserializer();
            GsonBuilder builder = new GsonBuilder();

            builder
                    .registerTypeAdapter(MatlabException.class, new MatlabExceptionDeserializer())
                    .registerTypeAdapter(MatlabException.class, new MatlabExceptionSerializer())
                    .registerTypeAdapter(IOException.class, new IOExceptionDeserializer())
                    .registerTypeAdapter(MatlabRequest.class, new MatlabRequestDeserializer())
                    .registerTypeAdapter(MatlabResult.class, new MatlabResultDeserializer());
            for (Class<?> c : VALUE_CLASSES) {
                builder.registerTypeAdapter(c, valueDeserializer);
                builder.registerTypeAdapter(c, valueSerializer);
            }

            return builder
                    .disableHtmlEscaping()
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
    }
}
