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
package com.github.autermann.matlab.values;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.StringVisitor;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class StringVisitorTest {
    private StringVisitor visitor;

    @Before
    public void setUp() {
        this.visitor = StringVisitor.create();
    }

    @Test
    public void testMatlabArray() {
        MatlabArray value = new MatlabArray(new double[] { 1.1, 1.2 });
        assertThat(visitor.apply(value), is("[ 1.1, 1.2 ]"));
    }

    @Test
    public void testMatlabBoolean() {
        assertThat(visitor.apply(MatlabBoolean.yes()), is("1"));
        assertThat(visitor.apply(MatlabBoolean.no()), is("0"));
    }

    @Test
    public void testMatlabCell() {
        MatlabCell value = new MatlabCell(
                new MatlabArray(new double[] { 1.1, 1.2 }),
                MatlabBoolean.yes());
        assertThat(visitor.apply(value), is("{ [ 1.1, 1.2 ], 1 }"));
    }

    @Test
    public void testMatlabMatrix() {
        MatlabMatrix value = new MatlabMatrix(new double[][] {
            new double[] { 1.1, 1.2 },
            new double[] { 1.3, 1.4 }
        });
        assertThat(visitor.apply(value), is("[ 1.1, 1.2; 1.3, 1.4 ]"));
    }

    @Test
    public void testMatlabScalar() {
        assertThat(visitor.apply(new MatlabScalar(1.1)), is("1.1"));
    }

    @Test
    public void testMatlabString() {
        assertThat(visitor.apply(new MatlabString("asdf")), is("'asdf'"));
        assertThat(visitor.apply(new MatlabString("a'sdf")), is("'a''sdf'"));
    }

    @Test
    public void testMatlabStruct() {
        MatlabStruct value = new MatlabStruct()
                .set("fiel'd1", new MatlabMatrix(new double[][] {
                    new double[] { 1.1, 1.2 },
                    new double[] { 1.3, 1.4 }
                }))
                .set("field2", new MatlabScalar(1.1))
                .set("field3", MatlabBoolean.yes());

        assertThat(visitor.apply(value), is("struct('fiel''d1', [ 1.1, 1.2; 1.3, 1.4 ], 'field2', 1.1, 'field3', 1)"));
    }
}
