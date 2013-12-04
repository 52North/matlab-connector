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

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.joda.time.DateTime;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabDateTime;
import com.github.autermann.matlab.value.MatlabFile;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabValues {
    private static final Random random = new Random();

    public static MatlabString randomMatlabString() {
        return new MatlabString(randomString());
    }

    public static String randomString() {
        return new BigInteger(130, random).toString(32);
    }

    public static MatlabArray randomMatlabArray(int length) {
        return new MatlabArray(randomArray(length));
    }

    public static double[] randomArray(int length) {
        double[] array = new double[length];
        for (int i = 0; i < length; ++i) {
            array[i] = random.nextDouble();
        }
        return array;
    }

    public static MatlabMatrix randomMatlabMatrix(int rows, int columns) {
        double[][] matrix = new double[rows][columns];
        for (int i = 0; i < rows; ++i) {
            matrix[i] = randomArray(columns);
        }
        return new MatlabMatrix(matrix);
    }

    public static MatlabScalar randomMatlabScalar() {
        return new MatlabScalar(random.nextDouble());
    }

    public static MatlabCell randomCell() {
        return new MatlabCell(MatlabValues.randomMatlabArray(3),
                              MatlabBoolean.fromBoolean(true),
                              MatlabBoolean.fromBoolean(false),
                              MatlabValues.randomMatlabMatrix(5, 5),
                              MatlabValues.randomMatlabScalar(),
                              MatlabValues.randomMatlabString(),
                              randomStruct());
    }

    public static MatlabStruct randomStruct() {
        MatlabStruct struct = new MatlabStruct();
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabArray(3));
        struct.set(MatlabValues.randomString(),
                        MatlabBoolean.fromBoolean(true));
        struct.set(MatlabValues.randomString(),
                        MatlabBoolean.fromBoolean(false));
        struct.set(MatlabValues.randomString(),
                        new MatlabCell(MatlabValues.randomMatlabArray(3),
                                       MatlabBoolean.fromBoolean(true),
                                       MatlabBoolean.fromBoolean(false),
                                       MatlabValues.randomMatlabMatrix(5, 5),
                                       MatlabValues.randomMatlabScalar(),
                                       MatlabValues.randomMatlabString()));
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabMatrix(5, 5));
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabScalar());
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        struct.set(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        return struct;
    }

    public static MatlabFile randomFile() throws IOException {
        File file = File.createTempFile("test-file", ".rnd");
        file.deleteOnExit();
        byte[] bytes = new byte[8192];
        random.nextBytes(bytes);
        ByteStreams.write(bytes, Files.newOutputStreamSupplier(file));
        return new MatlabFile(file);
    }

    public static MatlabDateTime randomDateTime() {
        DateTime now = DateTime.now();
        long offset = (long) (Math.random() * (now.getMillis() + 1));
        return new MatlabDateTime(now.minus(offset));
    }
}
