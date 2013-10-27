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

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public abstract class TypeSafeRepresent<T> implements Represent {

    private final MatlabRepresenter delegate;
    private final Class<T> type;

    protected TypeSafeRepresent(MatlabRepresenter delegate, Class<T> type) {
        this.type = Preconditions.checkNotNull(type);
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Node representData(Object data) {
        Preconditions.checkNotNull(data);
        if (type.isAssignableFrom(data.getClass())) {
            return represent(type.cast(data));
        } else {
            throw new IllegalArgumentException(
                    data + " is not instance of " + type);
        }
    }

    protected Node delegate(Tag tag, Object data) {
        Node node = delegate(data);
        node.setTag(tag);
        return node;
    }

    protected Node delegate(Object data) {
        return this.delegate.delegate(data);
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
        if (delegate.getDefaultFlowStyle() != FlowStyle.AUTO) {
            return delegate.getDefaultFlowStyle().getStyleBoolean();
        } else {
            return style;
        }
    }

    public Class<T> getType() {
        return type;
    }

    protected abstract Node represent(T t);

}
