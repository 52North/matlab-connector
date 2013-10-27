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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.github.autermann.matlab.value.MatlabValue;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public abstract class MatlabConstruct extends AbstractConstruct {

    private final MatlabConstructor delegate;

    public MatlabConstruct(MatlabConstructor delegate) {
        this.delegate = delegate;
    }

    protected Object constructAs(Tag tag, Node node) {
        Tag tmp = node.getTag();
        node.setTag(tag);
        Construct construct = this.delegate.getConstructor(node);
        node.setTag(tmp);
        return construct.construct(node);
    }

    protected List<? extends Object> constructSequence(Node node) {
        return delegate.constructSequence((SequenceNode) node);
    }

    protected Object constructScalar(Node node) {
        return delegate.constructScalar((ScalarNode) node);
    }

    protected Map<Object, Object> constructMapping(Node node) {
        return delegate.constructMapping((MappingNode) node);
    }

    protected int constructInteger(Object resultCount) {
        if (resultCount instanceof Number) {
            return ((Number) resultCount).intValue();
        }
        throw new IllegalArgumentException();
    }

    protected Double constructDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return Double.parseDouble((String) o);
        }
        throw new IllegalArgumentException();
    }

    protected Double constructDouble(Node node) {
        return constructDouble(constructScalar(node));
    }

    protected Double[] constructDoubleArray(Node node) {
        return constructDoubleArray(constructSequence(node));
    }

    protected Double[] constructDoubleArray(Object o) {
        if (o instanceof List) {
            return constructDoubleArray((List<? extends Object>) o);
        }
        throw new IllegalArgumentException();
    }

    protected Double[] constructDoubleArray(List<? extends Object> list) {
        ArrayList<Double> value
                = Lists.newArrayListWithExpectedSize(list.size());
        for (Object o : list) {
            value.add(constructDouble(o));
        }
        return value.toArray(new Double[list.size()]);
    }

    protected Double[][] constructDoubleMatrix(Node node) {
        List<? extends Object> list = constructSequence(node);
        ArrayList<Double[]> matrix
                = Lists.newArrayListWithExpectedSize(list.size());
        for (Object o1 : list) {
            matrix.add(constructDoubleArray(o1));
        }
        return matrix.toArray(new Double[matrix.size()][]);
    }

    protected String constructString(Node n) {
        Object scalar = constructScalar(n);
        return constructString(scalar);
    }

    protected String constructString(Object scalar) {
        if (scalar instanceof String) {
            return (String) scalar;
        }
        throw new IllegalArgumentException();
    }

    protected MatlabValue constructValue(Object o) {
        if (o instanceof MatlabValue) {
            return (MatlabValue) o;
        }
        throw new IllegalArgumentException();
    }

    protected List<MatlabValue> constructValueList(Node node) {
        List<? extends Object> seq = constructSequence(node);
        return constructValueList(seq);
    }

    protected List<MatlabValue> constructValueList(List<? extends Object> seq) {
        List<MatlabValue> result = Lists.newArrayListWithExpectedSize(seq.size());
        for (Object o : seq) {
            result.add(constructValue(o));
        }
        return result;
    }

    protected List<MatlabValue> constructValueList(Object o) {
        if (o instanceof List) {
            return constructValueList((List<? extends Object>) o);
        }
        throw new IllegalArgumentException();
    }

    protected MatlabValue[] constructValueArray(Node node) {
        List<MatlabValue> values = constructValueList(node);
        return values.toArray(new MatlabValue[values.size()]);
    }

}
