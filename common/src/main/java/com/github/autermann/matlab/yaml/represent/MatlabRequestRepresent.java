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
package com.github.autermann.matlab.yaml.represent;

import java.util.ArrayList;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.yaml.MatlabYAMLConstants;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MatlabRequestRepresent extends TypeSafeRepresent<MatlabRequest> {

    public MatlabRequestRepresent(MatlabRepresenter delegate) {
        super(delegate, MatlabRequest.class);
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
