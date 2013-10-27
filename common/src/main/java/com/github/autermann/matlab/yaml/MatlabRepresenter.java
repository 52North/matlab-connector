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

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.value.MatlabArray;
import com.github.autermann.matlab.value.MatlabCell;
import com.github.autermann.matlab.value.MatlabMatrix;
import com.github.autermann.matlab.value.MatlabScalar;
import com.github.autermann.matlab.value.MatlabString;
import com.github.autermann.matlab.value.MatlabStruct;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
class MatlabRepresenter extends Representer {

    MatlabRepresenter() {
        register(new MatlabStructRepresent());
        register(new MatlabStringRepresent());
        register(new MatlabScalarRepresent());
        register(new MatlabCellRepresent());
        register(new MatlabArrayRepresent());
        register(new MatlabMatrixRepresent());
        register(new MatlabRequestRepresent());
        register(new MatlabResultRepresent());
        register(new MatlabExceptionRepresent());
    }

    private void register(TypeSafeRepresent<?> represent) {
        this.representers.put(represent.getType(), represent);
    }

    private abstract class TypeSafeRepresent<T> implements Represent {
        private final Class<T> type;

        protected TypeSafeRepresent(Class<T> type) {
            this.type = Preconditions.checkNotNull(type);
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public Node representData(Object data) {
            Preconditions.checkNotNull(data);
            if (type.isAssignableFrom(data.getClass())) {
                return represent(type.cast(data));
            } else {
                throw new IllegalArgumentException(data + " is not instance of " +
                                                   type);
            }
        }

        protected Node delegate(Tag tag, Object data) {
            Node node = delegate(data);
            node.setTag(tag);
            return node;
        }

        protected Node delegate(Object data) {
            return MatlabRepresenter.this.representData(data);
        }

        protected Boolean getFlowStyleForTuples(
                Iterable<? extends NodeTuple> tuples) {
            return getFlowStyleForNodes(Iterables.concat(Iterables
                    .transform(tuples, new Function<NodeTuple, Node>() {
                        @Override
                        public Node apply(NodeTuple input) {
                            return input.getKeyNode();
                        }
                    }), Iterables
                    .transform(tuples, new Function<NodeTuple, Node>() {
                        @Override
                        public Node apply(NodeTuple input) {
                            return input.getValueNode();
                        }
                    })));
        }

        protected Boolean getFlowStyleForNodes(
                Iterable<? extends Node> nodes) {
            Boolean style = true;
            for (Node node : nodes) {
                if (node instanceof ScalarNode) {
                    ScalarNode scalarNode = (ScalarNode) node;
                    if (scalarNode.getStyle() != null) {
                        style = false;
                        break;
                    }
                }
            }
            if (getDefaultFlowStyle() != FlowStyle.AUTO) {
                return getDefaultFlowStyle().getStyleBoolean();
            } else {
                return style;
            }
        }

        public Class<T> getType() {
            return type;
        }

        protected abstract Node represent(T t);
    }

    private class MatlabStructRepresent extends TypeSafeRepresent<MatlabStruct> {
        MatlabStructRepresent() {
            super(MatlabStruct.class);
        }

        @Override
        protected Node represent(MatlabStruct struct) {
            return delegate(MatlabYAMLConstants.MATLAB_STRUCT_TAG,
                            struct.getFields());
        }
    }

    private class MatlabStringRepresent extends TypeSafeRepresent<MatlabString> {
        MatlabStringRepresent() {
            super(MatlabString.class);
        }

        @Override
        protected Node represent(MatlabString t) {
            return delegate(MatlabYAMLConstants.MATLAB_STRING_TAG, t.getString());
        }
    }

    private class MatlabScalarRepresent extends TypeSafeRepresent<MatlabScalar> {
        MatlabScalarRepresent() {
            super(MatlabScalar.class);
        }

        @Override
        protected Node represent(MatlabScalar t) {
            return delegate(MatlabYAMLConstants.MATLAB_SCALAR_TAG, t.getScalar());
        }
    }

    private class MatlabCellRepresent extends TypeSafeRepresent<MatlabCell> {
        MatlabCellRepresent() {
            super(MatlabCell.class);
        }

        @Override
        protected Node represent(MatlabCell t) {
            return delegate(MatlabYAMLConstants.MATLAB_CELL_TAG, t.getCell());
        }
    }

    private class MatlabArrayRepresent extends TypeSafeRepresent<MatlabArray> {
        MatlabArrayRepresent() {
            super(MatlabArray.class);
        }

        @Override
        protected Node represent(MatlabArray t) {
            return delegate(MatlabYAMLConstants.MATLAB_ARRAY_TAG, t.getArray());
        }
    }

    private class MatlabMatrixRepresent extends TypeSafeRepresent<MatlabMatrix> {
        MatlabMatrixRepresent() {
            super(MatlabMatrix.class);
        }

        @Override
        protected Node represent(MatlabMatrix t) {
            return delegate(MatlabYAMLConstants.MATLAB_MATRIX_TAG, t.getMatrix());
        }
    }

    private class MatlabResultRepresent extends TypeSafeRepresent<MatlabResult> {
        MatlabResultRepresent() {
            super(MatlabResult.class);
        }

        @Override
        protected Node represent(MatlabResult t) {
            return delegate(MatlabYAMLConstants.MATLAB_RESULT_TAG, t.iterator());
        }
    }

    private class MatlabRequestRepresent extends TypeSafeRepresent<MatlabRequest> {
        MatlabRequestRepresent() {
            super(MatlabRequest.class);
        }

        @Override
        protected Node represent(MatlabRequest t) {
            ArrayList<NodeTuple> nodes = Lists.newArrayList(
                    new NodeTuple(delegate(MatlabYAMLConstants.FUNCTION_KEY),
                                  delegate(t.getFunction())),
                    new NodeTuple(delegate(MatlabYAMLConstants.PARAMETERS_KEY),
                                  delegate(t.getParameters())),
                    new NodeTuple(delegate(MatlabYAMLConstants.RESULT_COUNT_KEY),
                                  delegate(t.getResultCount())));
            return new MappingNode(MatlabYAMLConstants.MATLAB_REQUEST_TAG,
                                   nodes, getFlowStyleForTuples(nodes));
        }
    }

    private class MatlabExceptionRepresent extends TypeSafeRepresent<MatlabException> {
        MatlabExceptionRepresent() {
            super(MatlabException.class);
        }

        @Override
        protected Node represent(MatlabException t) {
            ArrayList<NodeTuple> nodes = Lists.newArrayList(
                    new NodeTuple(delegate(MatlabYAMLConstants.MESSAGE_KEY),
                                  delegate(t.getMessage())));
            return new MappingNode(MatlabYAMLConstants.MATLAB_EXCEPTION_TAG,
                                   nodes, getFlowStyleForTuples(nodes));
        }
    }
}