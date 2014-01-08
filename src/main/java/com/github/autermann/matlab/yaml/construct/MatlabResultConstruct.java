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

import com.github.autermann.matlab.MatlabResult;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabResultConstruct extends MatlabConstruct {

    public MatlabResultConstruct(MatlabConstructor delegate) {
        super(delegate);
    }

    @Override
    public Object construct(Node node) {
        MatlabResult result = new MatlabResult();
        Map<Object, Object> map = constructMapping(node);
        for (Entry<Object, Object> e : map.entrySet()) {
            result.addResult(constructString(e.getKey()),
                             constructValue(e.getValue()));
        }
        return result;
    }

}