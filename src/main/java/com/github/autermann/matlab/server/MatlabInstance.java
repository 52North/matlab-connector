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
package com.github.autermann.matlab.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.AbstractMatlabValueVisitor;
import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabBoolean;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabEvalStringVisitor;
import com.github.autermann.matlab.value.MatlabFile;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabType;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class MatlabInstance {
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final String CHAR_TYPE = "char";
    private static final String CELL_TYPE = "cell";
    private static final String STRUCT_TYPE = "struct";
    private static final String LOGICAL_TYPE = "logical";
    private final Logger log = LoggerFactory.getLogger(MatlabInstance.class);
    private final MatlabProxy proxy;
    private final MatlabTypeConverter processor;
    private final MatlabInstanceConfiguration config;

    public MatlabInstance() throws MatlabException {
        this(MatlabInstanceConfiguration.builder().build());
    }

    public MatlabInstance(MatlabInstanceConfiguration config) throws
            MatlabException {
        this.config = checkNotNull(config);
        try {
            proxy = createProxyFactory(config).getProxy();
            processor = new MatlabTypeConverter(proxy);
        } catch (MatlabConnectionException e) {
            throw new MatlabException("Unable to connect to MATLAB.", e);
        }
    }

    private MatlabProxyFactory createProxyFactory(
            MatlabInstanceConfiguration config) {
        MatlabProxyFactoryOptions.Builder builder
                = new MatlabProxyFactoryOptions.Builder()
                .setHidden(config.isHidden())
                .setPort(config.getPort());
        if (config.getBaseDir().isPresent()) {
            builder.setMatlabStartingDirectory(config.getBaseDir().get());
        }
        return new MatlabProxyFactory(builder.build());
    }

    public void destroy() throws MatlabException {
        try {
            proxy.exit();
        } catch (MatlabInvocationException e) {
            throw new MatlabException("Couldn't exit MATLAB.", e);
        }
    }

    private void preHandle() throws MatlabException {
        try {
            if (config.getBaseDir().isPresent()) {
                changeDir(config.getBaseDir().get().getAbsolutePath());
                proxy.eval("addpath('.')");
            }
        } catch (MatlabInvocationException e) {
            throw new MatlabException("Unable to perform pre-request setup.", e);
        }
    }

    private void postHandle() throws MatlabException {
        try {
            clearAll();
        } catch (MatlabInvocationException e) {
            throw new MatlabException("Unable to perform post-request clean.", e);
        }
    }

    protected Map<String, MatlabValue> feval(String function,
                                             Map<String, MatlabType> results,
                                             List<MatlabValue> parameters)
            throws MatlabInvocationException, MatlabException {
        final int length = results.size();
        final String[] rarray = results.keySet().toArray(new String[length]);
        final String[] varray = genvarnames(rarray);
        final String cmd = buildFEval(function, varray, parameters);
        log.debug("Evaluation: {}", cmd);
        proxy.eval(cmd);
        log.info("Evaluation complete, parsing results...");
        final Map<String, MatlabValue> result = Maps.newLinkedHashMap();
        for (int i = 0; i < length; ++i) {
            result.put(rarray[i], parseValue(varray[i], results.get(rarray[i])));
        }
        return result;
    }

    protected String[] genvarnames(String[] rarray) throws
            MatlabInvocationException {
        final int length = rarray.length;
        final String[] varray = new String[length];
        final MatlabEvalStringVisitor f = MatlabEvalStringVisitor.create();
        for (int i = 0; i < length; ++i) {
            String safe = f.apply(new MatlabString(rarray[i]));
            String cmd = String.format("genvarname(%s, who)", safe);
            varray[i] = (String) proxy.returningEval(cmd, 1)[0];
        }
        return varray;
    }

    protected String buildFEval(String function, String[] varray,
                                List<MatlabValue> parameters) {
        StringBuilder sb = new StringBuilder();
        COMMA_JOINER.appendTo(sb.append('['), varray).append("]");
        sb.append(" = ");
        sb.append("feval('").append(function).append('\'');
        if (!parameters.isEmpty()) {
            COMMA_JOINER.appendTo(sb.append(", "), Iterables.transform(
                    parameters, MatlabEvalStringVisitor.create()));
        }
        sb.append(')');
        return sb.toString();
    }

    public MatlabResult handle(MatlabRequest request) throws
            MatlabException {
        // anything we need to do before handling
        preHandle();

        // eval request
        File temp = Files.createTempDir();
        try {
            request.visitParameters(new FileSavingVisitor(temp));
            Map<String, MatlabValue> results;
            try {
            log.info("Evaluating function {}...", request.getFunction());

                results = feval(request.getFunction(),
                                request.getResults(),
                                request.getParameters());
            } finally {
                request.visitParameters(new FileDeletingVisitor(false));
            }

            FileDeletingVisitor delV = new FileDeletingVisitor(true);
            MatlabResult result = new MatlabResult(request.getId());


            for (Entry<String, MatlabValue> e : results.entrySet()) {
                e.getValue().accept(delV);
                result.addResult(e.getKey(), e.getValue());
            }

            return result;
        } catch (MatlabInvocationException e) {
            throw new MatlabException("Unable to evaluate request.", e);
        } finally {
            temp.delete();
            try {
                postHandle();
            } catch (MatlabException e) {
                // this isn't too important
            }
        }
    }

    private MatlabValue parseValue(String varName, MatlabType toType)
            throws MatlabException {
        MatlabValue value = parseValue(varName);
        MatlabType fromType = value.getType();
        if (fromType == toType) {
            return value;
        }
        String message = String.format("can not convert from %s to %s",
                                       fromType, toType);
        switch (fromType) {
            case SCALAR:
                switch (toType) {
                    case BOOLEAN:
                        return value.asScalar().toBoolean();
                    case DATE_TIME:
                        return value.asScalar().toDateTime();
                    default:
                        throw new MatlabException(message);
                }
            case STRING:
                switch (toType) {
                    case BOOLEAN:
                        return value.asString().toBoolean();
                    case DATE_TIME:
                        return value.asString().toDateTime();
                    case FILE:
                        try {
                            return value.asString().toFile(true);
                        } catch (IOException ex) {
                            throw new MatlabException("error loading file", ex);
                        }
                    default:
                        throw new MatlabException(message);
                }
            case ARRAY:
                switch (toType) {
                    case DATE_TIME:
                        return value.asArray().toDateTime();
                    default:
                        throw new MatlabException(message);
                }
            default:
                throw new MatlabException(message);
        }

    }

    private MatlabValue parseValue(String varName) throws
            MatlabException {
        try {

            if (isNumerical(varName)) {
                return parseDoubleValue(varName);
            }
            String clazz = getType(varName);
            if (clazz.equals(LOGICAL_TYPE)) {
                return parseBooleanValue(varName);
            } else if (clazz.equals(CHAR_TYPE)) {
                return parseCharValue(varName);
            } else if (clazz.equals(CELL_TYPE)) {
                return parseCellValue(varName);
            } else if (clazz.equals(STRUCT_TYPE)) {
                return parseStructValue(varName);
            } else {
                throw new MatlabException("Unable to parse value of type " +
                                          clazz + ", unsupported.");
            }
        } catch (MatlabInvocationException e) {
            throw new MatlabException("Unable to parse value.", e);
        }
    }

    private String getType(String varName) throws MatlabInvocationException {
        String cmd = String.format("class(%s)", varName);
        return (String) proxy.returningEval(cmd, 1)[0];
    }

    private boolean isNumerical(String varName)
            throws MatlabInvocationException {
        String cmd = String.format("isnumeric(%s)", varName);
        Object ret = proxy.returningEval(cmd, 1)[0];
        if (ret instanceof boolean[]) {
            return ((boolean[]) ret)[0];
        } else {
            return (boolean) ret;
        }
    }

    private void clearAll() throws MatlabInvocationException {
        proxy.eval("clear all");
    }

    private void changeDir(String path) throws MatlabInvocationException {
        // matlab needs escaped slashes too
        proxy.eval(String.format("cd('%s')", path.replace("\\", "\\\\")));
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

    private MatlabValue parseDoubleValue(String varName)
            throws MatlabInvocationException {
        MatlabNumericArray array = processor.getNumericArray(varName);
        int[] lengths = array.getLengths();
        double[][] realArray = array.getRealArray2D();
        if (lengths[0] == 1 && lengths[1] == 1) {
            return new MatlabScalar(realArray[0][0]);
        } else if (lengths[0] == 1) {
            return new MatlabArray(realArray[0]);
        } else {
            return new MatlabMatrix(realArray);
        }
    }
       private MatlabBoolean parseBooleanValue(String varName)
            throws MatlabInvocationException {
        boolean[] variable = (boolean[]) proxy.getVariable(varName);
        return MatlabBoolean.fromBoolean(variable[0]);
    }

    private MatlabString parseCharValue(String varName)
            throws MatlabInvocationException {
        String string = (String) proxy.getVariable(varName);
        return new MatlabString(string);
    }

    private MatlabCell parseCellValue(String varName)
            throws MatlabException, MatlabInvocationException {
        // cell looks like ["key", ["another", 0.1]]
        Object[] obj = getVariable(varName);
        MatlabValue[] cell = new MatlabValue[obj.length];
        final String subvarName = varName + "s";
        for (int i = 0; i < cell.length; i++) {
            assign(subvarName, varName + "{" + (i + 1) + "}");
            cell[i] = parseValue(subvarName);
        }
        return new MatlabCell(cell);
    }

    private MatlabStruct parseStructValue(String varName)
            throws MatlabException, MatlabInvocationException {
        MatlabStruct struct = new MatlabStruct();
        for (String name : fieldNames(varName)) {
            String subvarName = varName + "s";
            assign(subvarName, varName + "." + name);
            struct.set(name, parseValue(subvarName));
        }
        return struct;
    }

    private static class FileSavingVisitor extends AbstractMatlabValueVisitor {
        private final File directory;

        FileSavingVisitor(File directory) {
            this.directory = checkNotNull(directory);
        }

        @Override
        public void visit(MatlabCell cell) {
            cell.stream().forEach(v -> v.accept(this));
        }

        @Override
        public void visit(MatlabStruct struct) {
            struct.value().values().forEach(v -> v.accept(this));
        }

        @Override
        public void visit(MatlabFile file) {
            try {
                File f = File.createTempFile("matlab-file", ".bin", directory);
                file.save(f);
                file.unload();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class FileDeletingVisitor extends AbstractMatlabValueVisitor {
        private final boolean load;

        FileDeletingVisitor(boolean load) {
            this.load = load;
        }

        @Override
        public void visit(MatlabCell cell) {
            cell.stream().forEach(v -> v.accept(this));
        }

        @Override
        public void visit(MatlabStruct struct) {
            struct.value().values().forEach(v -> v.accept(this));
        }

        @Override
        public void visit(MatlabFile file) {
            try {
                if (file.isSaved()) {
                    if (load && !file.isLoaded()) {
                        file.load();
                    }
                    file.delete();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
