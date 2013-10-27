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

import org.yaml.snakeyaml.nodes.Node;

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.value.MatlabValue;
import com.github.autermann.matlab.yaml.MatlabYAMLConstants;

/**
 * TODO JavaDoc
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabRequestConstruct extends MatlabConstruct {

    public MatlabRequestConstruct(MatlabConstructor delegate) {
        super(delegate);
    }

    @Override
    public Object construct(Node node) {
        Map<Object, Object> map = constructMapping(node);
        String function = constructString(
                map.get(MatlabYAMLConstants.FUNCTION_KEY));
        MatlabRequest req = new MatlabRequest(function);
        if (map.containsKey(MatlabYAMLConstants.RESULT_COUNT_KEY)) {
            req.setResultCount(constructInteger(
                    map.get(MatlabYAMLConstants.RESULT_COUNT_KEY)));
        }
        List<MatlabValue> parameters = constructValueList(
                        map.get(MatlabYAMLConstants.PARAMETERS_KEY));
        for (MatlabValue parameter : parameters) {
            req.addParameter(parameter);
        }
        return req;
    }

}
