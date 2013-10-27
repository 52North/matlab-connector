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
package com.github.autermann.matlab.yaml.construct;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.github.autermann.matlab.yaml.MatlabYAMLConstants;

/**
 * TODO JavaDvaluec
 *
 * @author Christian Autermann <c.autermann@52nvaluerth.valuerg>
 */
public class MatlabConstructor extends SafeConstructor {

    public MatlabConstructor() {
        register(MatlabYAMLConstants.MATLAB_MATRIX_TAG,
                 new MatlabMatrixConstruct(this));
        register(MatlabYAMLConstants.MATLAB_SCALAR_TAG,
                 new MatlabScalarConstruct(this));
        register(MatlabYAMLConstants.MATLAB_EXCEPTION_TAG,
                 new MatlabExceptionConstruct(this));
        register(MatlabYAMLConstants.MATLAB_CELL_TAG,
                 new MatlabCellConstruct(this));
        register(MatlabYAMLConstants.MATLAB_STRUCT_TAG,
                 new MatlabStructConstruct(this));
        register(MatlabYAMLConstants.MATLAB_REQUEST_TAG,
                 new MatlabRequestConstruct(this));
        register(MatlabYAMLConstants.MATLAB_STRING_TAG,
                 new MatlabStringConstruct(this));
        register(MatlabYAMLConstants.MATLAB_ARRAY_TAG,
                 new MatlabArrayConstruct(this));
        register(MatlabYAMLConstants.MATLAB_RESULT_TAG,
                 new MatlabResultConstruct(this));
        register(MatlabYAMLConstants.MATLAB_BOOLEAN_TAG,
                 new MatlabBooleanConstruct(this));
    }

    private void register(Tag tag, Construct construct) {
        this.yamlConstructors.put(tag, construct);
    }

    public List<? extends Object> constructSequence(Node node) {
        if (node instanceof SequenceNode) {
            return constructSequence((SequenceNode) node);
        }
        throw new IllegalArgumentException();
    }

    public Object constructScalar(Node node) {
        if (node instanceof ScalarNode) {
            return constructScalar((ScalarNode) node);
        }
        throw new IllegalArgumentException();
    }

    public Map<Object, Object> constructMapping(Node node) {
        if (node instanceof MappingNode) {
            return constructMapping((MappingNode) node);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Construct getConstructor(Node node) {
        return super.getConstructor(node);
    }
}
