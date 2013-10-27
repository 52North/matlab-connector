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

import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.nodes.Node;

import com.github.autermann.matlab.value.MatlabStruct;

/**
 * TODO JavaDoc
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MatlabStructConstruct extends MatlabConstruct {

    public MatlabStructConstruct(MatlabConstructor delegate) {
        super(delegate);
    }

    @Override
    public MatlabStruct construct(Node node) {
        Map<Object, Object> map = constructMapping(node);
        MatlabStruct struct = new MatlabStruct();
        for (Entry<Object, Object> e : map.entrySet()) {
            struct.setField(constructString(e.getKey()),
                            constructValue(e.getValue()));
        }
        return struct;
    }

}
