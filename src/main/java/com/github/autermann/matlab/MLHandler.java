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
package com.github.autermann.matlab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.github.autermann.matlab.json.IOExceptionDeserializer;
import com.github.autermann.matlab.json.MLExceptionDeserializer;
import com.github.autermann.matlab.json.MLExceptionSerializer;
import com.github.autermann.matlab.json.MLRequestDeserializer;
import com.github.autermann.matlab.json.MLResultDeserializer;
import com.github.autermann.matlab.json.MLValueSerializer;
import com.github.autermann.matlab.value.MLValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handles MATLAB requests/responses. See
 * {@link #sendRequest(String, int, MLRequest)} for how to send
 * a request to a MATLAB server.
 *
 * @author Richard Jones
 *
 */
public class MLHandler {
    private final Gson gson;

    /**
     * Creates a new
     * <code>MLHandler</code> instance.
     *
     */
    public MLHandler() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        builder.registerTypeAdapter(MLException.class,
                                    new MLExceptionDeserializer());
        builder.registerTypeAdapter(MLException.class,
                                    new MLExceptionSerializer());
        builder.registerTypeAdapter(IOException.class,
                                    new IOExceptionDeserializer());
        builder.registerTypeAdapter(MLRequest.class,
                                    new MLRequestDeserializer());
        builder.registerTypeAdapter(MLResult.class,
                                    new MLResultDeserializer());
        builder.registerTypeAdapter(MLValue.class,
                                    new MLValueSerializer());
        gson = builder.create();
    }

    /**
     * Parses a {@link MLRequest} from an {@link InputStream}.
     *
     * @param is the <code>InputStream</code> to parse from
     *
     * @return the parsed <code>MLRequest</code>
     */
    public MLRequest parseRequest(InputStream is) {
        return gson.fromJson(new InputStreamReader(is), MLRequest.class);
    }

    /**
     * Parses a {@link MLRequest} from a {@link String}.
     *
     * @param json the <code>String</code> to parse from
     *
     * @return the parsed <code>MLRequest</code>
     */
    public MLRequest parseRequest(String json) {
        return gson.fromJson(json, MLRequest.class);
    }

    /**
     * Parses a {@link MLException} from an {@link InputStream}.
     *
     * @param is the <code>InputStream</code> to parse from
     *
     * @return the parsed <code>MLException</code>
     */
    public MLException parseException(InputStream is) {
        return gson.fromJson(new InputStreamReader(is), MLException.class);
    }

    /**
     * Parses a {@link MLException} from a {@link String}.
     *
     * @param json the <code>String</code> to parse from
     *
     * @return the parsed <code>MLException</code>
     */
    public MLException parseException(String json) {
        return gson.fromJson(json, MLException.class);
    }

    /**
     * Parses a {@link MLResult} from an {@link InputStream}.
     *
     * @param is the <code>InputStream</code> to parse from
     *
     * @return the parsed <code>MLResult</code>
     */
    public MLResult parseResult(InputStream is) {
        return gson.fromJson(new InputStreamReader(is), MLResult.class);
    }

    /**
     * Parses a {@link MLResult} from a {@link String}.
     *
     * @param json the <code>String</code> to parse from
     *
     * @return the parsed <code>MLResult</code>
     */
    public MLResult parseResult(String json) {
        return gson.fromJson(json, MLResult.class);
    }

    /**
     * Outputs a {@link MLRequest} to an {@link OutputStream}.
     *
     * @param request the <code>MLRequest</code> to output
     * @param os      the <code>OutputStream</code> to output to
     */
    public void outputRequest(MLRequest request, OutputStream os) {
        print(request, os);
    }

    /**
     * Outputs a {@link MLResult} to an {@link OutputStream}.
     *
     * @param result the <code>MLResult</code> to output
     * @param os     the <code>OutputStream</code> to output to
     */
    public void outputResult(MLResult result, OutputStream os) {
        print(result, os);
    }

    /**
     * Outputs a {@link MLException} to an {@link OutputStream}.
     *
     * @param exception the <code>MLException</code> to output
     * @param os        the <code>OutputStream</code> to output to
     */
    public void outputException(MLException exception, OutputStream os) {
        print(exception, os);
    }

    public <T> T parse(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public <T> T parse(InputStream is, Class<T> type) {
        return gson.fromJson(new InputStreamReader(is), type);
    }

    private void print(Object o, OutputStream os) {
        String json = gson.toJson(o);
        PrintWriter pw = new PrintWriter(os, true);
        pw.println(json);
    }
}
