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
package org.uncertweb.matlab.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.MLRequest;
import org.uncertweb.matlab.MLResult;
import org.uncertweb.matlab.value.MLArray;
import org.uncertweb.matlab.value.MLCell;
import org.uncertweb.matlab.value.MLMatrix;
import org.uncertweb.matlab.value.MLScalar;
import org.uncertweb.matlab.value.MLString;
import org.uncertweb.matlab.value.MLStruct;
import org.uncertweb.matlab.value.MLValue;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class MLInstance {
    private static final String CHAR_TYPE = "char";
    private static final String CELL_TYPE = "cell";
    private static final String DOUBLE_TYPE = "double";
    private static final String STRUCT_TYPE = "struct";
    private final Logger logger = LoggerFactory.getLogger(MLInstance.class);
    private MatlabProxy proxy;
    private MatlabTypeConverter processor;
    private final MLInstanceConfig config;

    public MLInstance() throws MLConnectorException {
        this(null);
    }

    public MLInstance(MLInstanceConfig config) throws MLConnectorException {
        MatlabProxyFactory factory = new MatlabProxyFactory();
        try {
            proxy = factory.getProxy();
            processor = new MatlabTypeConverter(proxy);
        } catch (MatlabConnectionException e) {
            throw new MLConnectorException("Unable to connect to MATLAB.", e);
        }
        this.config = config;
    }

    public void destroy() throws MLConnectorException {
        try {
            proxy.exit();
        } catch (MatlabInvocationException e) {
            throw new MLConnectorException("Couldn't exit MATLAB.", e);
        }
    }

    private void preHandle() throws MLConnectorException {
        try {
            if (config.getBaseDir() != null) {
                changeDir(config.getBaseDir());
                proxy.eval("addpath('.')");
            }
        } catch (MatlabInvocationException e) {
            throw new MLConnectorException("Unable to perform pre-request setup.", e);
        }
    }

    private void postHandle() throws MLConnectorException {
        try {
            clearAll();
        } catch (MatlabInvocationException e) {
            throw new MLConnectorException("Unable to perform post-request clean.", e);
        }
    }

    public MLResult handle(MLRequest request) throws MLConnectorException {
        // anything we need to do before handling
        preHandle();

        // eval request
        try {
            logger.info("Evaluating function " + request.getFunction() + "...");
            proxy.eval(request.toEvalString());

            // get results
            logger.info("Evaluation complete, parsing results...");
            MLResult result = new MLResult();
            for (int i = 1; i <= request.getResultCount(); i++) {
                result.addResult(parseValue("result" + i));
            }
            return result;
        } catch (MatlabInvocationException e) {
            throw new MLConnectorException("Unable to evaluate request.", e);
        } finally {
            try {
                postHandle();
            } catch (MLConnectorException e) {
                // this isn't too important
            }
        }
    }

    private MLValue parseValue(String varName) throws MLConnectorException {
        try {
            String type = getType(varName);
            if (type.equals(DOUBLE_TYPE)) {
                return parseDoubleValue(varName);
            } else if (type.equals(CHAR_TYPE)) {
                return parseCharValue(varName);
            } else if (type.equals(CELL_TYPE)) {
                return parseCellValue(varName);
            } else if (type.equals(STRUCT_TYPE)) {
                return parseStructValue(varName);
            } else {
                throw new MLConnectorException("Unable to parse value of type " +
                                               type + ", unsupported.");
            }
        } catch (MatlabInvocationException e) {
            throw new MLConnectorException("Unable to parse value.", e);
        }
    }

    private String getType(String varName) throws MatlabInvocationException {
        return (String) proxy.returningEval("class(" + varName + ")", 1)[0];
    }

    private void clearAll() throws MatlabInvocationException {
        proxy.eval("clear all");
    }

    private void changeDir(String path) throws MatlabInvocationException {
        // matlab needs escaped slashes too
        proxy.eval("cd('" + path.replace("\\", "\\\\") + "')");
    }

    private String[] fieldNames(String name) throws MatlabInvocationException {
        return (String[]) (Object[]) proxy
                .returningEval("fieldnames(" + name + ")", 1)[0];
    }

    private void assign(String name, String expression)
            throws MatlabInvocationException {
        proxy.eval(name + "=" + expression);
    }

    private Object[] getVariable(String name)
            throws MatlabInvocationException {
        return (Object[]) proxy.getVariable(name);
    }

    private MLValue parseDoubleValue(String varName)
            throws MatlabInvocationException {
        MatlabNumericArray array = processor.getNumericArray(varName);
        int[] lengths = array.getLengths();
        double[][] realArray = array.getRealArray2D();
        if (lengths[0] == 1 && lengths[1] == 1) {
            return new MLScalar(realArray[0][0]);
        } else if (lengths[0] == 1) {
            return new MLArray(realArray[0]);
        } else {
            return new MLMatrix(realArray);
        }
    }

    private MLValue parseCharValue(String varName)
            throws MatlabInvocationException {
        String string = (String) proxy.getVariable(varName);
        return new MLString(string);
    }

    private MLValue parseCellValue(String varName)
            throws MLConnectorException, MatlabInvocationException {
        // cell looks like ["key", ["another", 0.1]]
        Object[] obj = getVariable(varName);
        MLValue[] cell = new MLValue[obj.length];
        for (int i = 0; i < cell.length; i++) {
            final String subvarName = varName + "s";
            assign(subvarName, varName + "{" + (i + 1) + "}");
            cell[i] = parseValue(subvarName);
        }
        return new MLCell(cell);
    }

    private MLValue parseStructValue(String varName)
            throws MLConnectorException, MatlabInvocationException {
        MLStruct struct = new MLStruct();
        for (String name : fieldNames(varName)) {
            String subvarName = varName + "s";
            assign(subvarName, varName + "." + name);
            struct.setField(name, parseValue(subvarName));
        }
        return struct;
    }
}
