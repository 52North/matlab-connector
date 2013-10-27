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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
class MatlabConstructor extends SafeConstructor {

    MatlabConstructor() {
        register(MatlabYAMLConstants.MATLAB_MATRIX_TAG,
                 new MatlabMatrixConstruct());
        register(MatlabYAMLConstants.MATLAB_SCALAR_TAG,
                 new MatlabScalarConstruct());
        register(MatlabYAMLConstants.MATLAB_EXCEPTION_TAG,
                 new MatlabExceptionConstruct());
        register(MatlabYAMLConstants.MATLAB_CELL_TAG,
                 new MatlabCellConstruct());
        register(MatlabYAMLConstants.MATLAB_STRUCT_TAG,
                 new MatlabStructConstruct());
        register(MatlabYAMLConstants.MATLAB_REQUEST_TAG,
                 new MatlabRequestConstruct());
        register(MatlabYAMLConstants.MATLAB_STRING_TAG,
                 new MatlabStringConstruct());
        register(MatlabYAMLConstants.MATLAB_ARRAY_TAG,
                 new MatlabArrayConstruct());
        register(MatlabYAMLConstants.MATLAB_RESULT_TAG,
                 new MatlabResultConstruct());
    }

    private void register(Tag tag, Construct construct) {
        this.yamlConstructors.put(tag, construct);
    }

    private class MatlabMatrixConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            List<? extends Object> list = (List<? extends Object>) constructAs(Tag.SEQ, node);
            ArrayList<Double[]> matrix = Lists.newArrayListWithExpectedSize(list.size());
            for (Object o1 : list) {
                List<? extends Object> list2 = (List<? extends Object>) o1;
                ArrayList<Double> array = Lists.newArrayListWithExpectedSize(list2.size());
                for (Object o2 : list2) {
                    Double value = null;
                    if (o2 instanceof Number) {
                        value = ((Number) o2).doubleValue();
                    } else if (o2 instanceof String) {
                        value = Double.parseDouble((String) o2);
                    }
                    array.add(value);
                }
                matrix.add(array.toArray(new Double[array.size()]));
            }
            return new MatlabMatrix(matrix.toArray(new Double[matrix.size()][]));
        }

        private Object constructAs(Tag tag, Node node) {
            Tag tmp = node.getTag();
            node.setTag(tag);
            Construct delegate = getConstructor(node);
            node.setTag(tmp);
            return delegate.construct(node);
        }
    }

    private class MatlabScalarConstruct extends AbstractConstruct {
        @Override
        public MatlabScalar construct(Node node) {
            ScalarNode snode = (ScalarNode) node;
            Object scalar = constructScalar(snode);
            Double value = null;
            if (scalar instanceof Number) {
                value = ((Number) scalar).doubleValue();
            } else if (scalar instanceof String) {
                value = Double.parseDouble((String) scalar);
            }
            return new MatlabScalar(value);
        }
    }

    private class MatlabExceptionConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            Map<Object, Object> map = constructMapping(mnode);
            String message = (String) map.get(MatlabYAMLConstants.MESSAGE_KEY);
            return new MatlabException(message);
        }
    }

    private class MatlabCellConstruct extends AbstractConstruct {
        @Override
        public MatlabCell construct(Node node) {
            SequenceNode snode = (SequenceNode) node;
            List<? extends Object> list = constructSequence(snode);
            return new MatlabCell(list.toArray(new MatlabValue[list.size()]));
        }
    }

    private class MatlabStructConstruct extends AbstractConstruct {
        @Override
        public MatlabStruct construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            Map<Object, Object> map = constructMapping(mnode);
            MatlabStruct struct = new MatlabStruct();
            for (Entry<Object, Object> e : map.entrySet()) {
                String key = (String) e.getKey();
                MatlabValue value = (MatlabValue) e.getValue();
                struct.setField(key, value);
            }
            return struct;
        }
    }

    private class MatlabRequestConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            Map<Object, Object> map = constructMapping(mnode);
            MatlabRequest req = new MatlabRequest((String) map
                    .get(MatlabYAMLConstants.FUNCTION_KEY));
            if (map.containsKey(MatlabYAMLConstants.RESULT_COUNT_KEY)) {
                Object resultCount = map.get(MatlabYAMLConstants.RESULT_COUNT_KEY);
                req.setResultCount(((Integer) resultCount).intValue());
            }
            Collection<?> parameters = (Collection<?>) map
                    .get(MatlabYAMLConstants.PARAMETERS_KEY);
            for (Object o : parameters) {
                MatlabValue parameter = (MatlabValue) o;
                req.addParameter(parameter);
            }
            return req;
        }
    }

    private class MatlabStringConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            ScalarNode snode = (ScalarNode) node;
            String value = (String) constructScalar(snode);
            return new MatlabString(value);
        }
    }

    private class MatlabArrayConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            SequenceNode snode = (SequenceNode) node;
            List<? extends Object> list = constructSequence(snode);
            ArrayList<Double> value
                    = Lists.newArrayListWithExpectedSize(list.size());
            for (Object o : list) {
                if (o instanceof String) {
                    value.add(Double.valueOf((String) o));
                } else if (o instanceof Number) {
                    value.add(((Number) o).doubleValue());
                }
            }
            return new MatlabArray(value.toArray(new Double[list.size()]));
        }
    }

    private class MatlabResultConstruct extends AbstractConstruct {
        @Override
        public Object construct(Node node) {
            SequenceNode snode = (SequenceNode) node;
            List<? extends Object> seq = constructSequence(snode);
            MatlabResult result = new MatlabResult();
            for (Object o : seq) {
                MatlabValue value = (MatlabValue) o;
                result.addResult(value);
            }
            return result;
        }
    }

}
