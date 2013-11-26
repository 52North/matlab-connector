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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.values.MatlabValues;
import com.google.gson.Gson;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabJSONTest {
    private Gson gson;

    @Before
    public void setUp() {
        this.gson = new MatlabGSON().getGson();
    }

    private <T> T readWrite(T t) {
        Class<T> c = (Class<T>) t.getClass();
        String s = gson.toJson(t);
        System.out.println(s);
        return gson.fromJson(s, c);
    }

    @Test
    public void testRequest() throws IOException {
        MatlabRequest request = new MatlabRequest("add")
                .addResult("result1")
                .addResult("result2")
                .addResult("result3");
        request.addParameter(MatlabValues.randomMatlabArray(3));
        request.addParameter(MatlabBoolean.fromBoolean(true));
        request.addParameter(MatlabBoolean.fromBoolean(false));
        request.addParameter(MatlabValues.randomCell());
        request.addParameter(MatlabValues.randomMatlabMatrix(5, 5));
        request.addParameter(MatlabValues.randomMatlabScalar());
        request.addParameter(MatlabValues.randomMatlabString());
        request.addParameter(MatlabValues.randomStruct());
        request.addParameter(MatlabValues.randomFile());
        MatlabRequest prequest = readWrite(request);
        assertThat(prequest, is(equalTo(request)));
    }

    @Test
    public void testResult() throws IOException {
        MatlabResult response = new MatlabResult();
        response.addResult("result1", MatlabValues.randomMatlabArray(3));
        response.addResult("result2", MatlabBoolean.fromBoolean(true));
        response.addResult("result3", MatlabBoolean.fromBoolean(false));
        response.addResult("result4", MatlabValues.randomCell());
        response.addResult("result5", MatlabValues.randomMatlabMatrix(5, 5));
        response.addResult("result6", MatlabValues.randomMatlabScalar());
        response.addResult("result7", MatlabValues.randomMatlabString());
        response.addResult("result8", MatlabValues.randomStruct());
        response.addResult("result9", MatlabValues.randomFile());
        MatlabResult presponse = readWrite(response);
        assertThat(presponse, is(equalTo(response)));
    }

    @Test
    public void testMatlabArray() {
        MatlabValue value = MatlabValues.randomMatlabArray(3);
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabTrue() {
        MatlabValue value = MatlabBoolean.yes();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabFalse() {
        MatlabValue value = MatlabBoolean.no();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabCell() {
        MatlabValue value = MatlabValues.randomCell();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabMatrix() {
        MatlabValue value = MatlabValues.randomMatlabMatrix(10, 10);
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabScalar() {
        MatlabValue value = MatlabValues.randomMatlabScalar();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabString() {
        MatlabValue value = MatlabValues.randomMatlabString();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabStruct() {
        MatlabValue value = MatlabValues.randomStruct();
        assertThat(readWrite(value), is(equalTo(value)));
    }

    @Test
    public void testMatlabFile() throws IOException {
        MatlabValue value = MatlabValues.randomFile();
        assertThat(readWrite(value), is(equalTo(value)));
    }
}
