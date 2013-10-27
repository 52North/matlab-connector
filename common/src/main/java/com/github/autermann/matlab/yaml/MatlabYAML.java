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
import java.io.StringReader;
import java.io.StringWriter;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.Yaml;

import com.github.autermann.matlab.MatlabEncoding;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.yaml.construct.MatlabConstructor;
import com.github.autermann.matlab.yaml.represent.MatlabRepresenter;
import com.google.common.base.Charsets;

/**
 * TODO JavaDoc
 *
 * @author Chrequestistian Auterequestmann
 * <c.auterequestmann@52norequestth.orequestg>
 */
public class MatlabYAML implements MatlabEncoding {

    @Override
    public MatlabRequest decodeRequest(InputStream is) {
        return decode(is, MatlabRequest.class);
    }

    @Override
    public MatlabResponse decodeResponse(InputStream is) {
        return decode(is, MatlabResponse.class);
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
        createYAML().dump(t, new OutputStreamWriter(os, Charsets.UTF_8));
    }

    private <T> T decode(InputStream is, Class<T> type) throws RuntimeException {
        Object o = createYAML().load(is);
        if (o != null && type.isAssignableFrom(o.getClass())) {
            return type.cast(o);
        } else {
            throw new RuntimeException("Loaded unknown object: " + o);
        }
    }

    private Yaml createYAML() {
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

    public static void main(String[] args) {
        MatlabRequest request = new MatlabRequest("add", 1);
        request.addParameter(new MatlabScalar(2.2));
        request.addParameter(new MatlabScalar(2.5));

        MatlabResult response = new MatlabResult();
        response.addResult(new MatlabArray(new double[] { 1.2, 1.3 }));
        response.addResult(new MatlabCell(
                new MatlabString("value"),
                new MatlabArray(new double[] { 1.2, 1.3 })));
        MatlabStruct struct = new MatlabStruct();
        struct.setField("field1", new MatlabString("value1"));
        struct.setField("field2", new MatlabString("value2"));
        response.addResult(struct);
        response.addResult(MatlabBoolean.fromBoolean(true));
        response.addResult(MatlabBoolean.fromBoolean(false));
        response.addResult(new MatlabScalar(2.2));
        response.addResult(new MatlabMatrix(new double[][] {
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 },
            new double[] { 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2,
                           1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3, 1.2, 1.3 }, }));
        StringWriter w = new StringWriter();
        MatlabYAML matlabYAML = new MatlabYAML();
        matlabYAML.createYAML().dump(request, w);
        matlabYAML.createYAML().dump(response, w);
        System.err.println(w.toString());

        StringReader r = new StringReader(w.toString());
        Iterable<Object> o = matlabYAML.createYAML().loadAll(r);
        System.err.println(o.iterator().next());
        System.err.println(o.iterator().next());
    }
}
