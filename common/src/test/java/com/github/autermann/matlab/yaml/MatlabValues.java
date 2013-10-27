package com.github.autermann.matlab.yaml;

import java.math.BigInteger;
import java.util.Random;

import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
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
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabArray(3));
        struct.setField(MatlabValues.randomString(),
                        MatlabBoolean.fromBoolean(true));
        struct.setField(MatlabValues.randomString(),
                        MatlabBoolean.fromBoolean(false));
        struct.setField(MatlabValues.randomString(),
                        new MatlabCell(MatlabValues.randomMatlabArray(3),
                                       MatlabBoolean.fromBoolean(true),
                                       MatlabBoolean.fromBoolean(false),
                                       MatlabValues.randomMatlabMatrix(5, 5),
                                       MatlabValues.randomMatlabScalar(),
                                       MatlabValues.randomMatlabString()));
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabMatrix(5, 5));
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabScalar());
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        struct.setField(MatlabValues.randomString(),
                        MatlabValues.randomMatlabString());
        return struct;
    }
}
