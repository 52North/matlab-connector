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
package com.github.autermann.matlab.yaml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.Yaml;

import com.github.autermann.matlab.MatlabEncoding;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.yaml.construct.MatlabConstructor;
import com.github.autermann.matlab.yaml.represent.MatlabRepresenter;
import com.google.common.base.Charsets;

public class MatlabYAML implements MatlabEncoding {

    @Override
    public MatlabRequest decodeRequest(InputStream is) {
        return decode(is, MatlabRequest.class);
    }

    @Override
    public MatlabRequest decodeRequest(Reader is) {
        return decode(is, MatlabRequest.class);
    }

    @Override
    public MatlabRequest decodeRequest(String request) {
        return decode(request, MatlabRequest.class);
    }

    @Override
    public MatlabResponse decodeResponse(InputStream is) {
        return decode(is, MatlabResponse.class);
    }

    @Override
    public MatlabResponse decodeResponse(Reader is) {
        return decode(is, MatlabResponse.class);
    }

    @Override
    public MatlabResponse decodeResponse(String response) {
        return decode(response, MatlabResponse.class);
    }

    @Override
    public void encodeRequest(MatlabRequest request, Writer os) {
        encode(request, os);
    }

    @Override
    public String encodeRequest(MatlabRequest request) {
        return encode(request);
    }

    @Override
    public void encodeResponse(MatlabResponse response, Writer os) {
        encode(response, os);
    }

    @Override
    public String encodeResponse(MatlabResponse response) {
        return encode(response);
    }

    @Override
    public void encodeRequest(MatlabRequest request, OutputStream os) {
        encode(request, os);
    }

    @Override
    public void encodeResponse(MatlabResponse response, OutputStream os) {
        encode(response, os);
    }

    private <T> void encode(T t, OutputStream os) {
        encode(t, new OutputStreamWriter(os, Charsets.UTF_8));
    }

    private <T> void encode(T t, Writer os) {
        createYAML().dump(t, os);
    }

    private <T> String encode(T t) {
        return createYAML().dump(t);
    }

    private <T> T decode(InputStream is, Class<T> type) {
        return cast(createYAML().load(is), type);
    }

    private <T> T decode(String yaml, Class<T> type) {
        return cast(createYAML().load(yaml), type);
    }

    private <T> T decode(Reader is, Class<T> type) {
        return cast(createYAML().load(is), type);
    }

    private <T> T cast(Object o, Class<T> type) {
        if (o != null && type.isAssignableFrom(o.getClass())) {
            return type.cast(o);
        } else {
            throw new RuntimeException("Loaded unknown object: " + o);
        }
    }

    protected Yaml createYAML() {
        DumperOptions options = new DumperOptions();
        options.setExplicitEnd(true);
        options.setWidth(80);
        options.setIndent(2);
        options.setDefaultScalarStyle(ScalarStyle.PLAIN);
        options.setLineBreak(LineBreak.UNIX);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setVersion(Version.V1_1);
        options.setAllowUnicode(true);
        options.setExplicitStart(true);
        return new Yaml(new MatlabConstructor(),
                        new MatlabRepresenter(),
                        options);
    }
}
