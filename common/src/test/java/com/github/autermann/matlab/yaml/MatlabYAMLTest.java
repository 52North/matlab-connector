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

import com.github.autermann.matlab.values.MatlabValues;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabBoolean;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabYAMLTest {

    @Test
    public void testRequest() {
        MatlabRequest request = new MatlabRequest("add", 5);
        request.addParameter(MatlabValues.randomMatlabArray(3));
        request.addParameter(MatlabBoolean.fromBoolean(true));
        request.addParameter(MatlabBoolean.fromBoolean(false));
        request.addParameter(MatlabValues.randomCell());
        request.addParameter(MatlabValues.randomMatlabMatrix(5, 5));
        request.addParameter(MatlabValues.randomMatlabScalar());
        request.addParameter(MatlabValues.randomMatlabString());
        request.addParameter(MatlabValues.randomStruct());
        MatlabRequest prequest = readWrite(request);
        assertThat(prequest, is(equalTo(request)));
    }

    @SuppressWarnings("unchecked")
    private <T> T readWrite(T t) {
        StringWriter w = new StringWriter();
        Yaml yaml = new MatlabYAML().createYAML();
        yaml.dump(t, w);
        T read = (T) yaml.load(new StringReader(w.toString()));
        return read;
    }

    @Test
    public void testResult() {
        MatlabResult response = new MatlabResult();
        response.addResult(MatlabValues.randomMatlabArray(3));
        response.addResult(MatlabBoolean.fromBoolean(true));
        response.addResult(MatlabBoolean.fromBoolean(false));
        response.addResult(MatlabValues.randomCell());
        response.addResult(MatlabValues.randomMatlabMatrix(5, 5));
        response.addResult(MatlabValues.randomMatlabScalar());
        response.addResult(MatlabValues.randomMatlabString());
        response.addResult(MatlabValues.randomStruct());
        MatlabResult presponse = readWrite(response);
        assertThat(presponse, is(equalTo(response)));

    }

    
}
